[TOC]
# redis docker
https://hub.docker.com/_/redis/

# 本地启动docker
本地启动一个简单redis
```
docker run --name baoying-redis -p 6379:6379 -d redis
```
- warn:这个没有设置任何安全认证，就是为了POC使用

# 进入docker 内部的cli
- 直接执行redis-cli ：docker exec -it baoying-redis redis-cli
- 或者进去之后执行redis-cli

参考：https://stackoverflow.com/questions/54205691/access-redis-cli-inside-a-docker-container


## more https://redis.io/topics/rediscli  这里很多操作，还包括交互式的


redis build and install
https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-redis-on-ubuntu-16-04


start: see above link
 /usr/local/bin/redis-server /etc/redis/redis.conf
对map的一些操作
```
get all values of a map
hgetall map_name
hgetall "ltc_unconfirmed_tx"

get a value of the map
hmget map_name a_key_of_map
hmget "ltc_unconfirmed_tx" 8b68053326b752a0a

length of a map
hlen map_name
hlen "ltc_unconfirmed_tx"
```