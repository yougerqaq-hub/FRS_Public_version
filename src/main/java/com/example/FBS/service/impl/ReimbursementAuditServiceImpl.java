package com.example.FBS.service.impl;

import com.example.FBS.entity.Budget;
import com.example.FBS.entity.BudgetWarning;
import com.example.FBS.entity.Reimbursement;
import com.example.FBS.entity.ReimbursementAudit;
import com.example.FBS.entity.User;
import com.example.FBS.mapper.BudgetMapper;
import com.example.FBS.mapper.BudgetWarningMapper;
import com.example.FBS.mapper.ReimbursementAuditMapper;
import com.example.FBS.mapper.ReimbursementMapper;
import com.example.FBS.mapper.UserMapper;
import com.example.FBS.service.ReimbursementAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReimbursementAuditServiceImpl implements ReimbursementAuditService {

    @Autowired
    private ReimbursementAuditMapper reimbursementAuditMapper;

    @Autowired
    private ReimbursementMapper reimbursementMapper;

    @Autowired
    private BudgetMapper budgetMapper;

    @Autowired
    private BudgetWarningMapper budgetWarningMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<ReimbursementAudit> findByReimbursementId(Long reimbursementId) {

        if (reimbursementId == null) {
            return List.of();
        }

        return reimbursementAuditMapper
                .findByReimbursementId(reimbursementId);
    }

    @Override
    public int addAudit(ReimbursementAudit audit) {

        if (audit == null) {
            return 0;
        }

        if (audit.getReimbursementId() == null) {
            return 0;
        }

        if (audit.getAuditorId() == null) {
            return 0;
        }

        if (audit.getResult() == null
                || audit.getResult().trim().isEmpty()) {
            return 0;
        }

        if (!"APPROVED".equals(audit.getResult())
                && !"REJECTED".equals(audit.getResult())) {
            return 0;
        }

        if (audit.getComment() == null
                || audit.getComment().trim().isEmpty()) {
            return 0;
        }

        return reimbursementAuditMapper.addAudit(audit);
    }

    @Override
    @Transactional
    public boolean auditReimbursement(
            Long reimbursementId,
            Long auditorId,
            String result,
            String comment) {

        // 1. 基础参数校验
        if (reimbursementId == null) {
            return false;
        }

        if (auditorId == null) {
            return false;
        }

        if (result == null || result.trim().isEmpty()) {
            return false;
        }

        if (!"APPROVED".equals(result)
                && !"REJECTED".equals(result)) {
            return false;
        }

        if (comment == null || comment.trim().isEmpty()) {
            return false;
        }
        // 2. 查询审核人
        User auditor = userMapper.findById(auditorId);

        // 审核人不存在
        if (auditor == null) {
            return false;
        }

        // 只有财务人员或管理员可以审核
        if (!"FINANCE".equals(auditor.getRole()) && !"ADMIN".equals(auditor.getRole())) {
            return false;
        }

        // 3. 查询报销
        Reimbursement reimbursement =
                reimbursementMapper.findById(reimbursementId);

        // 报销不存在
        if (reimbursement == null) {
            return false;
        }

        // 只有待审核状态才能审核
        if (!"PENDING".equals(reimbursement.getStatus())) {
            return false;
        }

        // 4. 审核通过前的预算检查
        Budget targetBudget = null;
        if ("APPROVED".equals(result)) {

            if (reimbursement.getAmount() == null
                    || reimbursement.getAmount()
                    .compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }

            // 从报销申请时间获得预算年份
            if (reimbursement.getApplyTime() == null) {
                return false;
            }

            Integer year = reimbursement
                    .getApplyTime()
                    .getYear();

            // 根据年份和报销类型寻找预算
            targetBudget =
                    budgetMapper.findByYearAndCategory(
                            year,
                            reimbursement.getCategory()
                    );

            // 没有对应预算
            if (targetBudget == null) {
                throw new IllegalStateException("当前年度未配置「" + reimbursement.getCategory() + "」预算，请先在预算管理中补充该类别预算");
            }

            if (targetBudget.getBudgetAmount() == null || targetBudget.getBudgetAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("该报销类别的预算金额必须大于 0");
            }

            if (targetBudget.getUsedAmount() == null) targetBudget.setUsedAmount(BigDecimal.ZERO);

            // 计算新的已使用金额
            BigDecimal newUsedAmount =
                    targetBudget.getUsedAmount()
                            .add(reimbursement.getAmount());

            // 防止预算使用金额出现非法数据
            if (newUsedAmount.compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
        }

        // 5. 创建审核记录
        ReimbursementAudit audit =
                new ReimbursementAudit();
        audit.setReimbursementId(reimbursementId);
        audit.setAuditorId(auditorId);
        audit.setResult(result);
        audit.setComment(comment.trim());
        audit.setAuditTime(LocalDateTime.now());
        int auditResult =
                reimbursementAuditMapper.addAudit(audit);
        if (auditResult <= 0) {
            return false;
        }

        // 6. 修改报销状态
        int statusResult =
                reimbursementMapper.updateStatus(
                        reimbursementId,
                        result
                );
        if (statusResult <= 0) {
            return false;
        }

        // 7. 审核通过 → 更新预算
        if ("APPROVED".equals(result)) {
            int budgetResult =
                    budgetMapper.addUsedAmount(
                            targetBudget.getId(),
                            reimbursement.getAmount()
                    );
            if (budgetResult <= 0) {
                return false;
            }

            // 8. 重新计算预算使用率
            BigDecimal newUsedAmount =
                    targetBudget.getUsedAmount()
                            .add(reimbursement.getAmount());

            BigDecimal usageRate = newUsedAmount.divide(targetBudget.getBudgetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);

            // 9. 判断预警等级
            String warningLevel;
            if (usageRate.compareTo(
                    new BigDecimal("70")) < 0) {
                warningLevel = "NORMAL";
            } else if (usageRate.compareTo(
                    new BigDecimal("90")) < 0) {
                warningLevel = "NOTICE";
            } else if (usageRate.compareTo(
                    new BigDecimal("100")) < 0) {
                warningLevel = "WARNING";
            } else {
                warningLevel = "OVER";
            }

            // 10. 创建新的预警对象
            BudgetWarning warning =
                    new BudgetWarning();
            warning.setBudgetId(targetBudget.getId());
            warning.setWarningLevel(warningLevel);
            warning.setUsageRate(usageRate);
            warning.setWarningTime(LocalDateTime.now());

            // 11. 有记录就更新，没有就新增
            BudgetWarning oldWarning = budgetWarningMapper.findByBudgetId(targetBudget.getId());
            int warningResult;
            if (oldWarning == null) {
                warningResult =
                        budgetWarningMapper
                                .addWarning(warning);
            } else {
                warningResult =
                        budgetWarningMapper
                                .updateWarning(warning);
            }
            if (warningResult <= 0) {
                return false;
            }
        }
        return true;
    }
}
