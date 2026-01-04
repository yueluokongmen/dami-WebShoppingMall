package com.example.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.Result;
import com.example.mall.entity.UserBrowseLog;
import com.example.mall.mapper.UserBrowseLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class AdminLogController {

    @Autowired
    private UserBrowseLogMapper userBrowseLogMapper;

    //后台接口

    //获取所有浏览日志
    // URL: /admin/log/list
    @GetMapping("/admin/log/list")
    public Result<List<UserBrowseLog>> list() {
        LambdaQueryWrapper<UserBrowseLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(UserBrowseLog::getBrowseTime); //按时间倒序
        wrapper.last("LIMIT 100"); //限制查最近100条演示
        return Result.success(userBrowseLogMapper.selectList(wrapper));
    }

    //前台接口

    //记录浏览日志
    //URL: /user/log/add
    @PostMapping("/user/log/add")
    public Result<String> addLog(@RequestBody UserBrowseLog log) {
        log.setBrowseTime(LocalDateTime.now());
        userBrowseLogMapper.insert(log);
        return Result.success("记录成功");
    }
}