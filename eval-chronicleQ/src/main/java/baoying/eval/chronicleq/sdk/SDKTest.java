package baoying.eval.chronicleq.sdk;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class SDKTest {

    @Test
    public void testNonBlockingRead(){

        String path = "/tmp/chronicle_sdk_q";
        String messageName = "trade";

        OffHeapQProducer producer = OffHeapQProducer.create(path, messageName);
        producer.write("hello 001".getBytes());
        producer.write("hello 002".getBytes());
        producer.write("hello 003".getBytes());
        producer.close();

        OffHeapQConsumer consumer = OffHeapQConsumer.create(path, messageName, "consumer001");

        Optional<byte[]> optionalBytes = consumer.read();
        Assert.assertTrue(optionalBytes.isPresent());
        Assert.assertEquals("", "hello 001", new String(optionalBytes.get()));

        optionalBytes = consumer.read();
        Assert.assertTrue(optionalBytes.isPresent());
        Assert.assertEquals("", "hello 002", new String(optionalBytes.get()));

        optionalBytes = consumer.read();
        Assert.assertTrue(optionalBytes.isPresent());
        Assert.assertEquals("", "hello 003", new String(optionalBytes.get()));

        optionalBytes = consumer.read();
        Assert.assertFalse(optionalBytes.isPresent());
        consumer.close();

    }

    @Test
    public void testBlockingSubscribeRead() throws Exception{

        //唯一的path，是为了避免前后消息的影响
        String path = "/tmp/chronicle_sdk_q_"+System.nanoTime();
        String messageName = "trade";

        OffHeapQProducer producer = OffHeapQProducer.create(path, messageName);
        producer.write("hello 001".getBytes());
        producer.write("hello 002".getBytes());
        producer.write("hello 003".getBytes());
        producer.close();

        OffHeapQConsumer consumer = OffHeapQConsumer.create(path, messageName, "consumer001");
        AtomicInteger counter = new AtomicInteger();

        //等待一小会儿，关闭等待
        new Thread(()->{
            long start = System.currentTimeMillis();
            while(System.currentTimeMillis() - start < 999 && counter.get() < 3) {
            }
            consumer.stopBlockSubscribe();

        }).start();

        consumer.blockingSubscribe((byte[] data)->{
            int index = counter.incrementAndGet();
            switch (index){
                case 1:Assert.assertEquals("", "hello 001", new String(data)); break;
                case 2:Assert.assertEquals("", "hello 002", new String(data)); break;
                case 3:Assert.assertEquals("", "hello 003", new String(data)); break;
                default:
                    Assert.fail("meet unexpected message num:"+index);
            }
        });

        Assert.assertEquals("", 3, counter.get());
        consumer.close();

    }
}
