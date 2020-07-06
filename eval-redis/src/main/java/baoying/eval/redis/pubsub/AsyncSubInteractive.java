package baoying.eval.redis.pubsub;
import baoying.eval.redis.JedisPubSubLogger;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.Console;
import java.time.Instant;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

/**
 * 用非常tricky的方式，让jedis完成异步返回
 * WARN：这里只是一种尝试，别这么做，因为与Jedis自身设计意图不符。
 * Jedis真正支持了异步再用官方支持的方法吧
 */
public class AsyncSubInteractive {

    public static void main(String[] args) throws Exception{

        final String initChannel = "channel01";
        Jedis jedis = new Jedis("localhost", 16379);


        JedisPubSubLogger pubSub = new JedisPubSubLogger();
        new Thread(()->{
            jedis.subscribe(pubSub, initChannel);
        },"Init Redis Thread").start();
        Thread.sleep(1000);

        Set<String> channels = new TreeSet<>();
        channels.add(initChannel);
        System.out.println("subscribed init channel:" + initChannel);
        while(true){

            //用scannner而非System.console().readLine()，因为IDE用javaw.exe而非java
            //https://stackoverflow.com/questions/26470972/trying-to-read-from-the-console-in-java
            System.out.println(Instant.now()+" input new channel to subscribed");
            Scanner scanner = new Scanner(System.in);
            String channel = scanner.nextLine();
            if(channels.contains(channel)){
                System.out.println(Instant.now()+" skip since already subscribed:" + channel);
            }else{
                pubSub.subscribe(channel);
                System.out.println(Instant.now()+" subscribed:" + channel);
            }

            channels.add(channel);
        }

    }

    static class JedisPubSubImpl extends JedisPubSub{

        @Override
        public void onMessage(String channel, String message) {
            System.out.println(Instant.now()+" Thread:"+Thread.currentThread().getName()+" channel:"+channel +", message:" + message);
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
