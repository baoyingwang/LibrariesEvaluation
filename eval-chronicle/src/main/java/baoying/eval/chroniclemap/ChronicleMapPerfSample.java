package baoying.eval.chroniclemap;

import net.openhft.chronicle.map.ChronicleMap;
import org.junit.Test;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

public class ChronicleMapPerfSample {

    interface PostalCodeRange {
        int minCode();
        void minCode(int minCode);

        int maxCode();
        void maxCode(int maxCode);
    }

    class PostalCodeRangeA implements PostalCodeRange {

        private int min;
        private int max;
        PostalCodeRangeA(int min, int max){
            this.min = min;
            this.max = max;
        }
        @Override
        public int minCode() {
            return this.min;
        }

        @Override
        public void minCode(int minCode) {
            this.min = minCode;
        }

        @Override
        public int maxCode() {
            return this.max;
        }

        @Override
        public void maxCode(int maxCode) {
            this.max = maxCode;
        }
    }

    @Test
    public void memorySample(){
        int totalSize = 10_000_000;
        ChronicleMap<CharSequence, PostalCodeRange> cityPostalCodes = ChronicleMap
                .of(CharSequence.class, PostalCodeRange.class)
                .name("city-postal-codes-map")
                .averageKey("Amsterdam")
                .entries(totalSize)
                .create();
        memoryTest("ChronicleMap-memory", cityPostalCodes, totalSize);
    }

    @Test
    public void memorySampleHashMap(){

        int totalSize = 10_000_000;
        Map<CharSequence, PostalCodeRange> cityPostalCodes = new ConcurrentHashMap<>(totalSize);

        memoryTest("JDKConcurrentHashmap", cityPostalCodes, totalSize);
    }

    @Test
    public void filemodeSample() throws Exception{
        int totalSize = 10_000_000;
        String cityPostalCodesFile = "/tmp/chronicleMap-persistedSample";
        ChronicleMap<CharSequence, PostalCodeRange> cityPostalCodes = ChronicleMap
                .of(CharSequence.class, PostalCodeRange.class)
                .name("city-postal-codes-map")
                .averageKey("Amsterdam")
                .entries(totalSize)
                .createPersistedTo(new File(cityPostalCodesFile));
        memoryTest("ChronicleMap-file", cityPostalCodes, totalSize);
    }

    public void memoryTest(String name, Map<CharSequence, PostalCodeRange> concurrentHashMap, int totalSize){

        long start = System.currentTimeMillis();
        for(int i=0; i<totalSize; i++ ){
            concurrentHashMap.put(""+i, new PostalCodeRangeA(i, i+1));
        }
        long putTook = System.currentTimeMillis()-start;

        start = System.currentTimeMillis();
        for(int i=0; i<totalSize; i++ ){
            PostalCodeRange got = concurrentHashMap.get(""+i);;
            assertEquals("check min",i, got.minCode());
            assertEquals("check max",i+1, got.maxCode());
        }
        long getTook = System.currentTimeMillis()-start;


        for (MemoryPoolMXBean mpBean: ManagementFactory.getMemoryPoolMXBeans()) {
            if (mpBean.getType() == MemoryType.HEAP) {
                System.out.printf(
                        "Name: %s: %s\n",
                        mpBean.getName(), mpBean.getUsage()
                );
            }
        }

        System.out.println(name+", put took:"+putTook+" ms, get took:"+ getTook +", size:"+totalSize);
    }

}
