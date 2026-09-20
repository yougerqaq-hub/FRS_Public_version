package com.example.FBS.controller;

import com.example.FBS.common.Result;
import com.example.FBS.entity.Reimbursement;
import com.example.FBS.service.ReimbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reimbursement")
@CrossOrigin(origins = "http://localhost:8081")
public class ReimbursementController {

    @Autowired
    private ReimbursementService reimbursementService;
    @Autowired
    private com.example.FBS.service.SessionService sessionService;

    // 查询某个用户的报销记录
    @GetMapping("/user/{userId}")
    public Result<List<Reimbursement>> findByUserId(
            @PathVariable Long userId, @RequestHeader(value = "X-Session-Token", required = false) String token) {
        com.example.FBS.entity.User user = sessionService.get(token);
        if (user == null || (!user.getId().equals(userId) && !sessionService.hasRole(token, "ADMIN", "FINANCE"))) return Result.error(403, "无权查看该报销记录");

        List<Reimbursement> list =
                reimbursementService.findByUserId(userId);

        return Result.success("查询成功", list);
    }

    // 查询全部报销记录
    @GetMapping("/all")
    public Result<List<Reimbursement>> findAll(@RequestHeader(value = "X-Session-Token", required = false) String token) {
        if (!sessionService.hasRole(token, "ADMIN", "FINANCE")) return Result.error(403, "无权查看全部报销记录");

        List<Reimbursement> list =
                reimbursementService.findAll();

        return Result.success("查询成功", list);
    }

    // 提交报销申请
    @PostMapping
    public Result<String> addReimbursement(
            @RequestBody Reimbursement reimbursement, @RequestHeader(value = "X-Session-Token", required = false) String token) {
        com.example.FBS.entity.User user = sessionService.get(token);
        if (user == null) return Result.error(401, "登录已失效");
        reimbursement.setUserId(user.getId());

        int result =
                reimbursementService.addReimbursement(reimbursement);

        if (result > 0) {
            return Result.success("报销申请提交成功", null);
        }

        return Result.error("报销申请提交失败");
    }
}
