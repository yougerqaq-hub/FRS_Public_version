package com.example.FBS.service.impl;

import com.example.FBS.entity.Budget;
import com.example.FBS.mapper.BudgetMapper;
import com.example.FBS.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetMapper budgetMapper;

    @Override
    public List<Budget> findAll() {
        return budgetMapper.findAll();
    }

    @Override
    public List<String> findCategories() {
        return budgetMapper.findCategories();
    }

    @Override
    public int addBudget(Budget budget) {

        if (budget == null) {
            return 0;
        }

        // 年份不能为空
        if (budget.getYear() == null) {
            return 0;
        }

        // 预算类型不能为空
        if (budget.getCategory() == null
                || budget.getCategory().trim().isEmpty()) {
            return 0;
        }

        // 预算金额不能为空
        if (budget.getBudgetAmount() == null) {
            return 0;
        }

        // 预算金额必须大于0
        if (budget.getBudgetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        // 已使用金额为空时默认为0
        if (budget.getUsedAmount() == null) {
            budget.setUsedAmount(BigDecimal.ZERO);
        }

        // 已使用金额不能小于0
        if (budget.getUsedAmount().compareTo(BigDecimal.ZERO) < 0) {
            return 0;
        }

        // 已使用金额不能大于预算金额
        if (budget.getUsedAmount()
                .compareTo(budget.getBudgetAmount()) > 0) {
            return 0;
        }

        return budgetMapper.addBudget(budget);
    }

    @Override
    public int adjustBudget(Long id, BigDecimal amount, boolean increase) {
        if (id == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return 0;
        Budget budget = budgetMapper.findById(id);
        if (budget == null || budget.getBudgetAmount() == null) return 0;
        BigDecimal current = budget.getBudgetAmount();
        BigDecimal next = increase ? current.add(amount) : current.subtract(amount);
        BigDecimal used = budget.getUsedAmount() == null ? BigDecimal.ZERO : budget.getUsedAmount();
        if (next.compareTo(used) < 0 || next.compareTo(BigDecimal.ZERO) <= 0) return 0;
        return budgetMapper.updateBudgetAmount(id, next);
    }
}
