package baoying.eval.jdk.memory;

import javafx.scene.control.RadioMenuItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 按照本文说法
 * https://blog.csdn.net/freebird_lb/article/details/7460556
 */
public class StringSplitFeature {

    private Random r = new Random();
    private String getUniqLongString(int sizeAtLeast64){
        if(sizeAtLeast64 < 64){
            throw new IllegalArgumentException("too short and cannot promise uniq");
        }

        StringBuilder s = new StringBuilder();
        s.append(System.currentTimeMillis());
        s.append(System.nanoTime());
        s.append(r.nextInt());

        if(s.length()>sizeAtLeast64){
            throw new RuntimeException("internal issue, cannot generate the string");
        }

        int appendLen = sizeAtLeast64 - s.length();
        for(int i =0; i<appendLen; i++){
            s.append('0');
        }


        return s.toString();
    }

    public static void main(String[] args) throws Exception{

        System.out.println(System.nanoTime());

        StringSplitFeature feature = new StringSplitFeature();

        int repeat = 1000 * 1000;
        Map<String, String> map = new HashMap<>();
        for(int i=0; i< repeat; i++){
            String key1 = feature.getUniqLongString(64);
            String val1 = feature.getUniqLongString(64);
            String key2 = feature.getUniqLongString(1024);
            String val2 = feature.getUniqLongString(64);
            String s = new StringBuilder()
                    .append(key1).append( "=").append(val1).append(",")//.toString();
                    .append(key2).append("=").append(val2).toString();

            String[] keyValList = s.split(",");
            String shortKeyVal = keyValList[0]; //64str = 64str
            //String shortKeyVal = keyValList[1]; //64str = 64str
            String[] shortKeyValArray = shortKeyVal.split("=");
            map.put(shortKeyValArray[0], shortKeyValArray[1]);
            //map.put(new String(shortKeyValArray[0]), new String(shortKeyValArray[1]));
            //map.put(s.substring(0,64), s.substring(65,129));

        }

        System.out.println("wait for checking");
        Thread.sleep(1000 * 60 * 5);

    }



}
