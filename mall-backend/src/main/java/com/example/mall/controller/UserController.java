package com.example.mall.controller;

import com.example.mall.common.Result;
import com.example.mall.entity.User;
import com.example.mall.mapper.UserMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.JwtUtils;
import cn.hutool.crypto.digest.BCrypt;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    //接口地址为http://localhost:8081/user/list
    @GetMapping("/list")
    public Result<List<User>> list() {
        List<User> users = userMapper.selectList(null);
        return Result.success(users);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;


//    @GetMapping("/gen")
//    public String generatePassword(@RequestParam String pwd) {
//        String encodedPassword = passwordEncoder.encode(pwd);
//        System.out.println("=========================================");
//        System.out.println("您的真实加密密码是:");
//        System.out.println(encodedPassword);
//        System.out.println("=========================================");
//        return encodedPassword;
//    }//临时获取密码用


    @PostMapping("/login")
    public Result<LoginResult> login(@RequestBody User loginUser) {
        //根据用户名去数据库查
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUser.getUsername());
        User dbUser = userMapper.selectOne(wrapper);

        //校验用户是否存在
        if (dbUser == null) {
            return Result.error("用户不存在");
        }

        //校验密码
        if (!passwordEncoder.matches(loginUser.getPassword(), dbUser.getPassword())) {
            return Result.error("密码错误");
        }

        //生成Token
        String token = JwtUtils.generateToken(dbUser.getUsername());

        //返回结果
        LoginResult data = new LoginResult();
        data.setToken(token);
        data.setUserId(dbUser.getUserId());
        data.setUsername(dbUser.getUsername());
        data.setAvatar(dbUser.getAvatar());

        return Result.success(data);
    }

    @Data
    class LoginResult {
        private String token;
        private Long userId;
        private String username;
        private String avatar;
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        //校验账号是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        Long count = userMapper.selectCount(wrapper);

        if (count > 0) {
            return Result.error("该账号已被注册");
        }

        //密码加密
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        //设置默认头像和昵称
        if(user.getNickname() == null) {
            user.setNickname("大米用户_" + System.currentTimeMillis() / 1000);
        }

        //存入数据库
        userMapper.insert(user);

        return Result.success("注册成功");
    }
    //修改个人信息接口
    //URL: POST http://localhost:8081/user/update
    @PostMapping("/update")
    public Result<User> update(@RequestBody User user) {
        if (user.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }

        //直接根据ID更新非空字段
        userMapper.updateById(user);

        //更新完后，查出最新的用户信息返回给前端刷新缓存
        User newUser = userMapper.selectById(user.getUserId());
        newUser.setPassword(null);

        return Result.success(newUser);
    }

    //修改密码
    @PostMapping("/updatePassword")
    public Result<String> updatePassword(@RequestBody Map<String, String> params) {
        String userIdStr = params.get("userId");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        if (userIdStr == null || oldPassword == null || newPassword == null) {
            return Result.error("参数不完整");
        }

        User user = userMapper.selectById(Long.parseLong(userIdStr));
        if (user == null) return Result.error("用户不存在");


        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }

        String newPwdHash = BCrypt.hashpw(newPassword);
        user.setPassword(newPwdHash);

        userMapper.updateById(user);

        return Result.success("修改成功");
    }
}