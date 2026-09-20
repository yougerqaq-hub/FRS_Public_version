package com.example.FBS.service;

import com.example.FBS.entity.User;

import java.util.List;

public interface UserService {


    User login(String username, String password);
    // 查询用户
    List<User> findAll();
    // 新增用户
    int addUser(User user);
    // 修改角色
    int updateRole(Long id,String role);

    int deleteUser(Long id);

}
