package baoying.eval.jdk.gramar;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class DeleteDuringIteration {

    /**
     * 这个是如何删除map中的key的
     * https://stackoverflow.com/questions/1884889/iterating-over-and-removing-from-a-map
     */
    @Test
    public void deleteEntryDuringIterateMap(){

        Map<String, String> map = new HashMap<String, String>() {
            {
                put("test1", "test123");
                put("test2", "test123");
                put("test3", "test123");
                put("test4", "test456");
            }
        };

        //Java 8 - 两种方法，便利entrySet()或者便利 keySet()都可以
        //1. map.entrySet().removeIf(e -> <boolean expression>);
        map.keySet().removeIf(e -> "test1".equals(e));
        Assert.assertNull(map.get("test1"));

        //全能版本
        for(Iterator<Map.Entry<String, String>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> entry = it.next();
            if(entry.getKey().equals("test2")) {
                it.remove();
            }
        }
        Assert.assertNull(map.get("test1"));
    }


    /**
     * 这个是如何删除list中元素的
     * https://stackoverflow.com/questions/1921104/loop-on-list-with-remove
     */
    @Test
    public void deleteEntryDuringIterateList(){

        String[] strArray = new String[]{"test1", "test2", "test3", "test4"};
        //Arrays.asList结果不能直接删除，所以增加一个new ArrayList的wrapper
        List<String> list = new ArrayList<>(Arrays.asList(strArray));

        //全能版本
        for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); ) {
            String fruit = iterator.next();
            if ("test1".equals(fruit)) {
                iterator.remove();
            }
        }
        Assert.assertFalse(list.contains("test1"));
    }
}
