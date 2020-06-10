[TOC]
# Overview
- 注册中心也不是什么新的概念，在Erueka/Springboot出现之前在很多软件系统中就有这个功能，很多公司自己开发相关的功能(譬如某公司的GID.registry)
  - 现在出现的spring cloud使得相关的技术迅速普及

- Eureka已经不推荐使用了，因为只更新到1.x， netflix已经不更新2.x了(discontinued）
  - AWS上面直接支持Eureka
  - 真正使用的时候去要启动集群（如何使用集群？防止单点问题）

# 注册中心到底用啥Eureka, Consul, ELB（云上）?
- Eureka 2.0已经不开发了，如果是新项目的话直接用Consul吧。
- Consul还有一个好处是可以作为注册中心+配置中心，减少维护
- 云上建议使用ELB，简化client代码

## Eureka/Consule 与AWS ELB的关系？
- ELB的这种方式更加传统一点，也更加简单（复杂度集中在ELB的配置上），调用者不用关心（也不知道）在loadbalance时候（或者节点选择避免单点故障时候）发生了什么。
- Eureka（或者类似的方案，如Consul之类的），client端知道很多信息，service端也要把自己向注册中心汇报，感觉这个复杂度增加了不少。当然了，都不要钱，自己维护吧。
- 总结：所以，在aws上面的话，我觉得就别折腾什么Eureka（或者consul）了，直接往ELB上面一挂，完事。否则给自己找活呢....


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


# Eureka注册中心使用简介

## 搭建Eureka注册中心超级简单

- 3件事
  - 添加依赖
  - 添加配置（defaultZone，registerWithEureka：false等）
  - 代码/app中增加注解：@EnableEurekaServer

- 见chapter 4.3和chapter4 eurekasvr的代码

## 注册service到Eureka- Easy（无代码更改，只有配置变更）

- 3件事
  - 添加Eureka依赖
  - 添加Eureka的配置到application.yml中
  - 启动脚本将Eureka的url作为参数传入 -  -Deureka.client.serviceUrl.defaultZone=$EUREKASERVER_URI

- 见chapter 4.4和 chapter4 oraganization service的代码（pom.xml/docker)

## Client调用：通过Eureka找到对应service（section 4.5），然后调用
- client已知：remote serviceName，和remote service API
- 见chapter 4.4和 chapter4 licensing service的代码
- 配置
  - application yml：eureka url, etc
  - pom.xml - spring-cloud-starter-netflix-eureka-client
  - note： 这些url可以覆盖（如-Deureka.client.serviceUrl.defaultZone=$EUREKASERVER_URI）
  - e.g.
eureka:
  instance:
    preferIpAddress: true
  client:
    registerWithEureka: true
    fetchRegistry: true
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/  
- 代码3中-DiscoveryClient, Ribon-based client, Feign client
  - DiscoveryClient - native 方式 - 不推荐（除非只是获取所有当前服务列表）- OrganizationDiscoveryClient（section 4.5.1）
    - @EnableDiscoveryClient on Application class - 这样我们后面就可以用了
    - 这里就是使用这个client了
      - @Autowired private DiscoveryClient discoveryClient;
      - discoveryClient.getInstances("organizationservice");  
      - restTemplate.exchange(
                              String.format("%s/v1/organizations/%s",instances.get(0).getUri().toString(), organizationId),
                              HttpMethod.GET,
                              null, Organization.class, organizationId);
                              
  - Ribbon-based REST（section 4.5.2）
    - 声明一个@LoadBalanced RestTemplate
      - @LoadBalanced @Bean public RestTemplate getRestTemplate(){ return new RestTemplate(); }
    - 然后就可以用这个RestTemplate了
      - @Autowired RestTemplate restTemplate;
      - ResponseEntity<Organization> restExchange =
            restTemplate.exchange(
                    "http://organizationservice/v1/organizations/{organizationId}",
                    HttpMethod.GET,
                    null, Organization.class, organizationId);
      - note：url中的hostname：organizationservice，这个不是真正的url（没有端口），只是
      
  - EurekaFeignt client（section 4.5.3）
    - @EnableFeignClients on Application class - 这样我们后面就可以用了
    - 直接以注解方式完成client代码
      - @FeignClient("organizationservice")
        public interface OrganizationFeignClient {
            @RequestMapping(
                    method= RequestMethod.GET,
                    value="/v1/organizations/{organizationId}",
                    consumes="application/json")
            Organization getOrganization(@PathVariable("organizationId") String organizationId);
        }

