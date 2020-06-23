package baoying.eval.redis;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class RedisSubSimple {

    public static void main(String[] args) throws Exception{

        Jedis jedis = new Jedis("localhost");

        //这个地方是blocking的
        //jedis.subscribe(new JedisPubSubLogger(), "channel01");
        jedis.subscribe(new JedisPubSubLogger(){
            @Override
            public void onMessage(String channel, String message) {
                //这里打印的线程名称为：Main，就是当前线程！
                System.out.println(Instant.now().toString()+ " onMessage - Thread:"+Thread.currentThread().getName()+", channel:"+channel +", message:" + message);

            }
        }, "channel01");

        //jedis.psubscribe(new JedisPubSubLogger(), "channel??");

        TimeUnit.SECONDS.sleep(30);


    }
}
