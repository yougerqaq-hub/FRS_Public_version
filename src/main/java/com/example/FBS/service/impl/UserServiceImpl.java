package com.example.FBS.service.impl;

import com.example.FBS.entity.User;
import com.example.FBS.mapper.UserMapper;
import com.example.FBS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;



    @Override
    public User login(String username, String password) {
        if(username == null || username.trim().isEmpty()){
            return null;
        }
        if(password == null || password.trim().isEmpty()){
            return null;
        }
        return userMapper.login(username,password);
    }

    @Override
    public List<User> findAll(){
        return userMapper.findAll();
    }

    @Override
    public int addUser(User user){

        if(user == null){
            return 0;
        }
        if(user.getUsername()==null
                || user.getUsername().trim().isEmpty()){
            return 0;
        }
        if(user.getPassword()==null
                || user.getPassword().trim().isEmpty()){
            return 0;
        }
        if(user.getRole()==null
                || user.getRole().trim().isEmpty()){
            return 0;
        }
        return userMapper.addUser(user);
    }

    @Override
    public int updateRole(Long id,String role){
        if(id==null){
            return 0;
        }
        if(!"ADMIN".equals(role)
                && !"FINANCE".equals(role)
                && !"EMPLOYEE".equals(role)){
            return 0;
        }
        return userMapper.updateRole(id,role);
    }

    @Override
    public int deleteUser(Long id) {
        return id == null ? 0 : userMapper.deleteById(id);
    }

}
