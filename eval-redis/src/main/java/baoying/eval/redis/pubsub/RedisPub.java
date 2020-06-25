package baoying.eval.redis.pubsub;
import redis.clients.jedis.Jedis;
public class RedisPub {

    public static void main(String[] args){

        //Jedis jedis = new Jedis("localhost");
        Jedis jedis = new Jedis("localhost", 16379);

        for(int i=0; i<10; i++){
            jedis.publish("channel01", "bar-"+i);
            jedis.publish("channel02", "bar-"+i);
        }

        jedis.close();
    }
}
