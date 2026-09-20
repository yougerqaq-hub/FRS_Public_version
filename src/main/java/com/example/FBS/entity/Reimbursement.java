package com.example.FBS.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//PENDING       待办
//APPROVED      批准
//REJECTED      拒绝
public class Reimbursement {

    private Long id;
    private Long userId;    //-->Mysql reimbursement.user_id    提交了这笔报销的用户
    private BigDecimal amount;  //BigDecimal用来表示金额，比double更适合处理财务数据
    private String category;
    private String description;
    private String status;
    private LocalDateTime applyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }
}