package perf.network;

public class SystemUtils {
    public static int getInt(String t, int i) {
        if(t.equals("msgSize")){
            return 1024;
            //return i;
        }else if(t.equals("messages")){
            return i;
        }else{
            throw new RuntimeException("unknown type:"+t);

        }
    }

    public static String delimiter() {
        return "\nXX\n";
    }
}
