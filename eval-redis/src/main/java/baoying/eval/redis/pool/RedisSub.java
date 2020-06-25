package baoying.eval.redis.pool;
import baoying.eval.redis.JedisPubSubLogger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

public class RedisSub {

    private JedisPool jedisPool;

    public RedisSub(JedisPool jedisPool){
        this.jedisPool = jedisPool;
    }

    public void blockingProcess() throws Exception{

        JedisPubSubLogger jedisPubSubLogger = new JedisPubSubLogger();

        while(true){
            Jedis jedis = null;
            try{
                //每次从pool重连从pool中获取一个好用的jedis对象
                jedis =  jedisPool.getResource();

                //这个地方是blocking的
                jedis.subscribe(jedisPubSubLogger, new String[]{"channel01","channel02"});
            }catch (Exception e){
                e.printStackTrace();
                if(jedis != null){
                    jedis.close();
                }
            }

            //链接出了问题才到这里，等一会重连
            TimeUnit.SECONDS.sleep(3);
        }
    }

    public static void main(String[] args) throws Exception{

        JedisPoolEval jedisPoolEval = new JedisPoolEval("localhost", 6379, null);
        JedisPool jedisPool = jedisPoolEval.getPool();

        new RedisSub(jedisPool).blockingProcess();

    }
}
