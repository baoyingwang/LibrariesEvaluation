package baoying.eval.redis.pubsub;
import baoying.eval.redis.JedisPubSubLogger;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class RedisSub {

    public static void main(String[] args) throws Exception{

        Jedis jedis = new Jedis("localhost", 6379);
        //jedis.auth("your_auth");

        JedisPubSubLogger jedisPubSubLogger = new JedisPubSubLogger();

        new Thread(()->{
            try{
                TimeUnit.SECONDS.sleep(300);
                jedisPubSubLogger.unsubscribe();

                TimeUnit.SECONDS.sleep(1);
                jedis.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

        //这个地方是blocking的，调用的函数（jedisPubSubLogger）中可以看到，其线程还是Main线程，即当前线程！不是在Jedis线程中
        jedis.subscribe(jedisPubSubLogger, new String[]{"channel01","channel02"});
        //jedis.subscribe(new JedisPubSubLogger(), "channel01");
        //jedis.psubscribe(new JedisPubSubLogger(), "channel??");

        TimeUnit.SECONDS.sleep(30);


    }
}
