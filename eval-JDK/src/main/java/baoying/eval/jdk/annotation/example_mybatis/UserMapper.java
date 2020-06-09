package baoying.eval.jdk.annotation.example_mybatis;


//import java.util.List;

/**
 * https://github.com/walidake/Annotation/blob/master/src/main/java/com/walidake/annotation/mybatis/UserMapper.java
 * Baoying: 这里仅仅参考了Insert注解，所以别的例子就去掉了
 */
public interface UserMapper {

    @Insert("insert into user (name,password) values (?,?)")
    public void addUser(String name, String password);

//    @Select("select * from user")
//    public List<User> findUsers();
//
//    @Select("select * from user where name = ?")
//    public User getUser(String name);
//
//    @Update("update user set password=? where name=?")
//    public void updateUser(String password, String name);
//
//    @Delete("delete from user where name=?")
//    public void deleteUser(String name);

}