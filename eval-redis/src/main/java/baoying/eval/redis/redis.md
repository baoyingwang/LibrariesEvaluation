基本java编码
https://www.baeldung.com/jedis-java-redis-client-library

注意：其中介绍的subscribe为blocking（上面连接中也明确写了）

异步订阅没有支持-https://github.com/xetorthio/jedis/issues/241 - 这里都讨论到2019年了
也是讨论https://github.com/xetorthio/jedis/pull/713 到了2020年3月19都不支持