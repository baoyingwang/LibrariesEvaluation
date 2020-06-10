package baoying.eval.spring.boot.annotation.enable_annotation_ex;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 *
 * https://blog.csdn.net/andy_zhang2007/java/article/details/83957588
 *
 * 一个 ImportSelector 实现类
 * 用于演示通过 ImportSelector ，给定一组要注册为bean组件的普通Java类的名称，
 * 然后通过利用 @Import(MyLogServiceImportSelector.class)即可注册相应的bean
 */
public class MyLogServiceImportSelector implements ImportSelector {

    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 返回要注册成为 bean 的类的全名称的数组
        return new String[]{MyLogService.class.getName()};
    }
}
