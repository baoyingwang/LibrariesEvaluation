package baoying.eval.redis;
import redis.clients.jedis.JedisPubSub;

import java.time.Instant;
public class JedisPubSubLogger extends JedisPubSub {

    /**
     * 通过普通订阅（非channel pattern）得到消息
     * @param channel
     * @param message
     */
    @Override
    public void onMessage(String channel, String message) {
        //这里打印的线程名称为：Main，就是当前线程！
        System.out.println(Instant.now().toString()+ " onMessage - Thread:"+Thread.currentThread().getName()+", channel:"+channel +", message:" + message);

    }

    /**
     * 通过channel的pattern匹配而受到的消息
     * @param pattern
     * @param channel
     * @param message
     */
    @Override
    public void onPMessage(String pattern, String channel, String message) {
        System.out.println(Instant.now().toString()+ " onPMessage - Thread:"+Thread.currentThread().getName()+", pattern:"+pattern + ", channel:"+channel+", message:" + message);
    }

    /**
     * 普通订阅（非pattern方式）订阅这个动作完成，执行这个操作
     * @param channel
     * @param subscribedChannels
     */
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println(Instant.now().toString()+ " onSubscribe - Thread:"+Thread.currentThread().getName()+", channel:"+channel +", subscribedChannels:" + subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println(Instant.now().toString()+ " onUnsubscribe - Thread:"+Thread.currentThread().getName()+", channel:"+channel +", subscribedChannels:" + subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        System.out.println(Instant.now().toString()+ " onPUnsubscribe - Thread:"+Thread.currentThread().getName()+", pattern:"+pattern +", subscribedChannels:" + subscribedChannels);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        System.out.println(Instant.now().toString()+ " onPSubscribe - Thread:"+Thread.currentThread().getName()+", pattern:"+pattern +", subscribedChannels:" + subscribedChannels);
    }

    @Override
    public void onPong(String pattern) {
        System.out.println(Instant.now().toString()+ " onPong - Thread:"+Thread.currentThread().getName()+", pattern:"+pattern);
    }
}
