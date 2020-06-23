package baoying.eval.redis;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class RedisSub {

    public static void main(String[] args) throws Exception{

        Jedis jedis = new Jedis("localhost");
        //jedis.auth("your_auth");

        JedisPubSubLogger jedisPubSubLogger = new JedisPubSubLogger();

        new Thread(()->{
            try{
                TimeUnit.SECONDS.sleep(30);
                jedisPubSubLogger.unsubscribe();

                TimeUnit.SECONDS.sleep(1);
                jedis.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();


        //这个地方是blocking的
        //jedis.subscribe(new JedisPubSubLogger(), "channel01");
        jedis.subscribe(jedisPubSubLogger, new String[]{"channel01","channel02"});
        //jedis.psubscribe(new JedisPubSubLogger(), "channel??");

        TimeUnit.SECONDS.sleep(30);


    }
}
