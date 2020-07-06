package baoying.eval.redis.pubsub;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.util.Scanner;
public class RedisPubInteractive {

    public static void main(String[] args){

        Jedis jedis = new Jedis("localhost",6379);

        while(true){

            //用scannner而非System.console().readLine()，因为IDE用javaw.exe而非java
            //https://stackoverflow.com/questions/26470972/trying-to-read-from-the-console-in-java
            System.out.println(Instant.now()+" input channel and data, e.g. channel1,data0001");
            Scanner scanner = new Scanner(System.in);
            String[] channelAndData = scanner.nextLine().split(",");
            String channel = channelAndData[0];
            String data = channelAndData[1];
            jedis.publish(channel, data);

            System.out.println(Instant.now()+" published data to:" + channel +" data:"+data);
        }
    }
}
