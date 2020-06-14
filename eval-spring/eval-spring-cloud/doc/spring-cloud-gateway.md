[TOC]
# Overview
- Gateway - 作为系统的入口，与外部系统区分开
  - 还可以做认证/授权/MetricsCollection/Logging等
  - 把client发送进来的url转换为真正的service url并进行调用。这个调用可能通过registry/Eureka完成
  
- 现在都用Spring Cloud Gateway了，新项目不再使用Zuul了
- 还有什么别的方式么？ 
  - AWS ELB也可以做类似的功能
  - Nginx 也有reverse proxy功能

# Zuul - reverse proxy

## 搭建Zuul
- pom.xml: spring-cloud-starter-zuul
- @EnableZuulProxy on Application
- 链接后端注册中心，使得可以获得左右注册列表
  - 多种注册方式
  - 默认啥也不弄，则Eureka注册中心的东西直接使用
    - Automated mapping of routes via service discovery
      - FROM: http://localhost:5555/organizationservice/v1/organizations/e254f8c-c442-4ebea82a-e2fc1d1ff78a
      - TO  : 
  - Manual mapping of routes using service discovery
  - Manual mapping of routes using static URLs
- note：Eureka url加到配置里面去

## Zuul功能
- 默认功能
  - 根据定义的mapping完成请求的分发
  - 针对分发完成Ribon-based loadbalance（？）
  - Hystrix已经内置（熔断之类的）
  
- 通过3中filter将request进行特殊处理
  - pre-filter - 验证/授权
  - post-filter - 回写header
  - route-filter - 定制化路由 
 
 ## Zuul 日常使用
 - 获取所有当前的route: https://localhost:5555/routes
 - 刷新配置：/refresh ， 从配置中心中心获取最新的配置文件
  