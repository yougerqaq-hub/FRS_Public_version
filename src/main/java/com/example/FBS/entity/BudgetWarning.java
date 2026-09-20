package com.example.FBS.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// NORMAL    正常
// NOTICE    提醒
// WARNING   警告
// OVER      超出
public class BudgetWarning {

    private Long id;
    private Long budgetId;
    private String warningLevel;
    private BigDecimal usageRate;
    private LocalDateTime warningTime;

    // 预算信息
    private Integer year;
    private String category;
    private BigDecimal budgetAmount;
    private BigDecimal usedAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public String getWarningLevel() {
        return warningLevel;
    }

    public void setWarningLevel(String warningLevel) {
        this.warningLevel = warningLevel;
    }

    public BigDecimal getUsageRate() {
        return usageRate;
    }

    public void setUsageRate(BigDecimal usageRate) {
        this.usageRate = usageRate;
    }

    public LocalDateTime getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(LocalDateTime warningTime) {
        this.warningTime = warningTime;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount) {
        this.usedAmount = usedAmount;
    }
}
