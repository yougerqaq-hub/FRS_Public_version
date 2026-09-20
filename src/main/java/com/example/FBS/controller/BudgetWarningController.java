package com.example.FBS.controller;

import com.example.FBS.common.Result;
import com.example.FBS.entity.BudgetWarning;
import com.example.FBS.service.BudgetWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budget-warning")
@CrossOrigin(origins = "http://localhost:8081")
public class BudgetWarningController {

    @Autowired
    private com.example.FBS.service.SessionService sessionService;

    @Autowired
    private BudgetWarningService budgetWarningService;

    // 查询所有预算预警
    @GetMapping
    public Result<List<BudgetWarning>> findAll(@RequestHeader(value = "X-Session-Token", required = false) String token) {
        if (!sessionService.hasRole(token, "ADMIN", "FINANCE")) return Result.error(403, "无权查看预算预警");
        List<BudgetWarning> list =
                budgetWarningService.findAll();
        return Result.success("查询成功", list);
    }

    // 重新计算指定预算的预警
    @PostMapping("/{budgetId}")
    public Result<BudgetWarning> generateWarning(
            @PathVariable Long budgetId,
            @RequestHeader(value = "X-Session-Token", required = false) String token) {
        if (!sessionService.hasRole(token, "ADMIN", "FINANCE")) return Result.error(403, "无权计算预算预警");
        BudgetWarning warning =
                budgetWarningService.generateWarning(budgetId);
        if (warning == null) {
            return Result.notFound("预算不存在");
        }
        return Result.success("预警计算成功", warning);
    }
}
