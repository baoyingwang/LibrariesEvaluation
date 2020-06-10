[TOC]
# Overview

There are four client resiliency patterns:
- 1 Client-side load balancing - Ribbon
- 2 Circuit breakers - 一个服务调用如果执行太久或者失败太多，则去掉它（啥时候填回来）
  - A circuit breaker acts as a middle man between the application and the remote service
- 3 Fallbacks - 一个服务失败，换到另一个服务或者指定的其他行为
- 4 Bulkheads - 不同的service对应不同的threadpool，避免相互影响


# 通过Hystrix完成 CircuitBraker/Fallback/Bulkhead - client resilience
## Hystrix使用例子（1个小例子，但是很全）
```
class LicensingService

@HystrixCommand( //circuit break - 这个annotation自身就代表了熔断功能

        //fallback： 如果当前方法出问题，则调用fallbackMethod（这个method如果还调用外部服务，则也应
        fallbackMethod = "buildFallbackLicenseList",  

       //bulkhead： 每个调用使用自己的线程池（那是不是线程池太多了？）
       threadPoolKey = "licenseByOrgThreadPool",
        threadPoolProperties =
                {@HystrixProperty(name = "coreSize",value="30"),
                 @HystrixProperty(name="maxQueueSize", value="10")},
        commandProperties={
                 @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),
                 @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
                 @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),
                 @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),
                 @HystrixProperty(name="metrics.rollingStats.numBuckets", value="5")}
)
public List<License> getLicensesByOrg(String organizationId){

    logger.debug("LicenseService.getLicensesByOrg  Correlation id: {}", UserContextHolder.getContext().getCorrelationId());
    randomlyRunLong(); //这个是故意增加的，用于模拟系统出问题了
    return licenseRepository.findByOrganizationId(organizationId);
}

```

## Hystrix还可以通过Feign来直接使用 - 但是Feign只能用来调用远端REST，如果是普通的方法就不行了（？）
- 对Hystrix的使用是通过Feign的yml配置和注解的参数完成的
- Feign确实比较简单，但是Feign好像也不维护了？还在维护，20200610还在release呢
  - 长期还要增加对resillience4j和spring cloud circuit breaker对支持 
- Fein文档与代码
  - 文档 https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html
  - 代码 https://github.com/OpenFeign/feign
## Hystrix的一些源码
https://cloud.tencent.com/developer/article/1334260
https://blog.csdn.net/songhaifengshuaige/article/details/80345072