[TOC]
# Overview
https://github.com/OpenHFT/Chronicle-Map
- off-heap map
- 用起来和cncurrentmap很像
- 可以在相同jvm使用，也可以跨jvm使用
- 不能跨机器

# Practice
- 放入的值可以用接口

# Performance - 在我的Mac Air（2020 i5 8G）上面做了简单的比较
- chronicleMap慢一些 15～20%左右
  - 已经排除了创建map时间，考虑到chronicleQ可能要初始化文件
- chronicleMap基本没有使用heap - 这个对比太明显了
  - old gen used 12M vs 1G

```java
10M-mem-chronicleMap
Name: G1 Eden Space: init = 26214400(25600K) used = 70254592(68608K) committed = 82837504(80896K) max = -1(-1K)
Name: G1 Old Gen: init = 108003328(105472K) used = 13284864(12973K) committed = 52428800(51200K) max = 2147483648(2097152K)
Name: G1 Survivor Space: init = 0(0K) used = 544000(531K) committed = 1048576(1024K) max = -1(-1K)
ChronicleMap-memory, took:6559 ms - size:10000000


10M entries-hashmap
Name: G1 Eden Space: init = 26214400(25600K) used = 8388608(8192K) committed = 88080384(86016K) max = -1(-1K)
Name: G1 Old Gen: init = 108003328(105472K) used = 1116520448(1090352K) committed = 1676673024(1637376K) max = 2147483648(2097152K)
Name: G1 Survivor Space: init = 0(0K) used = 12582912(12288K) committed = 12582912(12288K) max = -1(-1K)
JDKConcurrentHashmap, took:5578 ms - size:10000000
100M-hashmap -OOM

```