package baoying.eval.redis.pool;
import baoying.eval.redis.JedisPubSubLogger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

public class RedisSubSocat {

    public static void main(String[] args) throws Exception{

        JedisPoolEval jedisPoolEval = new JedisPoolEval("localhost", 16379, null);
        JedisPool jedisPool = jedisPoolEval.getPool();

        new RedisSub(jedisPool).blockingProcess();

    }
}
