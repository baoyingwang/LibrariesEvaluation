package baoying.perf.network.vertxtcp;

import io.vertx.core.buffer.Buffer;

public class VertxUtil {

    public static Buffer getB(long data, int msgSize){
        Buffer b = Buffer.buffer();

        b.appendLong(data);
        int initLength = b.length();
        for(int j=0; j< msgSize - initLength; j ++ ){
            b.appendByte((byte)'x');
        }

        return b;
    }

    public static Buffer getB(long data, int msgSize, String end){
        Buffer b = Buffer.buffer();

        b.appendLong(data);
        int initLength = b.length();
        for(int j=0; j< msgSize - initLength-1; j ++ ){
            b.appendByte((byte)'x');
        }
        b.appendString(end);

        return b;
    }

    public static Buffer getB(long data1,long data2, int msgSize){
        Buffer b = Buffer.buffer();

        b.appendLong(data1);
        b.appendLong(data2);
        int initLength = b.length();
        for(int j=0; j< msgSize - initLength; j ++ ){
            b.appendByte((byte)'x');
        }

        return b;
    }

    public static Buffer getB(long data1,long data2, int msgSize, String end){
        Buffer b = Buffer.buffer();

        b.appendLong(data1);
        b.appendLong(data2);
        int initLength = b.length();
        for(int j=0; j< msgSize - initLength-1; j ++ ){
            b.appendByte((byte)'x');
        }
        b.appendString(end);

        return b;
    }
}
