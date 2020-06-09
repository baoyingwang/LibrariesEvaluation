package baoying.eval.jdk.annotation.example_mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * https://github.com/walidake/Annotation/blob/master/src/main/java/com/walidake/annotation/mybatis/MethodProxyFactory.java
 *
 * 把MethodProxy也弄到这个类里面来，简化
 *
 * 动态代理工厂
 * @author walidake
 *
 */
public class MethodProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        final MethodProxy methodProxy = new MethodProxy();
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                methodProxy);
    }

}

/**
 *
 * https://github.com/walidake/Annotation/blob/master/src/main/java/com/walidake/annotation/mybatis/MethodProxy.java
 *
 * 方法动态代理
 *
 * @author walidake
 *
 */
class MethodProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] parameters)
            throws Throwable {
        return DaoOperatorFactory.handle(method, parameters);
    }

}