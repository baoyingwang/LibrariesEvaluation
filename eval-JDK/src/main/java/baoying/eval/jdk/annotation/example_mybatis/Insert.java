package baoying.eval.jdk.annotation.example_mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * https://github.com/walidake/Annotation/blob/master/src/main/java/com/walidake/annotation/mybatis/Insert.java
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Insert {
    public String value();
}