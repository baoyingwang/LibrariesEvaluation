- TODO
  - 协议
  - Lib/版本/最近是否活跃
  - 认证 - OAuth - https://grpc.io/docs/guides/auth/ https://grpc.io/docs/languages/objective-c/oauth2/
  - 授权 
  - 监控 
  - 服务部署 
  - HTTP2协议 
  - 学习了下怎么抓包调试 
  - 可能的疑难杂症
  - proto文件在开发缩放位置
  - Demo
  
- TODO - eval-dubbo，并且与grpc比较
  - 如 rpc框架之 grpc vs dubbo 性能比拼 / 20190419 / https://blog.csdn.net/magasea/article/details/89397641
  - dubbo直接完成grpc服务部分代码 - Dubbo 在跨语言和协议穿透性方向的探索：支持 HTTP/2 gRPC/https://zhuanlan.zhihu.com/p/93528787
  
- TODO - grpc服务发现
  - https://juejin.im/post/5d282476f265da1b80207252
    - 这个链接中（20190712）简略的介绍了两个当前常用方案
    - 其包含方案一.nginx + consul + consul-template
    - 其包含方案二.Envoy
    
  - k8s如何帮助/使用grpc，使得整个部署更加简化
    - 20200425/gRPC实战--Kubernetes中使用envoy负载均衡gRPC流量/https://zhuanlan.zhihu.com/p/136112210
    - 如  实用指南 | 基于Kubernetes, GRPC 和 Linkerd 构建可扩展微服务 / https://zhuanlan.zhihu.com/p/34264063
    - 扩展 - k8s vs service mesh：https://jimmysong.io/istio-handbook/preface/service-mesh-the-microservices-in-post-kubernetes-era.html
    - 扩展 - Service Mesh 初体验 2019/10/30/ https://zhuanlan.zhihu.com/p/89220823
      - 本文介绍了一些service mesh具体实现/组件，如lstio/envoy/mixer等等
      
  - Istio - 
    - https://istio.io/zh/docs/concepts/what-is-istio/
      - "为 HTTP、gRPC、WebSocket 和 TCP 流量自动负载均衡。"
      - "Istio 是独立于平台的，可以与 Kubernetes（或基础设施）的网络策略一起使用"
    - "简单的说，有了Istio，你的服务就不再需要任何微服务开发框架（典型如Spring Cloud，Dubbo）"
        - "也不再需要自己动手实现各种复杂的服务治理的功能（很多是Spring Cloud
        - "和Dubbo也不能提供的，需要自己动手）。
        - "只要服务的客户端和服务器可以进行简单的直接网络访问，
        - " 就可以通过将网络层委托给Istio，从而获得一系列的完备功能。
        - 链接：https://www.jianshu.com/p/a2cd02118ef1
        - ? 这么厉害？
   - Istio 中的主要模块 Envoy/Mixer/Pilot/Auth
   - 2019/01/17 使用 Istio 实现基于 Kubernetes 的微服务应用 / https://www.ibm.com/developerworks/cn/cloud/library/cl-lo-implementing-kubernetes-microservice-using-istio/index.html
     - 罗列了一大堆Istio的相关技术/组件，完成了一个示例