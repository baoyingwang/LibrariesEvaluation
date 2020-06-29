- 需要搞清楚依赖到底用哪个，all, client, driver都有什么区别

- 我关注的功能在于
  - 满足可靠广播的代码例子
    - 需要仔细读一下java guide和其他文档，理解一下其基本情况
    - 然后设计几个核心的case
  - 如果看到其他有意思的也可以看一看
    - 其可能还有别的功能，暂时暂时不是我的关注点
  

当前（20200629）进度是，copy发送/接收的两个代码，但是运行的时候都说无法创建两个文件
可能需要
- 增加权限
- 或者指定参数更换一个别的目录
- TODO： 看看代码和文档，and/or google一下
/Users/baoyingwang/Library/Java/JavaVirtualMachines/openjdk-14.0.1/Contents/Home/bin/java -javaagent:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar=62052:/Applications/IntelliJ IDEA CE.app/Contents/bin -Dfile.encoding=UTF-8 -classpath /Users/baoyingwang/ws/code/github/baoyingwang/LibrariesEvaluation_2/eval-aeron/target/classes:/Users/baoyingwang/.m2/repository/org/agrona/agrona/1.5.1/agrona-1.5.1.jar:/Users/baoyingwang/.m2/repository/io/aeron/aeron-client/1.28.2/aeron-client-1.28.2.jar baoying.eval.aeron.SimplePublisher
Publishing to aeron:udp?endpoint=localhost:40123 on stream id 10
Exception in thread "main" io.aeron.exceptions.DriverTimeoutException: CnC file not created: /var/folders/fc/ybq8pkvd3c3_zckpx2lnfnx40000gn/T/aeron-baoyingwang/cnc.dat
	at io.aeron.Aeron$Context.connectToDriver(Aeron.java:1328)
	at io.aeron.Aeron$Context.conclude(Aeron.java:680)
	at io.aeron.Aeron.<init>(Aeron.java:82)
	at io.aeron.Aeron.connect(Aeron.java:138)
	at baoying.eval.aeron.SimplePublisher.main(SimplePublisher.java:41)

Process finished with exit code 1