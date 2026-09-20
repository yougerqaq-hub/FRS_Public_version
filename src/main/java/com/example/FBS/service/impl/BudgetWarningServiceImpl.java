package com.example.FBS.service.impl;

import com.example.FBS.entity.Budget;
import com.example.FBS.entity.BudgetWarning;
import com.example.FBS.mapper.BudgetMapper;
import com.example.FBS.mapper.BudgetWarningMapper;
import com.example.FBS.service.BudgetWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BudgetWarningServiceImpl implements BudgetWarningService {

    @Autowired
    private BudgetMapper budgetMapper;

    @Autowired
    private BudgetWarningMapper budgetWarningMapper;

    @Override
    public List<BudgetWarning> findAll() {
        // Recalculate every budget before returning the view, so new/adjusted budgets are immediately reflected.
        for (Budget budget : budgetMapper.findAll()) {
            generateWarning(budget.getId());
        }
        return budgetWarningMapper.findAll();
    }

    @Override
    public BudgetWarning generateWarning(Long budgetId) {

        // 先查询所有预算
        List<Budget> budgetList = budgetMapper.findAll();

        Budget targetBudget = null;

        // 找到指定的预算
        for (Budget budget : budgetList) {
            if (budget.getId().equals(budgetId)) {
                targetBudget = budget;
                break;
            }
        }

        // 没找到预算
        if (targetBudget == null) {
            return null;
        }

        // 计算使用率
        if (targetBudget.getBudgetAmount() == null || targetBudget.getBudgetAmount().compareTo(BigDecimal.ZERO) <= 0) return null;
        BigDecimal usageRate = (targetBudget.getUsedAmount() == null ? BigDecimal.ZERO : targetBudget.getUsedAmount())
                .divide(targetBudget.getBudgetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);

        // 判断预警等级
        String warningLevel;

        if (usageRate.compareTo(new BigDecimal("70")) < 0) {
            warningLevel = "NORMAL";
        } else if (usageRate.compareTo(new BigDecimal("90")) < 0) {
            warningLevel = "NOTICE";
        } else if (usageRate.compareTo(new BigDecimal("100")) < 0) {
            warningLevel = "WARNING";
        } else {
            warningLevel = "OVER";
        }

        // 创建预警对象
        BudgetWarning warning = new BudgetWarning();

        warning.setBudgetId(budgetId);
        warning.setWarningLevel(warningLevel);
        warning.setUsageRate(usageRate);
        warning.setWarningTime(LocalDateTime.now());

        // 保存预警结果
        BudgetWarning oldWarning =
                budgetWarningMapper.findByBudgetId(budgetId);

        if (oldWarning == null) {
            budgetWarningMapper.addWarning(warning);
        } else {
            budgetWarningMapper.updateWarning(warning);
        }

        return warning;
    }
}
