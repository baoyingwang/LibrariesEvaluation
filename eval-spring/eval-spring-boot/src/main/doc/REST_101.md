[TOC]
# Overview
关于REST的接口定义的文章汗牛充栋，这里简要的记录一些主要的东西

# GET-query/POST-create-insert/PUT-update/PATCH-update-partially/DELETE
## 基本情况
这些动作完美匹配了日常的CRUD操作
C - create - POST
R - read - GET
U - update - PUT/PATCH
D - delete - DELETE
## X-HTTP-Method-Override-提示server真正的method
- 用于client端只能发送GET/POST，无法发送UPDATE/PUT/DELETE情况
- 这样server就知道真正的method是什么了

# URL

## 名词  /v1/car 获取汽车，don't:/v1/getCar
- 例子
  - /v1/cars - GET - query all cars
    - /v1/cars/2 - GET - 查询id为2的car
  - /v1/cars - POST - create a car
  - 以及PUT/PATCH/DELETE
- note： 

## 复合条件/避免多级url - /v1/cars?brand=Benz, don't: /v1/cars/brand/Benz
- don't: /v1/cars/brand/Benz
  - 这种复合在一起的，很难理解
- 另一个例子（good）：/v1/cars?releaseYear=2020

# 返回状态码-见src/java/baoying.eval.spring.boot/resulthandle/spring-errorcode.md
基本思路就是
- 把http-code 与 业务code 要弄清楚
- 业务code为主
- 发生错误的适合，不要返回200  
