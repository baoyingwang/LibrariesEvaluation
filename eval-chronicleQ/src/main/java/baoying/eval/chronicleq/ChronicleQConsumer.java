package baoying.eval.chronicleq;

import net.openhft.chronicle.bytes.MethodReader;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueExcerpts;
import net.openhft.chronicle.threads.Pauser;

import javax.swing.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class ChronicleQConsumer {

    public void nonblockingConsumeTextSample(String path){

        System.out.println("===nonblockingConsumeTextSample===");

        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(path).build()) {

            //Restartable Tailers -给tailer一个名字，这样重启的时候重新读取
            //如果不给它名字，则每次都从头读取
            final ExcerptTailer tailer = queue.createTailer("nonblockingConsumeTextSample-as-name");

            int counter = 0;
            StringBuilder textMsg = new StringBuilder();
            while (tailer.readText(textMsg)) {
                //也可以
                //textMsg = tailer.readText()
                //if(textMsg == null){
                //    break;
                //}
                counter++;
                System.out.println("got msg index:" + tailer.index() + ", counter:"+counter+", msg:" + textMsg);
            }
            ((SingleChronicleQueueExcerpts.StoreTailer)tailer).releaseResources();
        }
    }

    public void nonblockingConsumeSelfDescribingMessage(String path){

        System.out.println("===nonblockingConsumeSelfDescribingMessage===");
        //Restartable Tailers -给tailer一个名字，这样重启的时候重新读取
        //如果不给它名字，则每次都从头读取
        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(path).build();
             ExcerptTailer tailer = queue.createTailer("nonblockingConsumeSelfDescribingMessage-as-name");) {

            AtomicInteger counter = new AtomicInteger();
            while (true) {

                boolean got = tailer.readDocument(w -> w.read("trade").marshallable(
                        m -> {
                            //注意：必须按照写入的顺序挨个读取，不能跳过。否则消息就丢失了
                            //- 消息丢失是这个的默认处理策略，可以看一下这个方法（tailer.readDocument）对应的ReadMarshallable的默认实现
                            //- 鉴于这种严格的消息检查模式，不建议这么使用。更加建议使用json字符串方式封装。
                            LocalDateTime timestamp = m.read("timestamp").dateTime();
                            String symbol = m.read("symbol").text();
                            double price = m.read("price").float64();
                            double quantity = m.read("quantity").float64();
                            String side = m.read("side").text();
                            String trader = m.read("trader").text();

                            counter.incrementAndGet();
                            // do something with values.

                            System.out.println("got msg index:" + tailer.index() + ", counter:"+counter.get()+", msg:" +timestamp.toString()+"," +symbol+","+side+","+price+","+quantity+","+trader);

                        }));

                if(!got){
                    break;
                }
            }
            ((SingleChronicleQueueExcerpts.StoreTailer)tailer).releaseResources();

        }

    }

    /**
     * 关键点有3个
     * 1. tailer要有名字，否则每次都从头开始
     * 2. 获取当前index，并往前移动一个位置
     * 3. 如果当前index已经是第一个(index==0)，则不要移动
     *
     * WARN: theEnd的含义是当前tailer（有名字）上次消费到的位置，而不是当前queue的最后一个元素
     * 譬如：
     * - 发送和消费了a,b,c
     * - consumer down
     * - produce 发送d,e,f,g
     * - consumer启动之后，默认从d开始消费。
     *   - 为了避免上次的c没有处理完consumer可以将c再次消费一次
     *   - 方法就是获取consumer当前index(指向d)，然后向前移动一个位置(指向c)，然后再开始消费就从c开始了
     */
    public void reConsumeTheEndSample(String path){

        System.out.println("===reConsumeTheEndSample===");

        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(path).build()) {

            //Restartable Tailers -给tailer一个名字，这样重启的时候重新读取
            //如果不给它名字，则每次都从头读取
            final ExcerptTailer tailer = queue.createTailer("c");
            if(tailer.index() == 0){
                System.out.println("start of the queue, don't move back");
            }else{
                tailer.moveToIndex(tailer.index()-1);
            }

            int counter = 0;
            while (true) {
                String textMsg = tailer.readText();
                if(textMsg == null){
                    break;
                }
                counter++;
                System.out.println("got msg index:" + tailer.index() + ", counter:"+counter+", msg:" + textMsg);
            }
            ((SingleChronicleQueueExcerpts.StoreTailer)tailer).releaseResources();
        }
    }

    public void nonblockingConsumeBytesInSelfDescribeSample(String path){


        System.out.println("===nonblockingConsumeBytesInSelfDescribeSample===");
        //Restartable Tailers -给tailer一个名字，这样重启的时候重新读取
        //如果不给它名字，则每次都从头读取
        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(path).build();
             ExcerptTailer tailer = queue.createTailer("nonblockingConsumeBytesInSelfDescribeSample-as-name");) {

            AtomicInteger counter = new AtomicInteger();
            while (true) {

                boolean got = tailer.readDocument(w -> w.read("trade").marshallable(
                        m -> {
                            byte[] data = m.read("data").bytes();
                            counter.incrementAndGet();

                            System.out.println("got msg index:" + tailer.index() + ", counter:"+counter.get()+", msg:" +new String(data));

                        }));

                if(!got){
                    break;
                }
            }
            ((SingleChronicleQueueExcerpts.StoreTailer)tailer).releaseResources();

        }
    }

    public void pauseReader(String path){

        String methodName= "pauseReader";
        System.out.println("==="+methodName+"===");

        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(path).build()) {

            //Restartable Tailers -给tailer一个名字，这样重启的时候重新读取
            //如果不给它名字，则每次都从头读取
            final ExcerptTailer tailer = queue.createTailer(methodName+"-as-name");

            //Pauser pauser = Pauser.balanced();
            //https://github.com/OpenHFT/Chronicle-Queue#is-there-an-appender-to-tailer-notification
            boolean balanced = true;
            Pauser pauser = balanced ? Pauser.balanced() : Pauser.millis(1, 10);
            boolean closed = false;
            AtomicInteger counter = new AtomicInteger();
            while (!closed) {
                boolean got = tailer.readDocument(w -> w.read("trade").marshallable(
                        m -> {
                            byte[] data = m.read("data").bytes();
                            counter.incrementAndGet();

                            System.out.println("got msg index:" + tailer.index() + ", counter:"+counter.get()+",client recv time:"+ Instant.now().toString() +", msg:" +new String(data));

                        }));

                if (got){
                    pauser.reset();
                }else{
                    pauser.pause();
                }

            }
            ((SingleChronicleQueueExcerpts.StoreTailer)tailer).releaseResources();
        }
    }

    public static void main(String[] args){

        ChronicleQConsumer consumer = new ChronicleQConsumer();
        consumer.nonblockingConsumeTextSample(WriteConfig.TEXT.file);
        consumer.reConsumeTheEndSample(WriteConfig.TEXT.file);
        consumer.nonblockingConsumeSelfDescribingMessage(WriteConfig.SELF_DESCRIBE.file);
        consumer.nonblockingConsumeBytesInSelfDescribeSample(WriteConfig.BYTES_IN_SELF_DESCRIBE.file);
        consumer.pauseReader(WriteConfig.BYTES_IN_SELF_DESCRIBE.file);
    }
}
