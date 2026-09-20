package com.example.FBS.controller;

import com.example.FBS.common.Result;
import com.example.FBS.entity.Budget;
import com.example.FBS.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budget")
@CrossOrigin(origins = "http://localhost:8081")
public class BudgetController {

    @Autowired
    private com.example.FBS.service.SessionService sessionService;

    @Autowired
    private BudgetService budgetService;

    // 查询所有预算
    @GetMapping
    public Result<List<Budget>> findAll(@RequestHeader(value = "X-Session-Token", required = false) String token) {
        if (!sessionService.hasRole(token, "ADMIN", "FINANCE")) return Result.error(403, "无权查看预算");

        List<Budget> list =
                budgetService.findAll();

        return Result.success("查询成功", list);
    }

    // 新增预算
    @PostMapping
    public Result<String> addBudget(
            @RequestBody Budget budget,
            @RequestHeader(value = "X-Session-Token", required = false) String token) {
        if (!sessionService.hasRole(token, "ADMIN")) return Result.error(403, "仅管理员可新增预算");

        int result =
                budgetService.addBudget(budget);

        if (result > 0) {
            return Result.success("预算新增成功", null);
        }

        return Result.error("预算新增失败");
    }

    @GetMapping("/categories")
    public Result<List<String>> findCategories(@RequestHeader(value = "X-Session-Token", required = false) String token) {
        if (sessionService.get(token) == null) return Result.error(401, "登录已失效");
        return Result.success("查询成功", budgetService.findCategories());
    }

    @PostMapping("/{id}/adjust")
    public Result<String> adjustBudget(@PathVariable Long id, @RequestParam java.math.BigDecimal amount,
                                       @RequestParam boolean increase,
                                       @RequestHeader(value = "X-Session-Token", required = false) String token) {
        if (!sessionService.hasRole(token, "ADMIN")) return Result.error(403, "仅管理员可调整预算");
        int changed = budgetService.adjustBudget(id, amount, increase);
        return changed > 0 ? Result.success("预算调整成功", null) : Result.error("调整失败：削减后的预算不能低于已使用金额，且金额必须大于 0");
    }
}
