package com.example.FBS;

import com.example.FBS.entity.BudgetWarning;
import com.example.FBS.service.BudgetWarningService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BudgetWarningServiceTest {

    @Autowired
    private BudgetWarningService budgetWarningService;

    @Test
    public void testGenerateWarning() {

        System.out.println("===== 开始测试预算预警 =====");

        // 测试5条预算数据
        Long[] budgetIds = {1L, 2L, 3L, 4L, 5L};

        for (Long budgetId : budgetIds) {

            BudgetWarning warning =
                    budgetWarningService.generateWarning(budgetId);

            if (warning != null) {
                System.out.println(
                        "预算ID：" + warning.getBudgetId()
                                + "，使用率：" + warning.getUsageRate() + "%"
                                + "，预警等级：" + warning.getWarningLevel()
                );
            } else {
                System.out.println(
                        "预算ID：" + budgetId + " 不存在"
                );
            }
        }

        System.out.println("===== 预算预警测试结束 =====");
    }
}