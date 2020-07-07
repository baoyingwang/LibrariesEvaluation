package baoying.eval.jdk.lambda;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
public class Lambda_Consumer {

    /**
     *
     * Consumer就是一个有参数,而且没有返回值的接口
     * - BiConsumer,就是两个参数，仍然没有返回值
     * - 其特点就是没有返回值
     *
     */
    @Test
    public void evalConsumerAsCommonInterface(){

        //就是一个普通的接口使用
        // - 使用Consumer的好处在于省得重新定义特多自己的Interface，直接拿来已经定义好的用就行了
        // - 越来越多的人使用lambda，看到Consumer接口也都比较淡然了
        class A{
            Consumer<String> callback;
            void setCallback(Consumer<String> callback){
                this.callback = callback;
            }
            void doSomething(String val){
                callback.accept(val);
            }
        }

        A a = new A();
        Consumer<String> c1 = o -> {
            System.out.println(o);
        };
        a.setCallback(c1);
    }

    /**
     * - forEach中就是Consumer
     * - Map的forEach就是BiConsumer
     */
    @Test
    public void evalConsumerInForEach(){

        String[] valArray = new String[]{"a", "b"};
        List<String> vals = Arrays.asList(valArray);
        //这里可以看到，forEach的参数是一个Consumer
        vals.forEach(o->System.out.println(o));

        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        //可以看到，这里的forEach参数为BiConsumer
        map.forEach((k, v)-> System.out.println("key:"+k+", val:"+v));

    }
}
