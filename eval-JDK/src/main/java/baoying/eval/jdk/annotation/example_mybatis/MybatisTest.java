package baoying.eval.jdk.annotation.example_mybatis;

import org.junit.Test;

/**
 *
 * https://github.com/walidake/Annotation/blob/master/src/main/java/com/walidake/annotation/mybatis/MybatisTest.java
 *
 * 自定义mybatis测试样例
 *
 * @author walidake
 *
 */
public class MybatisTest {

    @Test
    public void addUser(){
        UserMapper mapper = MethodProxyFactory.getBean(UserMapper.class);
        mapper.addUser("walidake", "665908");
    }

}