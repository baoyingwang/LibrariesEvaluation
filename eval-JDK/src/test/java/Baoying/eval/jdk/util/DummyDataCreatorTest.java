package baoying.eval.jdk.util;

import junit.framework.Assert;

import org.junit.Test;

public class DummyDataCreatorTest
{
    @Test
    public void testGetDummyString1(){

        int size = 10;
        String repeatSeed = "ABC";
        String expected ="ABCABCABCA";
        String actual = DummyDataCreator.getDummyString(size, repeatSeed);
        Assert.assertEquals(expected, actual);


    }


    @Test
    public void testGetDummyString2(){

        int size = 4;
        String repeatSeed = "ABCDE";
        String expected ="ABCD";
        String actual = DummyDataCreator.getDummyString(size, repeatSeed);
        Assert.assertEquals(expected, actual);


    }

    @Test
    public void testGetDummyString3(){

        int size = 4;
        String repeatSeed = "A";
        String expected ="AAAA";
        String actual = DummyDataCreator.getDummyString(size, repeatSeed);
        Assert.assertEquals(expected, actual);


    }
}
