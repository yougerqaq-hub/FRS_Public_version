package com.example.FBS.mapper;

import com.example.FBS.entity.BudgetWarning;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BudgetWarningMapper {

    // 查询所有预算预警，同时查询对应的预算信息
    @Select("""
            SELECT
                bw.id,
                bw.budget_id,
                bw.warning_level,
                bw.usage_rate,
                bw.warning_time,
                b.year,
                b.category,
                b.budget_amount,
                b.used_amount
            FROM budget_warning bw
            LEFT JOIN budget b
                ON bw.budget_id = b.id
            ORDER BY bw.warning_time DESC
            """)
    List<BudgetWarning> findAll();

    // 根据预算ID查询预警
    @Select("""
            SELECT *
            FROM budget_warning
            WHERE budget_id = #{budgetId}
            """)
    BudgetWarning findByBudgetId(Long budgetId);

    // 新增预算预警
    @Insert("""
            INSERT INTO budget_warning
            (budget_id, warning_level, usage_rate, warning_time)
            VALUES
            (#{budgetId}, #{warningLevel}, #{usageRate}, #{warningTime})
            """)
    int addWarning(BudgetWarning warning);

    // 更新预算预警
    @Update("""
            UPDATE budget_warning
            SET warning_level = #{warningLevel},
                usage_rate = #{usageRate},
                warning_time = #{warningTime}
            WHERE budget_id = #{budgetId}
            """)
    int updateWarning(BudgetWarning warning);
}
