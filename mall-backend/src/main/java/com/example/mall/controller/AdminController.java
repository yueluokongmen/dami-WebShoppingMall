package com.example.mall.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.Result;
import com.example.mall.entity.User;
import com.example.mall.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    // 管理员专属登录接口
    // URL: POST http://localhost:8081/admin/login
    @PostMapping("/login")
    public Result<User> adminLogin(@RequestBody User params) {
        //查用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, params.getUsername());
        User user = userMapper.selectOne(wrapper);

        //校验是否存在
        if (user == null) {
            return Result.error("用户不存在");
        }

        //校验密码
        if (!BCrypt.checkpw(params.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        if (user.getRole() == null || user.getRole() != 1) {
            return Result.error("无权访问后台管理系统");
        }

        user.setPassword(null);
        return Result.success(user);
    }
}