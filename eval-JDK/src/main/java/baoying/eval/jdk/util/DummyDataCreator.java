package baoying.eval.jdk.util;

public class DummyDataCreator
{

    public static String getDummyString(int size, String repeatSeed)
    {

        if (repeatSeed == null || repeatSeed.length() < 1 || size < 1)
        {
            throw new IllegalArgumentException("repeatSeed should not be null, and should contain 1 char at least, size should bigger than 0");
        }

        if (repeatSeed.length() >= size)
        {
            return repeatSeed.substring(0, size);
        }

        StringBuilder result = new StringBuilder();
        int last = size % repeatSeed.length();
        int repeatOccurance = size / repeatSeed.length();
        for (int i = 0; i < repeatOccurance; i++)
        {
            result.append(repeatSeed);
        }

        result.append(repeatSeed.substring(0, last));
        return result.toString();

    }
    
    /**
     * For specific profiling purpose to add a wrapper.
     * It is helpful to study the reference relationship in profiling tool. 
     */
    public static WrappedBytes getWrappedBytes(int size){
    	
    	WrappedBytes result = new WrappedBytes();
    	result.data = new byte[size]; 
    	return result;
    }
    public static class WrappedBytes{
    	byte[] data;    	
    }
    
    /**
     * For specific profiling purpose to add a wrapper.
     * It is helpful to study the reference relationship in profiling tool.
     *  
     */
    public static WrappedLongs getWrappedLongs(int size){
    	
    	WrappedLongs result = new WrappedLongs();
    	result.data = new long[size]; 
    	return result;
    }    
    public static class WrappedLongs{
    	long[] data;    	
    }    
    
    
}
