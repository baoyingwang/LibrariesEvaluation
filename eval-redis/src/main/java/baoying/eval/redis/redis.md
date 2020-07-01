基本java编码
https://www.baeldung.com/jedis-java-redis-client-library

注意：其中介绍的subscribe为blocking（上面连接中也明确写了）

异步订阅没有支持-https://github.com/xetorthio/jedis/issues/241 - 这里都讨论到2019年了
也是讨论https://github.com/xetorthio/jedis/pull/713 到了2020年3月19都不支持

# Pub/Sub模型
可以多个Sub消费同一个channel
即：启动两个RedisSub，然后发送可以看到两个Sub收到了相同的消息

## Sub时候- client与channel在redis server中的内存模型
这个文章详细的描述了redis内部是如何维护sub client与channel之间的关系的（内存表现）
https://making.pusher.com/redis-pubsub-under-the-hood/
该文章超级清楚。注意这个开始稍微有点复杂的图”Handling disconnections“ - 它表示双方向的指针，即channel指向client，client也指向channel。这个理解了，下面的图就简单了。
该文变表名了，subscribe和unsubscribe是cost比较高的操作

## client-output-buffer-limit for pubsub
e.g. client-output-buffer-limit pubsub 128mb 64mb 300
- 如果客户端太慢，导致缓冲区消息堆积，则会关闭连接
  - 参考： Redis客户端输出缓冲区限制调整 url： https://www.unixso.com/Cache/redis-client-output-buffer-limit.html
- 譬如client-output-buffer-limit pubsub 32mb 8mb 60
  - 在发布订阅模式下，如果缓冲区超过32mb，则断开连接
  - 在发布订阅模式下，如果缓冲区持续60秒超过8mb，则断开连接

譬如下面配置 0标示无限制
pubsub：发布订阅模式下的client缓冲区
slave：主从模式下，master端的缓冲区限制（对于主从复制来说，master相当于client）
replica？
normal？
```  
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit replica 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
```  

查看当前参数(这里也有提到： https://www.unixso.com/Cache/redis-client-output-buffer-limit.html)
```
127.0.0.1:6379> config get client-output-buffer-limit
1) "client-output-buffer-limit"
2) "normal 0 0 0 slave 268435456 67108864 60 pubsub 33554432 8388608 60"
127.0.0.1:6379>
```


- note：client-output-buffer-limit 也用在master、slave关联上面配置项为
  - https://www.jianshu.com/p/4fbbd30d8e55
  - client-output-buffer-limit slave 256MB 64MB 60
  - 在全量复制阶段，主节点会将执行的写命令放到复制缓冲区中，该缓冲区存放的数据包括了以下几个时间段内主节点执行的写命令：bgsave生成RDB文件、RDB文件由主节点发往从节点、从节点清空老数据并载入RDB文件中的数据。
    - 当主节点数据量较大，或者主从节点之间网络延迟较大时，可能导致该缓冲区的大小超过了限制，此时主节点会断开与从节点之间的连接；这种情况可能引起全量复制→复制缓冲区溢出导致连接中断→重连→全量复制→复制缓冲区溢出导致连接中断……的循环。
    - from https://juejin.im/entry/5b90a96a5188255c48348e15 
    - 就是参数client-output-buffer-limit slave 256MB 64MB 60

更多的关于client buffer
https://redislabs.com/blog/top-redis-headaches-for-devops-client-buffers/  
本文还列出了一个很有用的关于各种不同条件下latency的数值，譬如cpu level1 cpu cache 的latency为0.5纳秒

# Redis的危险操作-KEYS等
本文提到了好几个https://redislabs.com/blog/top-redis-headaches-for-devops-client-buffers/  
”KEYS is not the only command that can cause this scenario, however. Similarly, Redis’ SMEMBERS, HGETALL, LRANGE and ZRANGE (and associated commands) “    

# Redis过期
## Redis key过期设置
- 通过expire keyname num 设定keyname num秒后过期
```
$ docker exec -it baoying-redis redis-cli
127.0.0.1:6379> set key1 5
OK
127.0.0.1:6379> expire key1 15
(integer) 1
127.0.0.1:6379> ttl key1
(integer) 11
127.0.0.1:6379> ttl key1
(integer) 9
127.0.0.1:6379> ttl key1
(integer) -2 小于0，已经过期
```

## Redis map不支持sub key过期，但是有一些折衷方案讨论
- 首先redis不支持map过期
- 一般根据具体业务情况使用各种折衷方式
  - 譬如：map key同时设定一个额外的普通key，普通key过期callback用于删除mapkey。不过这个callback可能需要写lua脚本或者自己启动单独进程去操作
  - 有一些Redis的fork版本支持map key过期功能
  - client本地维护这个hashmap，本地轮询自己的map，发现过期后删除远端map。重启的时候，清除所有也不会造成redis内容爆掉
    - 这个纯粹看业务要求了

这个连接中简单说了不支持，重要的是给出了一个链接（这里很多讨论，包括一个workaround的详细讨论-结合提出问题人的详细场景：redis保存登录人员的session，希望通过这个ttl自动过期session）    
https://stackoverflow.com/questions/22954851/how-to-expire-a-key-of-a-map-in-redis    

  