package baoying.eval.jdk.annotation.basic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 参考 http://www.java2s.com/Code/Java/Reflection/Showallannotationsforaclassandamethod.htm
 * 但是打印TestClass
 */
public class PrintAnnotationByJDK {

    public static void main(String[] args){
        TestClass ob = new TestClass();

        try {
            //打印当前对象（ob）对应class的所有类上面的annotation
            Annotation annos[] = ob.getClass().getAnnotations();
            System.out.println("All annotations for " + ob.getClass().getCanonicalName());
            for (Annotation a : annos)
                System.out.println(a);

            //打印指定方法（print()）的所有annotation
            Method m = ob.getClass().getMethod("print");
            annos = m.getAnnotations();
            System.out.println("All annotations for method: print():");
            for (Annotation a : annos)
                System.out.println(a);

        } catch (NoSuchMethodException exc) {
            System.out.println("Method Not Found.");
        }
    }
}
