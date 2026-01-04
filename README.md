# 🛍️ 大米商城 (Dami WebShopping) - 课程实验项目

> 《网络应用架构设计与开发》课程实验作业

## 👤 作者信息
* **姓名**：石德麒
* **学号**：202330451571
* **班级**：23网络工程
* **提交日期**：2025-12-31

## 🌐 在线演示 (Demo)
* **部署地址**：http://8.163.12.69

## 🛠️ 技术栈 (Tech Stack)
* **前端**：Vue 3, TypeScript, Vite, Element Plus, Axios
* **后端**：Spring Boot, MyBatis-Plus, MySQL 8.0, JWT
* **运维**：Aliyun ECS, Nginx, Linux (CentOS/Alibaba Cloud Linux)

## 📂 项目介绍
本项目是一个完整的前后端分离电子商务网站，包含以下核心功能：
1. **用户端**：浏览商品、加入购物车、下单、个人订单管理。
2. **管理端**：后台商品增删改查、订单状态管理、商品购买统计。
3. **部署**：已完成云服务器环境搭建与Nginx反向代理配置。

## 🚀 本地运行 
1. 克隆项目：`git clone https://github.com/yueluokongmen/dami-WebShoppingMall.git`
2. 导入 SQL 文件至 MySQL 数据库。
3. 后端：IDEA 打开 `mall-backend`，运行 Application。
4. 前端：VSCode 打开 `mall-frontend`，先`npm install`安装依赖后再运行 `npm run dev`。

## ☁ 云端测试
1. 新用户直接点击登录然后注册
2. 管理员登录入口直接在ip后输入/admin/login，然后输入账号即可
3. 网站性能不是很好，点击操作要慢一些，点击操作后如果卡顿请等待几秒钟
