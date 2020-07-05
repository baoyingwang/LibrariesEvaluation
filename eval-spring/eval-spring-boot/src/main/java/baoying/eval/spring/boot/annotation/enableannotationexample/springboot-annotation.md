[TOC]
#Overview
- Spring中，我们可以看到很多歌EnableXXX这样的annotation，譬如@EnableScheduling, @EnableAsync
  - 更多内置的类似annotation看这里 https://www.baeldung.com/spring-enable-annotations
  - 通过这样的注解，可以把该注解相关的bean或者功能引入到本项目中来
  
- 这里我们将通过例子证明自定义一个EnableXXX，使得相关的bean直接注册到spring container中来
  - 这里的注解名称为 @EnableMyOwnBeanDefinitions
  - 一般加到Application（main入口）
  - 其相关的类自动注册为bean，你可以通过@Autowire直接使用
  - 下面有更多介绍

# 例子简介
- 这里定义了一个EnableMyOwnBeanDefinitions
- 通过其Import参数，可以看到我们引入了4中不同类型的Bean，因为我们要演示如何增加这4中bean
  - 在没有使用@EnableMyOwnBeanDefinitions 情况下，这些Import中的类是不会形成bean的
  - 一旦引入了，则bean自动生成了

# Note
本package中的类全部来自于 https://blog.csdn.net/andy_zhang2007/article/details/83957588
仔细看EnableMyOwnBeanDefinitions这个类，我还增加了一些注释

# TODO
## 调查/实验 类似于 @EnableAsync和@EnableSchduling各自如何工作以及如何配合的
他们与我上面例子不一样