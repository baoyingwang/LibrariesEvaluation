# 介绍
基本java编码
https://www.baeldung.com/jedis-java-redis-client-library
注意：其中介绍的subscribe为blocking（上面连接中也明确写了）
   
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

  