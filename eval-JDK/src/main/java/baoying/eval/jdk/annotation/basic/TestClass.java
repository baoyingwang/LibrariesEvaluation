package baoying.eval.jdk.annotation.basic;

/**
 * @Author: feiweiwei
 * @Description:
 * @Created Date: 16:14 17/9/22.
 * @Modify by:
 */
@MyType(value = "test", className = "TestClass")
public class TestClass {

    @MyField(value = "testNum", name="num", type = "int")
    private int num;

    @MyField(value = "testName", name="name", type = "String")
    private String name;

    @MyMethod(value = "print ")
    public String print(){
        return "hello annotation";
    }
}