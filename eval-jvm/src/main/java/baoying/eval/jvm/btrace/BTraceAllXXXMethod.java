package baoying.eval.jvm.btrace;


import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.BTrace;
import org.openjdk.btrace.core.annotations.OnMethod;
import org.openjdk.btrace.core.annotations.ProbeClassName;
import org.openjdk.btrace.core.annotations.ProbeMethodName;
import org.openjdk.btrace.core.types.AnyType;

/**
 *
 * @BTrace annotation tells that this is a BTrace program
 *
 */
@BTrace(trusted=true)
public class BTraceAllXXXMethod {
    /**
     * 这个例子是打印所有run方法的调用，不管哪个类
     */
    //@OnMethod(clazz = "/.*/", method = "run")

    @OnMethod(clazz = "/.*/", method = "run")
    public static void funcLog(@ProbeClassName String className, @ProbeMethodName String methodName, AnyType[] args)
            throws InterruptedException {

        // BTraceUtils.print("Thread:"+Thread.currentThread().getName());
        // you can only call the static methods of BTraceUtils
        BTraceUtils.print("class:" + className + ",method:" + methodName + ", args:");
        BTraceUtils.printArray(args);
        BTraceUtils.println(".");
    }

}