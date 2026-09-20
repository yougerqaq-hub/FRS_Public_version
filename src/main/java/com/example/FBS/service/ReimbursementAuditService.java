package com.example.FBS.service;

import com.example.FBS.entity.ReimbursementAudit;

import java.util.List;

public interface ReimbursementAuditService {

    // 查询某笔报销的审核记录
    List<ReimbursementAudit> findByReimbursementId(Long reimbursementId);

    // 新增审核记录
    int addAudit(ReimbursementAudit audit);

    // 审核报销，!!!总业务方法!!!
    boolean auditReimbursement(
            Long reimbursementId,
            Long auditorId,
            String result,
            String comment
    );
}