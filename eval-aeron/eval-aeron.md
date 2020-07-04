[TOC]
# Overview
- Aeron是基于UDP的高性能协议和lib。
  - best practice文档中说了，上百个Channel/Stream就顶天了，别到1000。如果那么多的话，需要高层的协议
  - reliable multicast operation for modest receiver set size (< 100 receivers)
- RSocket支持使用多种底层协议，包括Aeron - http://rsocket.io
  - RSocket是one-to-one沟通的，所以不支持广播
    - https://rsocket.io/docs/Protocol.html
    - one-to-one communication
  - 倒是可以作为高速接入的方式
    - 但是接入方式使用它的话，还要给客户一个封装的lib，否则有点费劲

# Aeron具体功能研究
- 基本功能：局域网low latency广播
  - 如何写这部分代码

- 质量需求：消息不丢失
  - 原理是什么
  - 设计和实施几个测试case，验证它
  - 出现问题时候如何发现，以及可能的补偿
  
- 封装基本代码，后序供matching使用  


# 基本功能：局域网low latency广播

# 质量需求：消息不丢失
- 原理是什么
- 设计和实施几个测试case，验证它
- 出现问题时候如何发现，以及可能的补偿
  
# 封装基本代码，后序供matching使用    