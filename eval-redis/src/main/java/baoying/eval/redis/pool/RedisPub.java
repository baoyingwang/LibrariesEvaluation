package baoying.eval.redis.pool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.swing.plaf.IconUIResource;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
public class RedisPub {

    public static void main(String[] args) throws Exception{

        JedisPoolEval jedisPoolEval = new JedisPoolEval("localhost", 6379, null);
        JedisPool jedisPool = jedisPoolEval.getPool();

        Jedis jedis =  jedisPool.getResource();

        int total = 15;
        for(int i=0; i<total; i++){
            jedis.publish("channel01", Instant.now()+"-bar-"+i);
            jedis.publish("channel02", Instant.now()+"-bar-"+i);

            TimeUnit.MILLISECONDS.sleep(50);
        }
        System.out.println("send:" + total +" message to redis");
        jedis.close();
    }
}
