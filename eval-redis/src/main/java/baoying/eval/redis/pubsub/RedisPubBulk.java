package baoying.eval.redis.pubsub;
import redis.clients.jedis.Jedis;

import java.time.Instant;
public class RedisPubBulk {

    public static void main(String[] args) throws Exception{

        //Jedis jedis = new Jedis("localhost");
        Jedis jedis = new Jedis("localhost", 16379);

        int repeat = 128;
        String bulkString = createLongStr(1024*1024);
        for(int i=0; i<repeat; i++){

            jedis.publish("channel01", Instant.now().toString()+"-"+bulkString);
            System.out.println("sent - #"+i);

            if( (i+1) %100 == 0) {
                System.out.println("print enter to continue");
                System.in.read();
            }
        }

        jedis.close();
    }

    static String createLongStr(int len){

        StringBuilder s = new StringBuilder(len);
        for(int i=0; i<len; i++){
            s.append("0");
        }
        return s.toString();
    }
}
