package com.example.FBS.controller;


import com.example.FBS.common.Result;
import com.example.FBS.entity.LoginResult;
import com.example.FBS.entity.User;
import com.example.FBS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:8081")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private com.example.FBS.service.SessionService sessionService;

    // 登录
    @PostMapping("/login")
    public Result<LoginResult> login(
            @RequestBody User user){
        User loginUser =
                userService.login(
                        user.getUsername(),
                        user.getPassword()
                );
        if(loginUser==null){
            return Result.error("用户名或密码错误");
        }
        LoginResult result=new LoginResult();
        result.setId(loginUser.getId());
        result.setUsername(loginUser.getUsername());
        result.setRealName(loginUser.getRealName());
        result.setRole(loginUser.getRole());
        result.setToken(sessionService.create(loginUser));
        return Result.success(
                "登录成功",
                result
        );
    }

    // 查询所有用户
    @GetMapping("/list")
    public Result<List<User>> findAll(@RequestHeader(value = "X-Session-Token", required = false) String token){
        if (!sessionService.hasRole(token, "ADMIN")) return Result.error(403, "无权查看用户");
        return Result.success(
                "查询成功",
                userService.findAll()
        );
    }

    // 新增用户
    @PostMapping
    public Result<String> addUser(
            @RequestBody User user, @RequestHeader(value = "X-Session-Token", required = false) String token){
        if (!sessionService.hasRole(token, "ADMIN")) return Result.error(403, "无权新增用户");
        int result=userService.addUser(user);
        if(result>0){
            return Result.success(
                    "新增用户成功",
                    null
            );
        }
        return Result.error(
                "新增用户失败"
        );
    }

    // 修改角色
    @PutMapping("/role")
    public Result<String> updateRole(
            @RequestParam Long id,
            @RequestParam String role, @RequestHeader(value = "X-Session-Token", required = false) String token){
        if (!sessionService.hasRole(token, "ADMIN")) return Result.error(403, "无权修改角色");
        int result =
                userService.updateRole(id,role);

        if(result>0){
            return Result.success(
                    "角色修改成功",
                    null
            );
        }
        return Result.error(
                "角色修改失败"
        );
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id, @RequestHeader(value = "X-Session-Token", required = false) String token) {
        if (!sessionService.hasRole(token, "ADMIN")) return Result.error(403, "无权注销用户");
        if (sessionService.get(token).getId().equals(id)) return Result.error("不能注销当前登录账号");
        return userService.deleteUser(id) > 0 ? Result.success("用户已注销", null) : Result.error("注销失败：用户可能不存在或有关联记录");
    }
}
