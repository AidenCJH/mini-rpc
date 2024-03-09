# README

#### 更新日期：2024年3月9日

#### 作者：Aiden（个人网站：https://www.aidenchen.top）

## 项目介绍

本项目是一个RPC（远程过程调用）框架。

### 功能介绍

服务提供者可以通过本框架提供远程调用服务，通过心跳机制确保服务有效。

服务使用者可以通过本框架远程调用服务，像使用本地方法一样调用远程方法。

### 使用技术

采用Redis实现服务注册、服务管理和服务过期

服务提供者采用Tomcat接收调用的HTTP请求

服务使用者支持负载均衡、错误重试等功能

通过配置文件统一进行配置项的设置

### 使用方法

1、在项目中导入MiniRPC模块

2、参照Provider-Common模块编写接口

3、在服务提供模块和服务使用模块导入MiniRPC和Provider-Common的Maven依赖

4、参照Provider模块的Provider类进行服务注册和服务启动

5、参照Consumer模块的Consumer类进行服务调用

## 更新日志

### 2024.3.9

初步完成整体项目开发