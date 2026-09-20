package com.example.FBS.service;

import com.example.FBS.entity.Budget;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetService {

    // 查询所有预算
    List<Budget> findAll();

    List<String> findCategories();

    // 新增预算
    int addBudget(Budget budget);

    int adjustBudget(Long id, BigDecimal amount, boolean increase);
}
