package baoying.eval.spring.boot.annotation.enable_annotation_ex;


import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

/**
 *
 * https://blog.csdn.net/andy_zhang2007/java/article/details/83957588
 *
 * 一个接口ImportBeanDefinitionRegistrar的实现类，
 * 用于演示 , 演示点:
 * 1. 程序化将普通Java类作为bean注册到Spring IoC容器;
 * 2. 使用注解元数据属性动态决定bean的注册;
 */
public class MyOrderServiceBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    /**
     * @param importingClassMetadata 注解元数据
     * @param registry               Spring IoC 容器
     */
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(
                        EnableMyOwnBeanDefinitions.class.getName()));
        {// 注册类型为 OrderService 的订单服务组件 bean
            Class beanClass = MyOrderService.class;
            RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass);
            String beanName = StringUtils.uncapitalize(beanClass.getSimpleName());
            registry.registerBeanDefinition(beanName, beanDefinition);
        }

        {// 看情况决定是否注册类型为 OrderChangeRecordService 的订单变更记录服务组件 bean
            boolean trackOrderChange = annotationAttributes.getBoolean("trackOrderChange");
            if (trackOrderChange) {
                Class beanClass = MyOrderChangeRecordService.class;
                RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass);
                String beanName = StringUtils.uncapitalize(beanClass.getSimpleName());
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
        }
    }
}