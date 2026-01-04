package com.example.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mall.common.Result;
import com.example.mall.entity.Orders;
import com.example.mall.mapper.OrdersMapper;
import com.example.mall.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/admin/stats")
public class AdminStatsController {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/data")
    public Result<Map<String, Object>> getData() {
        Map<String, Object> map = new HashMap<>();


        //计算顶部的四个核心卡片数据


        //总销售额
        QueryWrapper<Orders> salesWrapper = new QueryWrapper<>();
        salesWrapper.select("IFNULL(sum(total_amount), 0) as total");
        Map<String, Object> salesRes = ordersMapper.selectMaps(salesWrapper).get(0);
        BigDecimal totalSales = (BigDecimal) salesRes.get("total");

        //总订单量
        Long totalOrders = ordersMapper.selectCount(null);

        //今日订单数
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        QueryWrapper<Orders> todayOrderWrapper = new QueryWrapper<>();
        todayOrderWrapper.ge("order_create_time", todayStart);
        Long todayOrders = ordersMapper.selectCount(todayOrderWrapper);

        //总用户数
        Long totalUsers = userMapper.selectCount(null);

        map.put("totalSales", totalSales != null ? totalSales : 0);
        map.put("totalOrders", totalOrders);
        map.put("todayOrders", todayOrders);
        map.put("totalUsers", totalUsers);


        //计算图表数据 (最近7天)

        List<String> dates = new ArrayList<>();
        List<BigDecimal> salesList = new ArrayList<>();
        List<Long> orderList = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));

            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            //查询当天的销售额
            QueryWrapper<Orders> daySalesWrapper = new QueryWrapper<>();
            daySalesWrapper.select("IFNULL(sum(total_amount), 0) as total")
                    .ge("order_create_time", start)
                    .le("order_create_time", end);
            Map<String, Object> daySalesRes = ordersMapper.selectMaps(daySalesWrapper).get(0);
            salesList.add((BigDecimal) daySalesRes.get("total"));

            //查询当天的订单数
            QueryWrapper<Orders> dayOrderWrapper = new QueryWrapper<>();
            dayOrderWrapper.ge("order_create_time", start)
                    .le("order_create_time", end);
            orderList.add(ordersMapper.selectCount(dayOrderWrapper));
        }

        map.put("dates", dates);
        map.put("sales", salesList);
        map.put("orders", orderList);

        return Result.success(map);
    }
}