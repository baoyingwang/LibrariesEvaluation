[TOC]
# Overview
https://spring.io/projects/spring-cloud-config
- Config作为一个独立的组件/进程，是的使用者可以方便的从这里拉取自己的配置。
- 注意：Consul可以作为Spring Cloud Config的backend出现。
  - 天啊，这么多组件。相当于多维护了一个（1组：避免单点故障）SpringCloudConfig服务
- 别种Spring Cloud Config了，直接用Consul吧
  - Consul能同时当配置中心和注册中心，少维护一个组件呢


# 灵魂拷问：配置中心到底用啥？ spring cloud config，etcd，consul，eureka ，zookeeper

- spring microservice in action ,table 3.1 /p69 有个表格，简要对比了etcd/eurika/consul/zookeeper/spring cloud config
- 对比 Applo/SpringCloudConfig/Nacos/ https://zhuanlan.zhihu.com/p/67800670
- zookeeper别用了，太老了，不方便
- 对比： http://www.ityouknow.com/springcloud/2018/07/20/spring-cloud-consul.html
- 用啥的都有，需要盯住一个，看看相关的feature怎么满足我们需求
- 我觉得：要用简单的/安全的，稍微繁琐一点可以自己研发一些东西出来
  - 否则客户那里部署太复杂的化，会导致很多问题
  - SpringCloudConfig的话，部署起来就比较复杂，需要bus/github或者gitlab，见https://zhuanlan.zhihu.com/p/67800670
  - 理论上etcd最简单，但是关于安全/认证等方面都没有（？）
  - 没有免费的午餐啊
  - 我想用consul或者eureka，因为他们可作为配置中心+注册中心，减少模块，这很重要！


- 要求/特性
  - security ： prod环境内容（密码/key）有权限才能看
        - spring cloud config如何做？需要到spring in action书中看一看
        - 还和如何维护这些文件有关系。
              - spring cloud config中支持使用git，然而git权限粒度又不那么好，所以需要进一步看了 - 参考Spring Boot in Action by Craig
  - 动态下发

# 日常使用

## Spring Cloud Config 常用REST
- TODO：找到/搭建可视化监控Spring Cloud Config情况的监控工具
- api这里可以看到：https://github.com/spring-cloud/spring-cloud-config，就在主页
- 获取某服务对应的配置文件
  - http://localhost:8888/serviceName/profileName
  - e.g. http://localhost:8888/licensingservice/dev
  - note: profileName一定要给，默认的用'default', e.g. http://localhost:8888/licensingservice/default
  - warn: 它可能返回套值，一套为dev，一套为default。合并这些值的只能在service内部完成。

# 开发过程

## quick start - server/client端的配置/代码

- server端
  - 配置文件安排：clientname(as dir)/clientmame-profilename.yaml
  
- 中间实际的protocol为：http://localhost:8888/clientname/profilename
  - e.g.  http://localhost:8888/licensingservice/dev

- client端 ： 通过clientname+profiles.active(profilename)确定唯一特性
  - 定义bookstrap.yaml，用于引导, 然后启动的时候overwrite一下
  - 还是参考书上例子
  - 客户端通过profiles.active定义，来决定使用什么环境的变量（内部通过更新url）
      - 开始时候启动，更新这个变量即可，譬如 -Dspring.profiles.active=$PROFILE

```

spring:
  application:
    name: licensingservice
  profiles:
    active:
      default
  cloud:
    config:
      uri: http://localhost:8888

```

## 用文件方式（仅仅测试）时候，可以绝对路径也可以classpath

譬如下面例子中，为了简化构建docker时候（避免copy文件），直接使用classpath（因为配置文件已经zip到jar文件中了）

```

server:
   port: 8888
spring:
  profiles:
    active: native
  cloud:
     config:
       server:
           native:
              #searchLocations: file:///Users/baoyingwang/ws/code/Manning-spring-microservice/spmia-chapter3-master/confsvr/src/main/resources/config/licensingservice,
              #                 file:///Users/baoyingwang/ws/code/Manning-spring-microservice/spmia-chapter3-master/confsvr/src/main/resources/config/organizationservice
              searchLocations: classpath:config/,classpath:config/licensingservice

```

## 构造一个简单的基于文件或者classpath的spring cloud config server - 非常简单

- 3步
  - 增加依赖
  - 给application增加注解 @EnableConfigServer
  - application.yml 中设置指向文件位置 / searchLocations
      - note：这里给出的文件目录和位置有一些简单的讲究
      - 每个服务对应一个文件夹（与服务名称同名）。服务名称就是服务在boolstrap.yml中的名字
      - 文件夹里面可以有default(application.yml), 和各个环境的文件(application-dev.yml, application-prod.yml)
      - 具体客户端使用的时候，哪个返回回去，请求端会把name和profile发上来，自然就能定位到对应的配置了
- 参考chapter 3.2.2

- 几个注意的点
  - 自动刷新：如果是git的话，可以绑定git的webhook；也可以通过acutator的refreshurl来完成，参考sectino 3.3.6
  - sensitive信息要做一个加密保存，而且避免返回的信息是解密的，让client自己去解密，有几个设置要搞一下。参考 section 3.4
  - 看看chapter3吧


## 配置service使用配置中心(chapter  3.3)

- 2步
  - pom中增加依赖
  - boostrap中增加指向config的url - 这个最后还是使用ENV（run.sh）中替换
  - note：chapter4中的licencing service和organization service都设置了另外一个变量（bootstrap.yaml) : spring.cloud.config.enabled: True - 好像也没啥用。当然，要关掉它的时候，可能有用

```

java -Dspring.cloud.config.uri=http://localhost:8888 \
-Dspring.profiles.active=dev \
-jar target/licensing-service-0.0.1-SNAPSHOT.jar
```

# Open问题

## (OK)Spring Cloud config - 数据库密码如何保存

### by Vault - 这个比较复杂了，官方文档里面倒是有写，但是开始的时候我们想要一个简要的版本

### 直接通过对称加密 key通过环境变量设置：ENCRYPT_KEY

  - manning spring microservice in action p91 3.4.2中有讲到/还要看3.4.3
  - 用对称加密或者公钥/私钥加密（这个书中没讲，就提了一句）
  - 对称加密比较简单些
      - 依赖于环境变量：ENCRYPT_KEY
      - 在build docker container（both config server and client service）时候，可以从系统环境变量传进去
          - 注意：在build container时候，已经知道了你用什么环境（dev还是prod）
          - build config server和client service时候，都需要这个变量
              - 启动的时候设置系统环境变量by:  export ENCRYPT_KEY="IMSYMMETRIC"
              - 或者启动的时候设置java -D ??? 还没试过
              - docker compose的时候，使用enviroment变量（如何避免明文保存这个key在compose文件中呢？很多种方式https://docs.docker.com/compose/environment-variables/， 譬如从system env中读取或者给一个env文件-prefer）
          - 记得把server的解密去掉，让client自己解（见section 3.4.3）
          - 用compose方式的话，参考chapter3/docker/common/docker-compose.yaml. 这个common的值被dev和prod的值覆盖（因为在dev/prod的yaml中可以看到 file: ../common/docker-compose.yml）。可以看到configserver和 licenseserver都有这个环境变量。我们不要写在compose。yaml中，而是定义在你的build机器上面，注意保密！！！
     
## (OPEN)使用Spring Cloud Config时候，如何限制不同环境（不同的人员）访问不同环境？

- 这个在manning - spring microservice in action p83 /3.2.2中提到，在另一本书spring boot in action by Craig有讲 - 说是使用  Spring Actuator.
  - 找了一些，没看到具体内容





## service配置了config server，但是config server没启动，service为啥能启动？
一个很tricky的地方，在chapter4中，我启动了oraganization service（依赖config server）, eureka，但是没有启动config server，都是通过mvn spring-boot:run来启动的。eureka的输出看起来正常（http://localhost:8761/eureka/apps/organizationservice），能够返回这个注册的organization service。

但是，为啥没有config server情况下，这个organization service竟然启动的挺正常呢？
在log中能看到起链接config server失败，但是就继续执行下去了，最后显示启动成功。
我期望的行为是，这玩意儿地config server的依赖是强依赖，我希望organization service启动不起来
我期望：有个配置能够设定如果链接config server失败，则service启动失败
有个实际的情况是：如果在org service中，用到了一些config server返回的值来初始化一些bean，因为config server没有启动，所以这些bean无法初始化，则org service就无法启动了。但是，这里的例子比较tricky，org service用默认的各个参数都跑起来了，哈哈

```
baoyingwang@localhost organization-service % pwd
/Users/baoyingwang/ws/code/Manning-spring-microservice/spmia-chapter4-master/organization-service
baoyingwang@localhost organization-service % mvn spring-boot:run

可以看到下面的错误
2020-06-08 22:43:32.214  WARN 59557 --- [           main] c.c.c.ConfigServicePropertySourceLocator : Could not locate PropertySource: I/O error on GET request for "http://localhost:8888/organizationservice/default": Connection refused (Connection refused); nested exception is java.net.ConnectException: Connection refused (Connection refused)
。。。
2020-06-08 23:25:43.720  INFO 60579 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_ORGANIZATIONSERVICE/192.168.100.116:organizationservice: registering service...
2020-06-08 23:25:43.785  INFO 60579 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_ORGANIZATIONSERVICE/192.168.100.116:organizationservice - registration status: 204
2020-06-08 23:25:43.867  INFO 60579 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
2020-06-08 23:25:43.869  INFO 60579 --- [           main] c.n.e.EurekaDiscoveryClientConfiguration : Updating port to 8080
2020-06-08 23:25:43.875  INFO 60579 --- [           main] c.t.organization.Application             : Started Application in 8.83 seconds (JVM running for 11.831)

最后提示启动成功

```

在bootstrap.yml中enable了config server, 我故意没有启动它（config server）
```
spring:
  application:
    name: organizationservice
  profiles:
    active:
      default
  cloud:
    config:
      enabled: true

```
