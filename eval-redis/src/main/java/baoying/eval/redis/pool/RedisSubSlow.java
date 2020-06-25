package baoying.eval.redis.pool;
import baoying.eval.redis.JedisPubSubLogger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RedisSubSlow {

    private JedisPool jedisPool;

    public RedisSubSlow(JedisPool jedisPool){
        this.jedisPool = jedisPool;
    }

    public void blockingProcess() throws Exception{

        AtomicLong receivedMessageCount = new AtomicLong(0);
        JedisPubSub jedisPubSubLogger = new JedisPubSub(){
            @Override
            public void onMessage(String channel, String message) {
                long receivedCount = receivedMessageCount.incrementAndGet();

                //这里打印的线程名称为：Main，就是当前线程！
                System.out.println(Instant.now().toString()
                        + " onMessage - Thread:"+Thread.currentThread().getName()
                        +", channel:"+channel
                        +", message.len:" + message.length()
                        + ", count:" + receivedMessageCount);

                try {
                    TimeUnit.SECONDS.sleep(1);
                    //TimeUnit.MILLISECONDS.sleep(1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

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

        new RedisSubSlow(jedisPool).blockingProcess();

    }
}

