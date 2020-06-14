[TOC]

# Overview

java.lan.ref中有SoftReference和WeekReference。两者都是用来指向给定的对象，这个被指定对象可能在某一时刻被回收。这个回收过程不受我们自己程序控制。
WeekReference被回收的可能性更高一些。
- WeekReference - 下一个回收周期就回收
- SoftReference - 内存不够了才回收，不够
  - 有个vm参数 -XX:SoftRefLRUPolicyMSPerMB 
    - 每兆堆空闲空间中SoftReference的存活时间/softly reachable objects will remain alive for some amount of time after the last time they were referenced. The default value is one second of lifetime per free megabyte in the heap
    - 假设还有5MB剩余内存，则那些reference存在了5秒(5MB * 1000ms/MB) 以上的则可能被清除
      - 这里链接清楚的解释了这个参数https://blog.shiftleft.io/understanding-jvm-soft-references-for-great-good-and-building-a-cache-244a4f7bb85d
      - 该链接还解释了很多别的reference相关的东西
    - 很多人调优的时候把这个值改为0，但是我还没明白原理（TODO-改成0有何帮助？）
  - 注意，GC会把所有soft refereces都清除了（除了那些时间SoftRefLRUPolicyMSPerMB计算后内的）
    - 极大可能影响cache效率

# 一般用法

## 普通语法
使用上譬如（下面是SoftReference， WeekReference类似）
```
StringBuilder builder = new StringBuilder();
SoftReference<StringBuilder> reference1 = new SoftReference<>(builder);

StringBuild theVale = reference1.get() //这里可能返回null
reference1.reset() //主动让这个refernence失效

```

## 常用WeakHashMap - key as WeekReference

- WeakHashMap的使用场景 https://blog.csdn.net/kaka0509/article/details/73459419
- Java中的WeakHashMap https://juejin.im/entry/5a085809f265da430c114c8b

# 参考：
- https://www.baeldung.com/java-soft-references
- https://stackoverflow.com/questions/299659/whats-the-difference-between-softreference-and-weakreference-in-java
- section "What determines when softly referenced objects are flushed?" in https://www.oracle.com/java/technologies/hotspotfaq.html#gc_softrefs
