package com.example.FBS;

import com.example.FBS.entity.User;
import com.example.FBS.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserService userService;

    @Test
    public void testLogin() {

        User user = userService.login("admin", "123456");

        if (user != null) {
            System.out.println("Service查询成功！");
            System.out.println("用户名：" + user.getUsername());
            System.out.println("真实姓名：" + user.getRealName());
            System.out.println("角色：" + user.getRole());
        } else {
            System.out.println("Service查询失败，用户不存在！");
        }
    }
}