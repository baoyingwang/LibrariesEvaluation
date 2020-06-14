package baoying.eval.spring.boot.annotation.enableannotationexample;



import org.springframework.context.annotation.Bean;

/**
 *
 * https://blog.csdn.net/andy_zhang2007/java/article/details/83957588
 *
 * 一个 Spring 配置类， 注解方式注册bean的典型用法 ：
 *
 * @Configuration 定义配置类 + @Bean 注解配置类方法注册bean
 *
//Baoying：这个Configuration注解特意去掉了，否则不需要什么annotation，直接springboot自己就给初始化好了
//@Configuration
 */
public class MyUserBeansConfig {
    /**
     * 注册管理员管理服务组件bean
     * AdminService 是一个普通Java类
     *
     * @return
     */
    @Bean
    public MyAdminService adminService() {
        return new MyAdminService();
    }

    /**
     * 注册客户管理服务组件bean
     * CustomerService 是一个普通Java类
     *
     * @return
     */
    @Bean
    public MyCustomerService customerService() {
        return new MyCustomerService();
    }
}
