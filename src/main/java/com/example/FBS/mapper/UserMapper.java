package com.example.FBS.mapper;

import com.example.FBS.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    // 登录
    @Select("""
            SELECT *
            FROM sys_user
            WHERE username = #{username}
            AND password = #{password}
            """)
    User login(
            @Param("username") String username,
            @Param("password") String password)
    ;

    // 根据ID查询用户
    @Select("""
            SELECT *
            FROM sys_user
            WHERE id = #{id}
            """)
    User findById(Long id);

    // 查询所有用户
    @Select("""
            SELECT *
            FROM sys_user
            ORDER BY id ASC
            """)
    List<User> findAll();

    // 新增用户
    @Insert("""
            INSERT INTO sys_user
            (
             username,
             password,
             real_name,
             role
            )
            VALUES
            (
             #{username},
             #{password},
             #{realName},
             #{role}
            )
            """)
    int addUser(User user);

    // 修改角色
    @Update("""
            UPDATE sys_user
            SET role = #{role}
            WHERE id = #{id}
            """)
    int updateRole(
            @Param("id") Long id,
            @Param("role") String role
    );

    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(Long id);

}
