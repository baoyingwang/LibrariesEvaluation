package baoying.eval.jdk.annotation.basic;

//import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * Baoying: 由于没有引入依赖（org.reflections.Reflections），相关代码注释掉了printAllAnnotation()
 * https://www.jianshu.com/p/7a02ddfb0e13
 *
 * Baoying: 给代码增加了一点儿注释
 *
 * @Author: feiweiwei
 * @Description:
 * @Created Date: 16:14 17/9/22.
 * @Modify by:
 */
public class PrintAnnotationByOrGReflections {

    private String allAnnotation = new String();

    public String printAllAnnotation(){
        //获取指定package包下的使用了MyType注解的类
//        Set<Class<?>> clazzes = new Reflections("baoying.eval.jdk.annotation").getTypesAnnotatedWith(MyType.class);
//
//        for (Class<?> clazz : clazzes) {
//            printMyType(clazz);
//        }
//        System.out.println(allAnnotation);
        return allAnnotation;
    }

    private void printMyType(Class<?> clazz) {

        //Baoying：获取当前类*clazz）的annotations中，类型为MyType.class的annotation，如果没有将返回null
        //如果进去查看clazz源代码的话，会发现其就是查询内部的一个map
        MyType myType = clazz.getAnnotation(MyType.class);
        allAnnotation = allAnnotation + clazz.getName() + ": " + myType.value() + "-" + myType.className() + "\n";


        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            MyField myField = field.getAnnotation(MyField.class);
            if(myField != null) {
                allAnnotation = allAnnotation + myField.value() + "-" + myField.name() + "-" + myField.type() + "\n";
            }
        }

        Method[] methods = clazz.getMethods();
        for(Method method : methods){

            MyMethod myMethod = method.getAnnotation(MyMethod.class);
            if(myMethod != null) {
                allAnnotation = allAnnotation + myMethod.methodName() + "-" + myMethod.value() + "\n";
            }
        }

    }

}
