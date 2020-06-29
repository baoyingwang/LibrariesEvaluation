# 介绍
基本java编码
https://www.baeldung.com/jedis-java-redis-client-library
注意：其中介绍的subscribe为blocking（上面连接中也明确写了）
   
# Redis的危险操作-KEYS等
本文提到了好几个https://redislabs.com/blog/top-redis-headaches-for-devops-client-buffers/  
”KEYS is not the only command that can cause this scenario, however. Similarly, Redis’ SMEMBERS, HGETALL, LRANGE and ZRANGE (and associated commands) “    
  