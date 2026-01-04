package com.example.mall.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.Result;
import com.example.mall.entity.User;
import com.example.mall.mapper.UserMapper;
import com.example.mall.common.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    //管理员登录接口
    // URL: POST http://localhost:8081/admin/login
    @PostMapping("/login")
    public Result<Map<String, Object>> adminLogin(@RequestBody User params) {
        //查用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, params.getUsername());
        User user = userMapper.selectOne(wrapper);

        //校验是否存在
        if (user == null) {
            return Result.error("用户不存在");
        }

        if(!params.getPassword().equals(user.getPassword()))
        if (!BCrypt.checkpw(params.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        if (user.getRole() == null || user.getRole() != 1) {
            return Result.error("无权访问后台管理系统");
        }

        String token = JwtUtils.generateToken(user.getUsername());

        user.setPassword(null);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }
}