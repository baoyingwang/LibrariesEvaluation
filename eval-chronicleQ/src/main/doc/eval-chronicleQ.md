[TOC]

# Overview
ChronicleQ/ChronicleMap是performance tuning后的数据结构
- off-heap
- ultra-low latency

- 代码中的关键概念，看代码的时候会看到下面几个概念
  - Excerpt ：数据容器
  - Appender：用来写入数据
  - Trailer：用来读取数据

# 限制
- 单机使用
  - note：跨machine的同步需要enterprise版才支持

# 官网
- ChronicleQ https://github.com/OpenHFT/Chronicle-Queue
- ChronicleMap https://github.com/OpenHFT/Chronicle-Map
- 简要介绍
  - https://www.baeldung.com/java-chronicle-queue

# Restartable Tailers - 给tailer起一个名字达到这个目标
文档中非常清楚，摘抄代码如下:
你可以看到，第二段try中，atailer、brailer分别从各自的之前度过的位置开始
```java
try (ChronicleQueue cq = SingleChronicleQueueBuilder.binary(tmp).build()) {
    ExcerptTailer atailer = cq.createTailer("a");
    assertEquals("test 0", atailer.readText());
    assertEquals("test 1", atailer.readText());
    assertEquals("test 2", atailer.readText()); // (1)

    ExcerptTailer btailer = cq.createTailer("b");
    assertEquals("test 0", btailer.readText()); // (3)
}

try (ChronicleQueue cq = SingleChronicleQueueBuilder.binary(tmp).build()) {
    ExcerptTailer atailer = cq.createTailer("a");
    assertEquals("test 3", atailer.readText()); // (2)
    assertEquals("test 4", atailer.readText());
    assertEquals("test 5", atailer.readText());

    ExcerptTailer btailer = cq.createTailer("b");
    assertEquals("test 1", btailer.readText()); // (4)
}
```

# 可能丢失问题-通过向前移动一个index位置来避免这个问题（见下面代码事例)
因为这个没有人工ack问题，系统重启时候，可能出现当前数据已经读取，但是没有来得及处理情况
这样，重新启动时候，这个数据可能就不会再读取出来了

     * 譬如：
     * - 发送和消费了a,b,c
     * - consumer down
     * - produce 发送d,e,f,g
     * - consumer启动之后，默认从d开始消费。
     *   - 为了避免上次的c没有处理完consumer可以将c再次消费一次
     *   - 方法就是获取consumer当前index(指向d)，然后向前移动一个位置(指向c)，然后再开始消费就从c开始了
     
下面代码测试过了。
```java
//注意：有值以后，其index不是从0开始，而是从一个很大很大的数字开始
if(tailer.index() == 0){
    System.out.println("start of the queue, don't move back");
}else{
    tailer.moveToIndex(tailer.index()-1);
}

```
# 建议消息格式-以bytes方式保存json或者自己规定的bytes格式，不要使用其内置的各种花哨的东西
- 建议1：用json bytes开发效率高一些，但是可能marshal/unmarshal效率可能是问题
- 建议2：使用自己定义的bytes格式，但是还是要用self-describe方式做一个简要的封装，
  - producer.produceSampleBytesInDescribed(WriteConfig.BYTES_IN_SELF_DESCRIBE.file);
  - consumer.nonblockingConsumeBytesInSelfDescribeSample(WriteConfig.BYTES_IN_SELF_DESCRIBE.file);
  
# 文件rolling每天UTC mid-night，可以改
- When using daily-rolling, Chronicle Queue will roll at midnight UTC.

# 线程
## FAQ中明确说了可以多个reader，但是要创建不同的queue（指向相同的目录）
## FAQ中明确说了可以多个writer，但是单个以保证更高消息。我怀疑多个writer（不同jvm）情况，因为相互之间极可能冲突。
以后做一些测试