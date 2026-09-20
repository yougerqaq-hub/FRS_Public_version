package com.example.FBS.mapper;

import com.example.FBS.entity.Budget;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BudgetMapper {

    // 查询所有预算
    @Select("""
            SELECT *
            FROM budget
            ORDER BY year DESC, id DESC
            """)
    List<Budget> findAll();

    @Select("SELECT DISTINCT category FROM budget ORDER BY category")
    List<String> findCategories();

    // 新增预算
    @Insert("""
            INSERT INTO budget
            (year, category, budget_amount, used_amount)
            VALUES
            (#{year}, #{category}, #{budgetAmount}, #{usedAmount})
            """)
    int addBudget(Budget budget);

    @Select("""
        SELECT *
        FROM budget
        WHERE id = #{id}
        """)
    Budget findById(Long id);

    @Update("""
        UPDATE budget
        SET used_amount = used_amount + #{amount}
        WHERE id = #{id}
        """)
    int addUsedAmount(
            @Param("id") Long id,
            @Param("amount") BigDecimal amount
    );
    @Update("UPDATE budget SET budget_amount = #{budgetAmount} WHERE id = #{id}")
    int updateBudgetAmount(@Param("id") Long id, @Param("budgetAmount") BigDecimal budgetAmount);
    @Select("""
        SELECT *
        FROM budget
        WHERE year = #{year}
        AND category = #{category}
        LIMIT 1
        """)
    Budget findByYearAndCategory(
            @Param("year") Integer year,
            @Param("category") String category
    );
}
