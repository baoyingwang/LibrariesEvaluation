package baoying.eval.chronicleq;


import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;

import java.time.Instant;
import java.time.ZoneOffset;

public class ChronicleQProducer {

    public void produceSampleText(String path){

        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(path).build()) {

            final ExcerptAppender appender = queue.acquireAppender();
            //3条消息
            appender.writeText("hello world");
            appender.writeText("hello China");
            appender.writeText("hello America");
            appender.close();
        }
    }

    public void produceSelfDescribingMessage(String path){

        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(path).build();
             ExcerptAppender appender = queue.acquireAppender()) {

            for(int i=0; i<5; i++) {
                appender.writeDocument(w -> w.write("trade").marshallable(
                        m -> m.write("timestamp").dateTime(Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime())
                                .write("symbol").text("EURUSD")
                                .write("price").float64(1.1101)
                                .write("quantity").float64(15e6)
                                .write("side").text("Buy")
                                .write("trader").text("peter")
                ));
            }
            //dump并不会影响消费，这个就是看一眼而已
            //String queueContent = queue.dump();
            //System.out.println(queueContent);
        }
    }

    public void produceSampleBytesInDescribed(String path){


        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(path).build();
             ExcerptAppender appender = queue.acquireAppender()) {

            for(int i=0; i<5; i++) {
                String data = "{version:'1.0', timestamp:"+Instant.now().toString()+", symbol:'IBM', qty:'1500', price:350.12, trader:'baoying'}";
                appender.writeDocument(w -> w.write("trade").marshallable(
                        m -> m.write("data").bytes(data.getBytes())
                ));
            }
            //dump并不会影响消费，这个就是看一眼而已
            //String queueContent = queue.dump();
            //System.out.println(queueContent);
        }
    }

    public void produceSampleCode(String path){

        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(path).build();
             ExcerptAppender appender = queue.acquireAppender();) {

            appender.writeText("hello world");

            // writing a self describing message
            appender.writeDocument(w -> w.write("trade").marshallable(
                    m -> m.write("timestamp").dateTime(Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime())
                            .write("symbol").text("EURUSD")
                            .write("price").float64(1.1101)
                            .write("quantity").float64(15e6)
                            //.write("side").object(Side.class, Side.Sell)
                            .write("trader").text("peter")));

            // writing just data
            appender.writeDocument(w -> w
                    .getValueOut().int32(0x123456)
                    .getValueOut().int64(0x999000999000L)
                    .getValueOut().text("Hello World"));

            // writing raw data
            appender.writeBytes(b -> b
                    .writeByte((byte) 0x12)
                    .writeInt(0x345678)
                    .writeLong(0x999000999000L)
                    .writeUtf8("Hello World"));

            // Unsafe low level - 编译不过去
//            appender.writeBytes(b -> {
//                long address = b.address(b.writePosition());
//                Unsafe unsafe = UnsafeMemory.UNSAFE;
//                unsafe.putByte(address, (byte) 0x12);
//                address += 1;
//                unsafe.putInt(address, 0x345678);
//                address += 4;
//                unsafe.putLong(address, 0x999000999000L);
//                address += 8;
//                byte[] bytes = "Hello World".getBytes(StandardCharsets.ISO_8859_1);
//                unsafe.copyMemory(bytes, Jvm.arrayByteBaseOffset(), null, address, bytes.length);
//                b.writeSkip(1 + 4 + 8 + bytes.length);
//            });
        }
    }


    public static void main(String[] args){

        ChronicleQProducer producer = new ChronicleQProducer();
        producer.produceSampleText(WriteConfig.TEXT.file);
        producer.produceSelfDescribingMessage(WriteConfig.SELF_DESCRIBE.file);
        for(int i=0; i<255;i++) {
            producer.produceSampleBytesInDescribed(WriteConfig.BYTES_IN_SELF_DESCRIBE.file);
        }

    }


}
