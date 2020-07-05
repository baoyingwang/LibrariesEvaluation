package baoying.eval.chronicleq.sdk;

import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;


/**
 *
 * - 包装ChronicleQueue，并固定使用其中self described消息的方式
 * - 这个简化的初衷是所有数据都用bytes，因为使用这个系统的用户将极大关注heap
 * - 如果用户有更加特有的需求，则自己重新封装一下就好了
 *
 */
public class OffHeapQProducer {

    private String path;
    private String messageName;
    private ChronicleQueue queue;
    private ExcerptAppender appender;

    private OffHeapQProducer(String path, String messageName){
        this.path = path;
        this.messageName = messageName;
    }

    private void init(){
         queue = ChronicleQueue.singleBuilder(path).build();
         appender = queue.acquireAppender();
    }

    public static OffHeapQProducer create(String path, String messageName){
        OffHeapQProducer offHeapQProducer = new OffHeapQProducer(path, messageName);
        offHeapQProducer.init();

        return offHeapQProducer;
    }

    public void close(){
        silentClose(appender);
        silentClose(queue);
    }

    private void silentClose(Closeable c){
        try{
            c.close();
        }catch (Exception ignore){

        }
    }

    public void write(byte[] data){
        appender.writeDocument(w -> w.write(this.messageName).marshallable(
                m -> m.write("data").bytes(data)
        ));
    }

}
