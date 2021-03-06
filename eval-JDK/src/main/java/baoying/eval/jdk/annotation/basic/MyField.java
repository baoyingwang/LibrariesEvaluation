package baoying.eval.jdk.annotation.basic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * from https://www.jianshu.com/p/7a02ddfb0e13
 *
 * @Author: feiweiwei
 * @Description:
 * @Created Date: 16:07 17/9/22.
 * @Modify by:
 */
@Documented
@Target({ ElementType.FIELD})
@Retention(RUNTIME)
public @interface MyField {

    String value() default "";
    String name() default "";
    String type() default"String";

}