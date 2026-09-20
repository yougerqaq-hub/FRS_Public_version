package com.example.FBS;

import com.example.FBS.entity.Budget;
import com.example.FBS.service.BudgetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class BudgetServiceTest {

    @Autowired
    private BudgetService budgetService;

    @Test
    public void testFindAll() {

        List<Budget> list = budgetService.findAll();

        System.out.println("===== 预算列表 =====");

        for (Budget budget : list) {
            System.out.println(
                    "预算ID：" + budget.getId()
                            + "，年份：" + budget.getYear()
                            + "，类型：" + budget.getCategory()
                            + "，预算金额：" + budget.getBudgetAmount()
                            + "，已使用：" + budget.getUsedAmount()
            );
        }
    }

    @Test
    public void testAddBudget() {

        Budget budget = new Budget();

        budget.setYear(2026);
        budget.setCategory("办公费");
        budget.setBudgetAmount(new BigDecimal("60000.00"));
        budget.setUsedAmount(new BigDecimal("10000.00"));

        int result = budgetService.addBudget(budget);

        if (result > 0) {
            System.out.println("===== 预算新增成功 =====");
        } else {
            System.out.println("===== 预算新增失败 =====");
        }
    }
}