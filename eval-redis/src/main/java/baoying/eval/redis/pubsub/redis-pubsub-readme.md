# Pub/Sub模型
- 可以多个Sub消费同一个channel，每个sub接收到相同的一份copy
  - 即：启动两个RedisSub，然后发送可以看到两个Sub收到了相同的消息
- 其不能保证消息不丢失
  - 如果某client未处理消息超过了限定大小client-output-buffer-limit pubsub，则该client被断开。断开期间的消息也就丢弃了
  - 下面section有更详细的关于这个参数的介绍

## AsyncSub

异步订阅没有支持-https://github.com/xetorthio/jedis/issues/241 - 这里都讨论到2019年了
也是讨论https://github.com/xetorthio/jedis/pull/713 到了2020年3月19都不支持

  
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

- 更多的关于client buffer
  - https://redislabs.com/blog/top-redis-headaches-for-devops-client-buffers/  
  - 本文还列出了一个很有用的关于各种不同条件下latency的数值，譬如cpu level1 cpu cache 的latency为0.5纳秒


- 譬如处理不及时，积压？？？这可看 
  https://redislabs.com/ebook/part-2-core-concepts/chapter-3-commands-in-redis/3-6-publishsubscribe/client-output-buffer-limit 
- 这里更详细https://www.alibabacloud.com/help/doc-detail/142055.htm
  - “Modern versions of Redis don’t have this issue, and will disconnect subscribed clients that are unable to keep up with the client-output-buffer-limit pubsub configuration option (which we’ll talk about in chapter 8).”
   - 新版本的redis将把慢客户端断掉，这来自第3章，它说第8章内容更多介绍（但是并没有找到）
   - from https://redislabs.com/ebook/part-2-core-concepts/chapter-3-commands-in-redis/3-6-publishsubscribe/

## 写sub代码注意，将所有的subscribe/unsubscribe/psubscribe/punsubscribe都log下来（info级别）
- 这是用来记录开始订阅和停止订阅的事件
- 如果发生了网络问题进行重新链接，则会记录这些信息
- 对定位问题非常重要
- 代码位置：订阅时候扩展的JedisPubSub子类
  - 我一般是自己写一个默认的打印log类，然后业务中都直接再继承它