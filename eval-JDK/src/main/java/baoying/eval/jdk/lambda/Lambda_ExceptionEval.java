package baoying.eval.jdk.lambda;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于lambda中的exception有几个要点要注意
 * 1. 其还是在当前线程中执行的（除非是调用parallel的那些feature）
 * 2. 所以exception抛出，与lambda的签名有关。for-each是Consumer接口类（无exception抛出），所以我们不能抛出任何checked exception
 * 3. RuntimeException抛出来后，当前线程就结束了
 *
 * 在forEach中无法抛出checked Exception, 因为forEach里面的 it ->{}实际上实现了Consumer接口，
 * 而Consumer接口的accept方法没有抛出任何异常。
 * 所以作为接口的实现类（it->{})不能抛出check exception，
 * 否则将破坏原有的继承使用原则（子类不能抛出父类未声明checked异常，
 * 否则无法保证子类object能够在父类的接口中使用）
 *
 * WARN： forEach中的runtime异常将会导致循环直接跳出来，
 * caller将得到这个异常。因为实际上内部还是使用的for(Object o: objects)结构。
 * 这里单独提一下，是因为stream.map( it ->{ }) 也有类似的情况，行为也是类似的。
 *
 * 这里有一个map的例子
 * https://stackoverflow.com/questions/44710902/need-to-continue-filtering-java-stream-when-map-throws-exception-after-filter-an
 */
public class Lambda_ExceptionEval {

    public static void main(String[] args) throws Exception{

        List<String> names = new ArrayList<>();
        names.add("John");
        names.forEach( it ->{
            //throw new Exception("");  <- 语法错误，因为Consumer接口限制
            //throw new RuntimeException("only for test in x.forEach"); ok here
        });

        for(String it: names){
            //<- 这里checked exception没有问题！！！，因为没有Consumer接口的限制
            //throw new Exception(it);
        }

        names.stream().forEach(it ->{
            System.out.println(it);
            //throw new Exception(""); <- 语法错误,因为consumer接口的限制
        });

        names.stream().forEach(it ->{
            System.out.println(it);
            throw new RuntimeException("only for test in x.stream().forEach");
        });
    }
}
