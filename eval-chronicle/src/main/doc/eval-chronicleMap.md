[TOC]
# Overview
https://github.com/OpenHFT/Chronicle-Map
- off-heap map
- 用起来和concurrentmap很像
- 也可以跨jvm使用 via 文件模式
  - 一个jvm写，另一个（或者几个）负责读
  - 如果是单个jvm使用则无需文件
- 不能跨机器

# Practice
## 要提前给定足够大的entries，否则超过这个数量则无法保存.而且文件一旦创建，这个数量无法改变
## 放入的值可以用接口-见本project例子
## 如果重启machine后不需要再保留map数据（重启进程则需要），把文件位置指向tmpfs
tmpfs:https://en.wikipedia.org/wiki/Tmpfs
"If you don’t need the Chronicle Map instance to survive the server restart
 (that is, you don’t need persistence to disk; only multi-process access), 
mount the file on tmpfs. 
For example, on Linux it is as easy as placing you file in /dev/shm directory."
Refer: https://github.com/OpenHFT/Chronicle-Map/blob/master/docs/CM_Tutorial.adoc#chroniclemap-instance-vs-chronicle-map-data-store

## 提供平均key/value的大小，可以获得更好性能
- 这样，其可以预先分配好相关资源
  - e.g averageKey("Amsterdam")
  - e.g.averageValue(new long[150])， constantKeySizeBySample(UUID.randomUUID())
- 参考 https://github.com/OpenHFT/Chronicle-Map/blob/master/docs/CM_Tutorial.adoc#key-and-value-types

## 文件维护/一般的应用在维护时候（每晚或者每周末）都应该停机把数据移走
## name字段用于标识，没发现什么实际用处
- 快速检查了代码，出现错误时候name在出错信息中方便识别

# Performance - 

## 测试报告1 
- 测试环境：Mac Air（2020 cpu:1.1GHz/4cores/IntelCore i5/8G, memory: 8GB,3733MHz,LPDDR4, disk:SSD,APFS
- 测试场景
  - 向map中存入10,000,000记录，key：String "0".."9,999,999", value: PostalCodeRange.minCode/maxCode
  - 从map中逐条取出（然后啥也不干）
- 测试代码：ChronicleMapPerfSample，手工执行  
- 测试结果

|case|memory|put时间/get时间:ALL|put时间get时间:平均每条|EdenUsed|OldGenUsed|SurvivorUsed|Note|
|----|----|----|-----|----|----|----|----|
|JDKConcurrentHashmap|heap|3858ms/1950ms| 0.3858us/0.195us |24576K|1112880K|2k|--|
|chronicleMap-memory|off-heap|7167ms/3522ms|0.7167us/0.3522us|56320K|8661K|2K|--|
|chronicleMap-file|off-heap|11184ms/3569ms|1.1184us/0.3569us|36964K|8628K|2K|--|

- 测试结果解析
  - chronicleMap的off-heap特性非常明显
  - chronicleMap比JDK ConcurrentHashmap要慢一倍到两倍
    - 文件方式比内存方式还要慢一点
    - 所以，只有比较大量的数据使用ChronicleMap才有意义。对于数量很小，或者存取频繁的数据，要慎重使用
  - chronicleMap的performance与其官方描述是一致的，基本小于1us（纳秒）的存取 


测试中的log输出
```java
Name: G1 Eden Space: init = 26214400(25600K) used = 25165824(24576K) committed = 91226112(89088K) max = -1(-1K)
Name: G1 Old Gen: init = 108003328(105472K) used = 1139589120(1112880K) committed = 1653604352(1614848K) max = 2147483648(2097152K)
Name: G1 Survivor Space: init = 0(0K) used = 2064(2K) committed = 1048576(1024K) max = -1(-1K)
JDKConcurrentHashmap, put took:3855 ms, get took:1950, size:10000000



JDKConcurrentHashmap（on-heap）
Name: G1 Eden Space: init = 26214400(25600K) used = 8388608(8192K) committed = 88080384(86016K) max = -1(-1K)
Name: G1 Old Gen: init = 108003328(105472K) used = 1116520448(1090352K) committed = 1676673024(1637376K) max = 2147483648(2097152K)
Name: G1 Survivor Space: init = 0(0K) used = 12582912(12288K) committed = 12582912(12288K) max = -1(-1K)
JDKConcurrentHashmap, took:5578 ms - size:10000000

ChronicleMap - 基于内存（off-heap）
Name: G1 Eden Space: init = 26214400(25600K) used = 57671680(56320K) committed = 72351744(70656K) max = -1(-1K)
Name: G1 Old Gen: init = 108003328(105472K) used = 8869376(8661K) committed = 41943040(40960K) max = 2147483648(2097152K)
Name: G1 Survivor Space: init = 0(0K) used = 2064(2K) committed = 1048576(1024K) max = -1(-1K)
ChronicleMap-memory, put took:7167 ms, get took:3522, size:10000000


ChronicleMap - 基于文件（off-heap）
Name: G1 Eden Space: init = 26214400(25600K) used = 37748736(36864K) committed = 72351744(70656K) max = -1(-1K)
Name: G1 Old Gen: init = 108003328(105472K) used = 8835072(8628K) committed = 41943040(40960K) max = 2147483648(2097152K)
Name: G1 Survivor Space: init = 0(0K) used = 2064(2K) committed = 1048576(1024K) max = -1(-1K)
ChronicleMap-file, put took:11184 ms, get took:3569, size:10000000
```