package com.example.FBS.mapper;

import com.example.FBS.entity.Reimbursement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReimbursementMapper {

    // 查询某个用户的报销记录
    @Select("""
            SELECT *
            FROM reimbursement
            WHERE user_id = #{userId}
            ORDER BY apply_time DESC
            """)
    List<Reimbursement> findByUserId(Long userId);

    // 查询全部报销记录
    @Select("""
            SELECT *
            FROM reimbursement
            ORDER BY apply_time DESC
            """)
    List<Reimbursement> findAll();

    // 提交报销申请
    @Insert("""
            INSERT INTO reimbursement
            (user_id, amount, category, description, status)
            VALUES
            (#{userId}, #{amount}, #{category}, #{description}, 'PENDING')
            """)
    int addReimbursement(Reimbursement reimbursement);

    // 根据报销ID查询报销
    @Select("""
            SELECT *
            FROM reimbursement
            WHERE id = #{id}
            """)
    Reimbursement findById(Long id);

    // 修改报销状态
    @Update("""
        UPDATE reimbursement
        SET status = #{status}
        WHERE id = #{id}
        """)
    int updateStatus(
            @Param("id") Long id,
            @Param("status") String status
    );
}