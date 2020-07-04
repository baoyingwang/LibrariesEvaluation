package baoying.eval.chroniclemap;

import net.openhft.chronicle.map.ChronicleMap;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ChronicleMapSample {

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
        ChronicleMap<CharSequence, PostalCodeRange> cityPostalCodes = ChronicleMap
                .of(CharSequence.class, PostalCodeRange.class)
                .name("city-postal-codes-map")
                .averageKey("Amsterdam")
                .entries(50_000)
                .create();

        cityPostalCodes.put("1_2", new PostalCodeRangeA(1, 2));
        PostalCodeRange got = cityPostalCodes.get("1_2");

        assertEquals("",1, got.minCode());
        assertEquals("",2, got.maxCode());
    }

    @Test
    public void persistedSample() throws Exception{

        String cityPostalCodesFile = "/tmp/chronicleMap-persistedSample";
        ChronicleMap<CharSequence, PostalCodeRange> cityPostalCodes = ChronicleMap
                .of(CharSequence.class, PostalCodeRange.class)
                .name("city-postal-codes-map")
                .averageKey("Amsterdam")
                .entries(50_000)
                .createPersistedTo(new File(cityPostalCodesFile));

        cityPostalCodes.put("1_2", new PostalCodeRangeA(1, 2));

        PostalCodeRange got = cityPostalCodes.get("1_2");
        assertEquals("",1, got.minCode());
        assertEquals("",2, got.maxCode());

    }

    @Test
    public void persistedSampleSeparately() throws Exception{

        //故意分成两个方法，证明put之后数据已经保存与文件之中
        //重新构建这个map（from 文件），数据自然保存在其中了
        putData();
        checkData();
    }
    private void putData() throws Exception{
        String cityPostalCodesFile = "/tmp/chronicleMap-persistedSample";
        ChronicleMap<CharSequence, PostalCodeRange> cityPostalCodes = ChronicleMap
                .of(CharSequence.class, PostalCodeRange.class)
                .name("city-postal-codes-map")
                .averageKey("Amsterdam")
                .entries(50_000)
                .createPersistedTo(new File(cityPostalCodesFile));

        cityPostalCodes.put("1_2", new PostalCodeRangeA(1, 2));
    }

    @Test
    public void checkData() throws Exception{

        //之前人肉执行过putData了，所以再次（新的进程）执行这个case也能通过
        //这更加证明了不同进程间通信的功能

        String cityPostalCodesFile = "/tmp/chronicleMap-persistedSample";
        ChronicleMap<CharSequence, PostalCodeRange> cityPostalCodes = ChronicleMap
                .of(CharSequence.class, PostalCodeRange.class)
                .name("city-postal-codes-map")
                .averageKey("Amsterdam")
                .entries(50_000)
                .createPersistedTo(new File(cityPostalCodesFile));
        PostalCodeRange got = cityPostalCodes.get("1_2");
        assertEquals("",1, got.minCode());
        assertEquals("",2, got.maxCode());
    }
}
