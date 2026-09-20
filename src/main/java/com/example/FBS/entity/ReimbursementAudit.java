package com.example.FBS.entity;

import java.time.LocalDateTime;

//APPROVED      批准
//REJECTED      拒绝
public class ReimbursementAudit {
    //审核记录

    private Long id;
    private Long reimbursementId;   //--> Mysql reimbursement_audit.reimbursement_id
    private Long auditorId;
    private String result;
    private String comment;
    private LocalDateTime auditTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReimbursementId() {
        return reimbursementId;
    }

    public void setReimbursementId(Long reimbursementId) {
        this.reimbursementId = reimbursementId;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }
}