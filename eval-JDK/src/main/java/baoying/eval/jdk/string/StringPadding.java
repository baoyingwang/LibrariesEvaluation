package baoying.eval.jdk.string;

import junit.framework.Assert;
import org.junit.Test;


public class StringPadding {

    @Test
    public void padding(){

        //https://stackoverflow.com/questions/473282/how-can-i-pad-an-integer-with-zeros-on-the-left
        //https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html
        //https://www.baeldung.com/java-pad-string   这里还介绍了apache commons和guava 的相关方法

        //0 表示补0， 5表示最短5位
        String leftPadding = "%05d";
        Assert.assertEquals("00001",String.format(leftPadding, 1));
        Assert.assertEquals("1234567",String.format(leftPadding, 1234567));

        //https://www.javacodeexamples.com/java-string-pad-zero-example/855
        Assert.assertEquals("0000a",String.format("%5s", "a").replace(' ', '0'));
        Assert.assertEquals("a0000",String.format("%-5s", "a").replace(' ', '0'));



    }
}
