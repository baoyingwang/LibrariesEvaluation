- spring boot的各个使用功能点，指导以后工作中直接使用，参考各个模块内部
  - controller例子
    - HelloController - done - copy/paste
    - OrderController - 稍微复杂了一点，更实用了一些
      - TODO， 增加更改/Put的例子
      - TODO，增加类似于swagger那样的REST文档
  - logging
    - 打印所有requests/response  - see: logging.LogAllRequests.java, and logging.LogAllResponses.java
      - TODO requests好像打印了两遍，需要调查
    - 给每个请求增加一个uuid，并打印到log中 - see：logging.LogUniqueIDPerRequest.java
    - 使用log4j2，而非默认的logback - see - pom.xml中exclude spring自身logging，和增加的log4j2.xml例子
  - 返回码 - 见returnhandle package
    - package里面有个spring-errorcode.md详细描述了REST接口的返回原则和例子等等
    - 还没有完全做完
    
- TODO
  - 认证
    - 这个topic比较大，因为有多种认证方式
      - 先从key/secret的做起来
      - 然后考虑JWT，OAuth等
  - 数据库链接/使用，尤其是dbunit相关的
    - 还可以考虑直接使用flyway之类的来协助migratedb
    - 还可以考虑使用docker来协助
    - 参考 KB 《Journal Mar 26, 2019(Tue) 工具重要，思路更重要》，和《spring boot and dbunit》
  - error处理时候的返回码（参考resulthandle package) - 有了一部分，但是还没弄完
  - 单元测试 - 已经有了一个，不过需要再看看
  - 集成测试 - 已经有了一个，不过需要再看看
