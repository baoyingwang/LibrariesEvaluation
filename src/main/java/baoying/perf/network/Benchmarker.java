package baoying.perf.network;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class Benchmarker {

    final String _name;
    Benchmarker(String name){
        _name = name;
    }
    public AtomicLong counter = new AtomicLong(0);
    AtomicLong sum = new AtomicLong(0);
    Instant start = Instant.now();
    Instant end = Instant.now();

    public static Benchmarker create(String name) {
        return new Benchmarker(name);
    }

    public void measure(long l) {

        long c = counter.incrementAndGet();
        if(c == 1){
            start = Instant.now();
        }
        sum.addAndGet(l);
    }

    public char[] results() {

        end = Instant.now();
        long total = counter.get();


        double durationInSecond = (end.toEpochMilli() - start.toEpochMilli())/1000.0;
        double avgNS = sum.get()*1.0/total;
        String r =  "";
        r +="avg latency(us):" + String.format("%.8f",sum.get() * 1.0/(total*1000)) +" ";
        r +="speed(baoying.perf second): " + String.format("%.2f",total/durationInSecond)
                +" took:"+ durationInSecond+" seconds"
                +" total:" + total +" ";

        //r += "\n start " + start.toString() +" end:" + end.toString();
        counter.set(0);
        sum.set(0);

        return r.toCharArray();
    }
}
