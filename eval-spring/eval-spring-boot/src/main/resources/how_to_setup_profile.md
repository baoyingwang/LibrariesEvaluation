[TOC]

# Overview
- spring boot支持设定多个不同的profile，这样方便的区分不同的环境，譬如dev，prod等
  - 譬如定义application-dev.yml和application-prod.yml去代表不同环境使用的配置i文件
  - 注意：通用的配置放在默认配置文件application.yml中，避免重复定义

https://stackoverflow.com/questions/40060989/how-to-use-spring-boot-profiles
https://www.baeldung.com/spring-profiles

# 如何指定使用什么profile？

## 几个方法
- 运行时：通过系统环境变量 SPRING_PROFILES_ACTIVE
- 运行时：通过JVM -Dspring.profiles.active=prod
- 开发时：可以直接更改application.yml中的指向
- mvn run时： e.g. mvn spring-boot:run  -Drun.profiles=prod
  - 暂时我测试一直不灵，不知为何（尝试了很多方法）
- UnitTest：@ActiveProfiles("dev")

# 细节

## 在开发时候，通过intellij启动,人肉修改applicaiton.yml
  - 可以在通过人肉更改的application.yml中默认指定为使用dev. 这样开发时候超级方便
    - 注意：部署环境的时候，别忘了更改为对应环境
```
spring:
  profiles:
    active: dev
```
## 在开发时候，通过mvn spring-boot:run启动
  - 如果applicaiton.yml中已经指定的active:dev, 则就是dev了
not work: mvn spring-boot:run -Drun.profiles=prod
not work: mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=prod"
  - mvn spring-boot:run时候的问题
    - https://stackoverflow.com/questions/52030105/spring-boots-spring-bootrun-doesnt-honor-specified-profile/52036427
    - 不要在pom.xml的plugin中指定properties
  - https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/maven-plugin/examples/run-profiles.html
  
## 运行时： 直接运行java启动的时候，通过-Dspring.profiles.active=prod指定（默认application.yml的值将被覆盖为当前命令行的值）
  - java -Dspring.profiles.active=prod -jar eval-spring-1.0-SNAPSHOT.jar
  - java -Dspring.profiles.active=dev -jar eval-spring-1.0-SNAPSHOT.jar
  - 还有一个方法，如下面放在参数中。不过我不建议使用，都几种在一个地方比较好，这种参数更像一个环境参数。
    - 不建议：java -jar eval-spring-1.0-SNAPSHOT.jar --spring.profiles.active=prod
    - 不建议：java -jar eval-spring-1.0-SNAPSHOT.jar --spring.profiles.active=prod

## 运行时：通过系统环境变量 SPRING_PROFILES_ACTIVE - 但是我建议使用java env

## TestCase中使用 @ActiveProfiles("dev")

- mvn -P, 经过我的测试不太好用
  - https://medium.com/@derrya/maven-profile-spring-boot-properties-a34f2b2bb386