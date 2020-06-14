[TOC]

# Overview

一些性能要求高一些的系统，都要求主动关闭Naggle（by set TCP_NO_DELAY = TRUE)
因为Nagle算法生效的时候，发送的ack数据可能要等一会儿
在NagleClient.java中可以看到我们设定了socket.setTcpNoDelay(!isNagleEnabled);


# 参考
- Windows系统发送ack的时间等待： 发送ack：when默认200毫秒内没有收到新数据，或者，也可以配置 https://support.microsoft.com/en-us/help/328890/new-registry-entry-for-controlling-the-tcp-acknowledgment-ack-behavior 
- 这个也应该读一下，关于Nagle和WindowsOS发送时候行为
  - NAGLE可以通过设置TCP_NODELAY给一个socket，这样这个socket就disable了Nagle了
- socket中的nagle算法 https://blog.csdn.net/ithzhang/article/details/8520026  