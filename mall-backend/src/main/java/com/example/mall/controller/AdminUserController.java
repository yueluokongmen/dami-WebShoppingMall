package com.example.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.Result;
import com.example.mall.entity.User;
import com.example.mall.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // 🔥 引入这个
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //获取客户列表
    @GetMapping("/list")
    public Result<List<User>> list() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);
        List<User> list = userMapper.selectList(wrapper);
        return Result.success(list);
    }

    //重置密码
    @PostMapping("/reset-pwd/{userId}")
    public Result<String> resetPwd(@PathVariable Long userId) {
        User user = new User();
        user.setUserId(userId);

        //使用 BCrypt加密"123456"
        String encodedPwd = passwordEncoder.encode("123456");
        user.setPassword(encodedPwd);

        userMapper.updateById(user);
        return Result.success("密码已重置为 123456");
    }
}