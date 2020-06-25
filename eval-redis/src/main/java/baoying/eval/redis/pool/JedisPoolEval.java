package baoying.eval.redis.pool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolEval {

    private String redisHost = "localhost";
    private int redisPort = 16379;
    private String redisPassword = null;

    public JedisPoolEval(String host, int port, String password){
        this.redisHost = host;
        this.redisPort = port;
        this.redisPassword = password;
    }

    public JedisPool getPool(){

        JedisPoolConfig  jedisPoolConfig = new JedisPoolConfig();
        //见上面url中描述的这些参数
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(3);
        jedisPoolConfig.setMinIdle(1);
        jedisPoolConfig.setMaxWaitMillis(5) ;

        int timeout = 5 * 1000;

        // redisHost indicates the instance IP address. redisPort indicates the instance port.
        // redisPassword indicates the password of the instance. The timeout parameter indicates both the connection timeout and the read/write timeout.
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort, timeout, redisPassword);

        return jedisPool;

    }

    /**
     * JedisPool optimization https://www.alibabacloud.com/help/doc-detail/98726.htm
     * @param args
     */
    public static void main(String[] args){
        JedisPoolEval jedisPoolEval = new JedisPoolEval("localhost", 16379, null);

        JedisPool jedisPool = jedisPoolEval.getPool();
        //Run the command as follows:
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // Specific commands
            jedis.publish("channel01", "value1");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //In JedisPool mode, the Jedis resource will be returned to the resource pool.
            if (jedis != null) {
                jedis.close();
            }
        }

    }
}
