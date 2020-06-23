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
     * 这个例子是：指定的class的指定method调用，则打印相关方法和参数
     */
    @OnMethod(clazz = "baoying.eval.jvm.btrace.Tmp", method = "someMethod")
    public static void yourClassAndMethodLog(@ProbeClassName String className, @ProbeMethodName String methodName, AnyType[] args)
            throws InterruptedException {

        // 一般而言，只能调用BTraceUtils的静态方法，而不能new任何变量
        // 但是因为我总是在trust mode下，所以没有这个限制

        BTraceUtils.print("Thread:"+Thread.currentThread().getName());
        BTraceUtils.print("class:" + className + ",method:" + methodName + ", args:");
        BTraceUtils.printArray(args);
        BTraceUtils.println(".");
    }


    /**
     * 类名和方法名都可以用正则表达式来匹配
     * 本例子中：任何类的方法名称为m1 or m2都将符合条件
     *
     */
    @OnMethod(clazz = "/.*/", method = "/m1|m2/")
    public static void regMethods(@ProbeClassName String className,
                                  @ProbeMethodName String methodName,
                                  AnyType[] args)
            throws InterruptedException {

        BTraceUtils.print("Thread:"+Thread.currentThread().getName());
        BTraceUtils.print("class:" + className + ",method:" + methodName + ", args:");
        BTraceUtils.printArray(args);
        BTraceUtils.println(".");
    }

    @OnMethod(clazz = "/baoying\\.eval\\.jvm\\.btrace.*/", method = "m3")
    public static void moreRegMethods(@ProbeClassName String className,
                                      @ProbeMethodName String methodName,
                                      AnyType[] args)
            throws InterruptedException {

        BTraceUtils.print("Thread:"+Thread.currentThread().getName());
        BTraceUtils.print("class:" + className + ",method:" + methodName + ", args:");
        BTraceUtils.printArray(args);
        BTraceUtils.println(".");
    }
}