package baoying.eval.redis.pubsub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CountDownLatch;

/**
 * 用非常tricky的方式，让jedis完成异步返回
 * WARN：这里只是一种尝试，别这么做，因为与Jedis自身设计意图不符。
 * Jedis真正支持了异步再用官方支持的方法吧
 */
public class AsyncSub {

    public static void main(String[] args) throws Exception{

        String channel = "channel01";
        Jedis jedis = new Jedis("localhost");


        CountDownLatch latch = new CountDownLatch(1);
        new Thread(()->{
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    System.out.println("Tricky - Thread:"+Thread.currentThread().getName()+" channel:"+channel +" message:" + message);
                }
            }, channel);
            latch.countDown();
        }).start();
        latch.await();

        JedisPubSubImpl pubSub = new JedisPubSubImpl();
        pubSub.subscribe(channel);

        System.out.println("sleeping");
        Thread.sleep(300* 1000);

    }

    static class JedisPubSubImpl extends JedisPubSub{

        @Override
        public void onMessage(String channel, String message) {
            System.out.println("Thread:"+Thread.currentThread().getName()+" channel:"+channel +" message:" + message);
        }

        @Override
        public void onPMessage(String pattern, String channel, String message) {
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
        }

        @Override
        public void onUnsubscribe(String channel, int subscribedChannels) {
        }

        @Override
        public void onPUnsubscribe(String pattern, int subscribedChannels) {
        }

        @Override
        public void onPSubscribe(String pattern, int subscribedChannels) {
        }

        @Override
        public void onPong(String pattern) {
        }
    }
}
