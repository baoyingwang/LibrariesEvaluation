[TOC]
#Overview
通过例子证明自定义一个EnableXXX（e.g. EnableMyOwnBeanDefinitions here)，
来说明这样的注解是如何创建一个Spring中这样的注解的。

# 例子简介
- 这里定义了一个EnableMyOwnBeanDefinitions
- 通过其Import参数，可以看到我们引入了4中不同类型的Bean，因为我们要演示如何增加这4中bean
  - 在没有使用@EnableMyOwnBeanDefinitions 情况下，这些Import中的类是不会形成bean的
  - 一旦引入了，则bean自动生成了

# Note
本package中的类全部来自于 https://blog.csdn.net/andy_zhang2007/article/details/83957588
仔细看EnableMyOwnBeanDefinitions这个类，我还增加了一些注释
