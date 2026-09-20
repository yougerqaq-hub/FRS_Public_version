package com.example.FBS;

import com.example.FBS.entity.Budget;
import com.example.FBS.entity.Reimbursement;
import com.example.FBS.entity.ReimbursementAudit;
import com.example.FBS.service.ReimbursementAuditService;
import com.example.FBS.mapper.BudgetMapper;
import com.example.FBS.mapper.ReimbursementMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ReimbursementAuditServiceIntegrationTest {

    @Autowired
    private ReimbursementAuditService reimbursementAuditService;

    @Autowired
    private ReimbursementMapper reimbursementMapper;

    @Autowired
    private BudgetMapper budgetMapper;

    @Test
    public void testAuditReimbursement() {

        Long reimbursementId = 1L;
        Long auditorId = 2L;

        // 1. 查询审核前的报销
        Reimbursement beforeReimbursement =
                reimbursementMapper.findById(reimbursementId);

        System.out.println("===== 审核前报销 =====");
        System.out.println(
                "报销ID：" + beforeReimbursement.getId()
                        + "，金额：" + beforeReimbursement.getAmount()
                        + "，类型：" + beforeReimbursement.getCategory()
                        + "，状态：" + beforeReimbursement.getStatus()
        );

        // 2. 查询对应预算
        List<Budget> budgetsBefore = budgetMapper.findAll();

        System.out.println("===== 审核前预算 =====");

        Budget targetBudgetBefore = null;

        for (Budget budget : budgetsBefore) {
            if (budget.getCategory()
                    .equals(beforeReimbursement.getCategory())) {

                targetBudgetBefore = budget;

                System.out.println(
                        "预算ID：" + budget.getId()
                                + "，预算金额：" + budget.getBudgetAmount()
                                + "，已使用：" + budget.getUsedAmount()
                );

                break;
            }
        }

        // 3. 执行完整审核业务
        System.out.println("===== 开始审核 =====");

        boolean result = reimbursementAuditService.auditReimbursement(
                reimbursementId,
                auditorId,
                "APPROVED",
                "报销材料完整，符合报销规定"
        );

        System.out.println("审核业务执行结果：" + result);

        // 4. 查询审核后的报销
        Reimbursement afterReimbursement =
                reimbursementMapper.findById(reimbursementId);

        System.out.println("===== 审核后报销 =====");
        System.out.println(
                "报销ID：" + afterReimbursement.getId()
                        + "，金额：" + afterReimbursement.getAmount()
                        + "，类型：" + afterReimbursement.getCategory()
                        + "，状态：" + afterReimbursement.getStatus()
        );

        // 5. 查询审核记录
        List<ReimbursementAudit> audits =
                reimbursementAuditService
                        .findByReimbursementId(reimbursementId);

        System.out.println("===== 审核记录 =====");

        for (ReimbursementAudit audit : audits) {
            System.out.println(
                    "审核ID：" + audit.getId()
                            + "，审核人ID：" + audit.getAuditorId()
                            + "，结果：" + audit.getResult()
                            + "，意见：" + audit.getComment()
            );
        }

        // 6. 查询审核后的预算
        List<Budget> budgetsAfter = budgetMapper.findAll();

        System.out.println("===== 审核后预算 =====");

        for (Budget budget : budgetsAfter) {
            if (targetBudgetBefore != null
                    && budget.getId().equals(targetBudgetBefore.getId())) {

                System.out.println(
                        "预算ID：" + budget.getId()
                                + "，预算金额：" + budget.getBudgetAmount()
                                + "，已使用：" + budget.getUsedAmount()
                );

                break;
            }
        }
    }
}