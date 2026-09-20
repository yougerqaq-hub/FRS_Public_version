package com.example.FBS.service;

import com.example.FBS.entity.Reimbursement;

import java.util.List;

public interface ReimbursementService {

    // 查询某用户的报销记录
    List<Reimbursement> findByUserId(Long userId);

    // 查询全部报销记录
    List<Reimbursement> findAll();

    // 提交报销申请
    int addReimbursement(Reimbursement reimbursement);
}