[TOC]
# Overview
Annotation在java中随处可见，关于注解的文章也很多。
尤其是在Spring Boot/Cloud中，我看可以看到一个简单的Annotation，完成了非常非常多的工作，而使用者非常简单

这里，我着重evaluate平时见到注解的背后工作原理，以及我们可能用到的一些地方

# 基本情况
## 基本概念
- 这里面定义了Runtime类型注解对于class，method，field上面的3中不同类型的注解例子
  - https://www.jianshu.com/p/7a02ddfb0e13
  - 相关代码也可以在package baoying.eval.jdk.annotation看到
- 我们可以看到，定义注解和apply注解都很简单
- 但是具体根据注解去运行相关的业务代码还是有点麻烦
  - 因为我们需要通过其classloader来获取相关annotation的定义
  - 一般日常编码中很少使用classloader
  - 所以，不难推断，要使用注解，必然要自己通过proxy等方式对调用方法进行拦截，然后根据注解做更多操作
## 基本使用
- 参考 https://github.com/walidake/Annotation
  - 其自定义对mybatis注解例子中（https://github.com/walidake/Annotation/tree/master/src/main/java/com/walidake/annotation/mybatis）
    - 定义注解名称 - 这只是一个标识, 如Insert.java, Update.java
    - 把注解apply到一个类上面(UserMapper.java) 
    - 拦截使用注解类对象的创建，增加对相关注解的处理
      - 创建UserMapper对象的时候，通过reflection.Proxy特性，使得执行其方法的时候会先解析注解
      - 参考在MybatisTest类中其这样创建UserMapper mapper = MethodProxyFactory.getBean(UserMapper.class);
```
定义一个annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Insert {
	public String value();
}

apply到UserMapp.java上面
public interface UserMapper {
    @Insert("insert into user (name,password) values (?,?)")
    public void addUser(String name, String password);
    //其他方法删掉了
}

使用 UserMapper mapper = MethodProxyFactory.getBean(UserMapper.class);
{
    class MethodProxy implements InvocationHandler {
    
        @Override
        public Object invoke(Object proxy, Method method, Object[] parameters)
                throws Throwable {

            //最终介些注解的代码在这里：：DaoOperatorFactory
            return DaoOperatorFactory.handle(method, parameters);
        }
}


```    


# Spring中Annotation使用
## 增加一个自定义annotation
## Spring Cloud中的Annotation是如何生效的（大部分命名为EnableXXXX)
- 写这个的文章也很多，譬如
  - https://blog.csdn.net/andy_zhang2007/article/details/83957588
    - 该文中给出了Spring EnableXXX注解的所有4中方式（用1个注解，来完成4中不同bean的引入）
  - https://www.jianshu.com/p/3da069bd865c
    - 这个例子中就是上面提到的4中bean中的ImportBeanDefinitionRegistrar这种
- 见package baoying.eval.spring.boot.annotation.enable_annotation_ex 中更加详细的解释
  - 里面有一个md，看那个（主要是注解类中增加类一些注释）
