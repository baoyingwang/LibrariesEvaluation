package baoying.security;

import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.print;
import com.sun.btrace.AnyType;
import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.ProbeClassName;
import com.sun.btrace.annotations.ProbeMethodName;

@BTrace
public class Monitor {

    @OnMethod(clazz = "sun.baoying.security.ssl.ServerHandshaker", method = "/.*/")
    public static void serverHandshaker(@ProbeClassName String cn, @ProbeMethodName String mn, AnyType[] args)
            throws InterruptedException {
        // println is defined in BTraceUtils
        // you can only call the static methods of BTraceUtils
        print("baoying - class:"+ cn +", method:"+ mn+", args:");
        BTraceUtils.printArray(args);
        println(".");
    }

    @OnMethod(clazz = "sun.baoying.security.ssl.ECDHCrypt", method = "/.*/")
    public static void ECDHCrypt(@ProbeClassName String cn, @ProbeMethodName String mn, AnyType[] args)
            throws InterruptedException {
        // println is defined in BTraceUtils
        // you can only call the static methods of BTraceUtils
        print("baoying - class:"+ cn +", method:"+ mn+", args:");
        BTraceUtils.printArray(args);
        println(".");
    }

    @OnMethod(clazz = "org.bouncycastle.jce.provider.JDKKeyPairGenerator", method = "/.*/")
    public static void jdkKeyPairGenerator(@ProbeClassName String cn, @ProbeMethodName String mn, AnyType[] args)
            throws InterruptedException {
        // println is defined in BTraceUtils
        // you can only call the static methods of BTraceUtils
        print("baoying - class:"+ cn +", method:"+ mn+", args:");
        BTraceUtils.printArray(args);
        println(".");
    }

}
