package com.example.FBS.mapper;

import com.example.FBS.entity.ReimbursementAudit;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReimbursementAuditMapper {

    // 查询某笔报销的审核记录
    @Select("""
            SELECT *
            FROM reimbursement_audit
            WHERE reimbursement_id = #{reimbursementId}
            ORDER BY audit_time DESC
            """)
    List<ReimbursementAudit> findByReimbursementId(Long reimbursementId);

    // 新增审核记录
    @Insert("""
            INSERT INTO reimbursement_audit
            (reimbursement_id, auditor_id, result, comment)
            VALUES
            (#{reimbursementId}, #{auditorId}, #{result}, #{comment})
            """)
    int addAudit(ReimbursementAudit audit);
}