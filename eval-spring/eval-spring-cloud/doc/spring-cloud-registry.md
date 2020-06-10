[TOC]
# Overview
- 已经不推荐使用了，因为只更新到1.x， netflix已经不更新2.x了(discontinued）
- AWS上面直接支持（没去confirm呢）
- 真正使用的时候去要启动集群（如何使用集群？防止单点问题）
- 使用者/client通过注册中心拿到全部列表，然后自己选择用哪个。可以通过Ribbon/LoadBalance来决定用哪个。

# 日常使用

## Eureka常用REST
- TODO：找到/搭建可视化监控Eureka情况的监控工具
- 获取所有applications
  - http://localhost:8761/eureka/apps/
  - note：文档中给的是v2，但是Eureka v2已经不继续了，注意调整为对应v1版本url 
    - https://github.com/Netflix/eureka/wiki/Eureka-REST-operations
- 获得某个服务的信息
  - http://localhost:8761/eureka/apps/${appName}
  - e.g. http://localhost:8761/eureka/apps/CONFIGSERVER
  - note: appName就是对应服务的名称（定义在各自pom.xml中）

# 与AWS ELB的关系？
- 我觉得ELB更好些，简单啊
  - ELB那里的配置麻烦一些，但是理解上很好理解，概念上清楚
  - ELB的问题在于绑定了vendor
- Service Registry like Eureka is an example of client-side service discovery. 
  - Eureka - client端知道很多细节，知道services端的各种信息
  - 客户端要自己去做loadbalancing，譬如通过Ribbon
  -  Eureka, Istio is ** Client-side service discovery ** The client talks directly to the service registry and gets the complete address (host and port) of service to be called. So, in the end, the client knows the host and port of service and the client is the one who makes a final request to targeted service, therefore, this is called client-side discovery.


- AWS ELB represents server-side service discovery.
  - ELB - client啥也不知道，只要与ELB打交道就行了
  - AWS ELB is Server-side service discovery The client talks to a Load Balancer (or router). Router internally discovers the address of service via Service registry and then make a call further to target service.

-- https://stackoverflow.com/questions/54093008/aws-elb-vs-service-registry

## 白话理解ELB 与 Eureka/Consul注册中心

- ELB的这种方式更加传统一点，也更加简单（复杂度集中在ELB的配置上），调用者不用关心（也不知道）在loadbalance时候（或者节点选择避免单点故障时候）发生了什么。
- Eureka（或者类似的方案，如Consul之类的），client端知道很多信息，service端也要把自己向注册中心汇报，感觉这个复杂度增加了不少。当然了，都不要钱，自己维护吧。
- 总结：所以，在aws上面的话，我觉得就别折腾什么Eureka（或者consul）了，直接往ELB上面一挂，完事。否则给自己找活呢....

# 资料
  - 玩儿转spring cloud全家桶 https://time.geekbang.org/course/detail/100023501-93220
  - (2017) Spring Microservices In Action-John Carnell-Manning
  - Microservice Patterns -Manning.pdf - Chris
  - springboot in action - Craig Walls

# 注册中心

## 注册中心到底用啥Eureka, Consul, ?



# Eureka注册中心使用简介

## 搭建Eureka注册中心超级简单

- 3件事
  - 添加依赖
  - 添加配置
  - 代码/app中增加注解：@EnableEurekaServer

- 见chapter 4.3和chapter4 eurekasvr的代码


## 把自己注册到Eureka超级简单（无代码更改，只有配置变更）

- 3件事
  - 添加Eureka依赖
  - 添加Eureka的配置到application.yml中
  - 启动脚本将Eureka的url作为参数传入 -  -Deureka.client.serviceUrl.defaultZone=$EUREKASERVER_URI

- 见chapter 4.4和 chapter4oraganizationservice的代码（pom.xml/docker)


## Client调用：通过Eureka找到对应service （sectin 4.5）。然后调用
- client已知服务的名称，和服务的接口（API）

### 先引入spring-cloud-starter-netflix-eureka-client依赖
### 然后在application.yml中增加自相Eureka位置的配置
譬如下面。其中的defaultZone可以在run.sh中通过jvm option覆盖， e.g.  -Deureka.client.serviceUrl.defaultZone=$EUREKASERVER_URI
```
eureka:
  instance:
    preferIpAddress: true
  client:
    registerWithEureka: true
    fetchRegistry: true
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/

```
### 最后然后使用下面3中方式之一调用
#### Spring Descovery Client - 仅仅用来查询service列表挺好。别自己去去选择哪个client
  - 这玩意儿**仅**用来获取service列表，检查health之类的，别用来进行调用。因为有更好的方式。refer 4.5.1 "The discovery client and real life"
  - 这是一个抽象 ：@EnableDiscoveryClient - 你将知道所有service信息并自己作出选择哪一个
  - @Autowired  private DiscoveryClient discoveryClient;
  - discoveryClient.getInstances("organizationservice"); TODO：这个名称要hardcode么？
  - 参考listing 4.8
#### Ribbon-aware Spring RestTemplate （4.5.2）。
  - 声明一个@LoadBalanced restTemplate的方法
      - 玩转 SpringCloud全家桶 92 7:19/11：09位置，可以看到一个更加定制化的restTemplate。在chapter 4.5.2中的则极其简化了
  - 调用时（list 4.10），url的hostname直接用对端servicename（无端口）即可
#### EurekaFeignt  Client
  - 现在连Eureka都不推荐使用了，这个client就更别提了
  - 不提了，看section 4.5.3就行了。很简单
  - S