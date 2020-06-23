[TOC]


# Introduction

20200307 从github move到这里，因为我还没有特别想清楚和和整理github与wiznote上的东西。可能把note整理到github sourcecode（不是github wiki，限制太多/功能太少），然后把doc放到github.io上面（还不知道怎么用）。快速看了一下github pages，其对多级目录导航的支持太脆弱了，我有点犹豫。还是暂时使用wiznote

下面是我之前move过来的KB，但是非常难读懂，我会把它重新整理为容易理解的文档。

what's btrace? It is something like DTrace, but only for java. 

home page：https://github.com/btraceio/btrace/wiki

```

Safe, dynamic tracing tool for Java. BTrace works by dynamically 
(bytecode) instrumenting classes of a running Java program. BTrace 
inserts tracing actions into the classes of a running Java program and 
hotswaps the traced program classes.

```


Relate this link could give you a quick story on how to prepare it and how to run it.
http://www.cnblogs.com/serendipity/archive/2012/05/14/2499840.html (CN)


I hope this page would be as a reference guide about btrace. It cover below currently
1. a real scenario recently with code
2. some tips for the implementation
3. some appendix for during using it - see catalog

# Maven依赖
```
  <repositories>
    <repository>
      <id>https://dl.bintray.com/btraceio/maven/</id>
      <url>https://dl.bintray.com/btraceio/maven/</url>
    </repository>
  </repositories>

  <dependencies>
    <!-- https://mvnrepository.com/artifact/org.openjdk.btrace/btrace-client -->
    <dependency>
      <groupId>org.openjdk.btrace</groupId>
      <artifactId>btrace-boot</artifactId>
      <version>2.0.2</version>
    </dependency>
    <dependency>
      <groupId>org.openjdk.btrace</groupId>
      <artifactId>btrace-agent</artifactId>
      <version>2.0.2</version>
    </dependency>
    <dependency>
      <groupId>org.openjdk.btrace</groupId>
      <artifactId>btrace-client</artifactId>
      <version>2.0.2</version>
    </dependency>
  </dependencies>
```


# 快速开始

How to attach btrace script file(java file, or compiled class) to a running jvm process

1. prepare btrace java(seeb below java example)
  * 类定义加上@BTrace(trusted=true) 使得其运行在trustmode，这样可以获取更多信息；运行脚本时候 （command line），增加 -u option，这样trust mode才能生效
  * Class file is recommended because it will tell you more detail on failure.
2. copy the scripts(java, or compiled class) to the jvm box
3. run btrace script
* set JAVA_HOME # 需要JDK
* bin/btrace -u -v pid script
  * note: the script is the java file, or class file uploaded. 
  * note: the script could be relative path, or absolute path. It is NOT required to follow the strict classpaht, e.g. you can use /export/home/baoying/btrace_scripts/bybtrace/LastLineLoad.class
  * 加上 -u 才能运行trust mode
  * 加上-v 方便trouble shooting

# A Trouble Shoooting Example

Oct 27, 2017, my colleague updated me that a number in the statistics file is NOT updated after app bounce. The number is expected to continue increasing, e.g. 3 before bounce, bounce, increase to 4 after processing a new message.



How to trouble shooting this problem?

Firstly : whether any error information in logs. - I skipped this step, because I am lazy. But you'd better check the logs firstly. 

Then :
       - prepare the env at local laptop, and try to reproduce, then debug
 OR 
         - have a quick look with btrace on related functions during the issue. I tried this directly, since I think no change on it recently. I hope a quick check on it.



Here is detail how I did
1. have a look at related source code. It tells that, during start, it will load the previous number(3 in the example) from the last line of statistics file. This is the key line for this feature.
2. write a btrace script to print the loaded last line. See the script code example below.
3. attach the btrace script to application and check console outpu. 



During #3, i should see that the there is NO last line at all, since the statistics file was deleted unexpectedly. So, we should check why the file is deleted. Before I try it, my coleague updated me that he found the missing file issue. So, it is a false alarm on the number change.



# Script Example(more examples at btrace-bin-1.3.9\samples)

note: you cannot create new object by default. See trusted mode in below section if you expect to do that.

```

package bybtrace;

import static com.sun.btrace.BTraceUtils.println;

import static com.sun.btrace.BTraceUtils.print;

import com.sun.btrace.AnyType;

import com.sun.btrace.BTraceUtils;

import com.sun.btrace.annotations.BTrace;

import com.sun.btrace.annotations.Kind;

import com.sun.btrace.annotations.Location;

import com.sun.btrace.annotations.OnMethod;

import com.sun.btrace.annotations.ProbeClassName;

import com.sun.btrace.annotations.ProbeMethodName;

import com.sun.btrace.annotations.Return;

import com.sun.btrace.annotations.Self;

//in this example, it print all methods invokation of class StatsWriter.

//BTrace annotation tells that this is a BTrace program

@BTrace

public class FIXGWLastLineLoad {

    @OnMethod(clazz = "com.txxxy.StatsWriter", method = "/.*/")

    public static void funcLog(@ProbeClassName String cn, @ProbeMethodName String mn, AnyType[] args)

        throws InterruptedException {

        // println is defined in BTraceUtils

        // you can only call the static methods of BTraceUtils

        print("baoying - class"+ cn +"enter "+ mn+", args:");

        BTraceUtils.printArray(args);

        println(".");

    }

}

```

 

See below section to attach this script to the target running jvm process.







# Tips



## btrace can do far more than print the method entry/return value, e.g. check the duration of a method, etc. see https://www.jianshu.com/p/dbb3a8b5c92f . (Chinese/中文）

and btrace homepage https://github.com/btraceio/btrace/wiki





## Tip - easy to write the scritp with eclipse or intellij IDEA, after importing related jar

I would suggest to add btrace to your test lib/dependency in your project, and prepare a BTraceSample.java at hand. Then easy use when required. E.g. update the BTraceSample.java to print the related lines, copy the BTraceSample.class to server, attach to the running jvm process. That is very easy (since you can write more powerful script which depends on your source libraryies.



https://mvnrepository.com/artifact/com.sun.tools.btrace/btrace-client/1.2



## Tip - for beginer or debuging phase, it is good idea to trace with verbose

btrace -v pid script

you will find verbose output at both btrace side, and the target jvm side



## Tip - @Return cannot carry native type(e.g. long, int). Below script has problem since getThreadCount return long.



```

    @OnMethod(clazz = "packagename.StatsWriter", method = "getThreadCount", location = @Location(Kind.RETURN))

    public static void funcOnThreadCount(@Return long callbackData) throws InterruptedException {

        //print return value - https://gist.github.com/littson/4192768

        print("baoying - enter getThreadCount:");

        BTraceUtils.print(callbackData);

        println(".");

    }

```

     

Otherwise, you will see error like below at the target jvm(i not sure whether whether it is required to set -v to see below)

```

[07:11:15.503][f][main ] Cannot start ttCONNECT Thrown: java.lang.VerifyError: Bad type on operand stack

Exception Details:

  Location:

    com/something masked/StatsWriter.getThreadCount()J @9: invokestatic

  Reason:

    Type long_2nd (current frame, stack[3]) is not assignable to 'java/lang/Object'

  Current Frame:

    bci: @9

    flags: { }

    locals: { 'com/something masked/StatsWriter', long, long_2nd }

    stack: { long, long_2nd, long, long_2nd }

  Bytecode:

    0000000: b800 1bb6 0022 5c40 1fb8 012f ad

```



# Tip - Access is denied on windows

start the command windows with administrator



# Appendix - btrace parameters



```

~/baoying/btrace-bin-1.3.9/bin>bash btrace

Usage: btrace <options> <pid> <btrace source or .class file> <btrace arguments>

where possible options include:

  --version Show the version

  -v Run in verbose mode

  -o <file> The path to store the probe output (will disable showing the output in console)

-u Run in trusted mode

  -d <path> Dump the instrumented classes to the specified path

  -pd <path> The search path for the probe XML descriptors

  -classpath <path> Specify where to find user class files and annotation processors

  -cp <path> Specify where to find user class files and annotation processors

  -I <path> Specify where to find include files

  -p <port> Specify port to which the btrace agent listens for clients

  -statsd <host[:port]> Specify the statsd server, if any

```

# Appendix - how to use with trusted(legacy named:unsafe) mode



Many limitations in the btrace script by default(unsafe/untrusted mode), e.g. you cannot new any object. Link to be added about the limitation


Steps
* your script class should be marked as @BTrace(trusted=true)
e.g.
```
@BTrace(trusted=true)
public class MemoryEater {  
```

* add "-u" option while run btrace
-u Run in trusted mode


note: trusted=true/fase is introduced since ???　to replace previous unsafe. From web, you can find many pages to mentino the depreated: -Dcom.sun.btrace.unsafe=true and is @BTrace(unsafe=true).


# Trouble Shooting
## 保证btrace运行的java-home和被监控的java进程是相同的jdk版本
否则可能会出现一些奇怪的问题
- e.g. Non-numeric value found - int expected
- e.g. 


# Reference
- 基本用法看这里
  - 主页的wiki https://github.com/btraceio/btrace/wiki
  - 还有btrace 脚本提示各个参数