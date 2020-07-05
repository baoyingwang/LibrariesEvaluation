package baoying.eval.chronicleq.sdk;

import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.threads.Pauser;

import java.util.Optional;
import java.util.function.Consumer;


/**
 * 包装ChronicleQueue，并固定使用其中self described消息的方式
 *
 */
public class OffHeapQConsumer {

    private String path;
    private String messageName;

    // 用于标识不同的消费者，这样他们重新读取的时候可以从自己上次的读取位置开始继续
    private String consumerName;

    private ChronicleQueue queue;
    private ExcerptTailer tailer;

    private OffHeapQConsumer(String path, String messageName, String consumerName){
        this.path = path;
        this.messageName = messageName;
        this.consumerName = consumerName;
    }

    private void init(){
         queue = ChronicleQueue.singleBuilder(path).build();
         tailer = queue.createTailer(consumerName);


    }

    public static OffHeapQConsumer create(String path, String messageName, String consumerName){
        OffHeapQConsumer offHeapQConsumer = new OffHeapQConsumer(path, messageName, consumerName);
        offHeapQConsumer.init();
        return offHeapQConsumer;
    }

    /**
     * 重新消费最后一条，因为有可能最后一条没有消费
     * 但是消费者需要明确重复处理不会有问题（幂等）
     * @return
     */
    public OffHeapQConsumer moveBackOneStep(){

        if(tailer.index() == 0){
            // System.out.println("start of the queue, don't move back");
        }else{
            tailer.moveToIndex(tailer.index()-1);
        }

        return this;
    }

    public void close(){
        silentClose(tailer);
        silentClose(queue);
    }

    private void silentClose(Closeable c){
        try{
            c.close();
        }catch (Exception ignore){

        }
    }

    private class BytesWrapper{
        byte[] data;
    }

    public Optional<byte[]> read(){

        BytesWrapper wrapper = new BytesWrapper();
        boolean got = tailer.readDocument(w -> w.read(this.messageName).marshallable(
                m -> {
                    wrapper.data = m.read("data").bytes();
                }));
        if(got){
            return Optional.of(wrapper.data);
        }else{
            return Optional.empty();
        }
    }

    private Thread blockingSubscribeThread = null;
    private volatile boolean blockingSubscribeStopped = false;
    /**
     * 和Redis的PubSub中的sub线程模型类似，当前线程blocking等待消息有则处理
     */
    public void blockingSubscribe(Consumer<byte[]> consumer){

        // 还有其他策略，如Pauser.busy();
        Pauser pauser =  Pauser.balanced();

        blockingSubscribeThread = Thread.currentThread();
        while (!blockingSubscribeStopped && !blockingSubscribeThread.isInterrupted()) {
            boolean got = tailer.readDocument(w -> w.read(this.messageName).marshallable(
                    m -> {
                        byte[] data = m.read("data").bytes();
                        consumer.accept(data);
                    }));

            if (got){
                pauser.reset();
            }else{
                pauser.pause();
            }

        }
    }

    public void stopBlockSubscribe(){
        blockingSubscribeStopped = true;
        blockingSubscribeThread.interrupt();
    }
}
