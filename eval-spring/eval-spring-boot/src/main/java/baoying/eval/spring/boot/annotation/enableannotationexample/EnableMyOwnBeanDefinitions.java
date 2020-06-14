package baoying.eval.spring.boot.annotation.enableannotationexample;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * https://blog.csdn.net/andy_zhang2007/java/article/details/83957588
 *
 * 只要在你的代码中
 * 1. 增加本package代码
 * 2. 增加 @EnableMyOwnBeanDefinitions到你的Applicaiton类中
 * 则所有Import中列出来的类都可以自动形成spring bean。你可以通过@Autowire直接使用
 *
 * 模仿 Spring 框架的 @EnableXXX 注解自定义的一个 @Enable 注解，
 * 此类注解一般使用 @Import 通过以下四种方式进行 bean 定义：
 * 1. @Configuration 注解的专门用于bean定义的类,一般通过@Bean注解的方法注册bean ;
 * 2. ImportSelector 给出某些要注册为bean的普通类的类名，将它们注册为 bean ;
 * 3. ImportBeanDefinitionRegistrar 直接基于某些普通类创建 BeanDefinition 并注册相应的 bean ,可以有较复杂的逻辑;
 * 4. 直接将某个普通类作为一个bean注册到容器。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        //Baoying: 这里4个方式与上面表述的4中方式1:1对应
        //这里，将下面列出的相关bean全部注册到spring中
        MyUserBeansConfig.class, //Baoying:  这个类的方法中返回很多Bean，则当前类（MyUserBeanConfig）这里import之后，其各个方法返回的bean也就都生效了
        MyLogServiceImportSelector.class, //Baoying：通过Selector，可以初始化其（selelctor）内部指定的所有普通类为bean
        MyOrderServiceBeanDefinitionRegistrar.class, //Baoying：在对应代码中决定要注册哪些Bean
        MySettingService.class}) //Baoying：这个就是直接把当前类注册到容器中
public @interface EnableMyOwnBeanDefinitions {
    /**
     * 注解属性：是否记录订单变更，语义 ：
     * false -- 不注册订单变更记录服务组件bean
     * true -- 注册订单变更记录服务组件bean
     *
     * @return
     */
    boolean trackOrderChange() default false;
}