package com.example.FBS.service;

import com.example.FBS.entity.BudgetWarning;

import java.util.List;

public interface BudgetWarningService {

    // 查询已有的预警记录
    List<BudgetWarning> findAll();

    // 根据预算信息计算并生成预警
    BudgetWarning generateWarning(Long budgetId);
}