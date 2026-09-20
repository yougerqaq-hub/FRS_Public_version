package com.example.FBS.controller;

import com.example.FBS.common.Result;
import com.example.FBS.entity.ReimbursementAudit;
import com.example.FBS.service.ReimbursementAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reimbursement-audit")
@CrossOrigin(origins = "http://localhost:8081")
public class ReimbursementAuditController {

    @Autowired
    private ReimbursementAuditService reimbursementAuditService;
    @Autowired
    private com.example.FBS.service.SessionService sessionService;

    // 查询某笔报销的审核记录
    @GetMapping("/reimbursement/{reimbursementId}")
    public Result<List<ReimbursementAudit>> findByReimbursementId(
            @PathVariable Long reimbursementId, @RequestHeader(value = "X-Session-Token", required = false) String token) {
        if (!sessionService.hasRole(token, "ADMIN", "FINANCE")) return Result.error(403, "无权查看审核记录");

        List<ReimbursementAudit> list =
                reimbursementAuditService
                        .findByReimbursementId(reimbursementId);

        return Result.success("查询成功", list);
    }

    // 审核报销
    @PostMapping
    public Result<String> auditReimbursement(
            @RequestParam Long reimbursementId,
            @RequestParam String result,
            @RequestParam String comment,
            @RequestHeader(value = "X-Session-Token", required = false) String token) {
        if (!sessionService.hasRole(token, "ADMIN", "FINANCE")) return Result.error(403, "无权审核");
        Long auditorId = sessionService.get(token).getId();

        boolean success;
        try {
            success = reimbursementAuditService.auditReimbursement(reimbursementId, auditorId, result, comment);
        } catch (IllegalStateException exception) {
            return Result.error(exception.getMessage());
        }

        if (success) {
            return Result.success("审核成功", null);
        }

        return Result.error("审核失败");
    }
}
