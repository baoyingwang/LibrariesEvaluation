package baoying.eval.redis.pubsub;
import baoying.eval.redis.JedisPubSubLogger;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RedisSubSlow {

    public static void main(String[] args) throws Exception{

        Jedis jedis = new Jedis("localhost");

        AtomicLong counter = new AtomicLong(0);
        //这个地方是blocking的
        jedis.subscribe(new JedisPubSubLogger(){
            @Override
            public void onMessage(String channel, String message) {
                //这里打印的线程名称为：Main，就是当前线程！
                counter.incrementAndGet();
                System.out.println(Instant.now().toString()+ " onMessage - Thread:"+Thread.currentThread().getName()+", channel:"+channel +", message.length:" + message.length() + ", #"+counter.get());
                try{
                    TimeUnit.SECONDS.sleep(1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "channel01");

        TimeUnit.SECONDS.sleep(30);


    }
}
