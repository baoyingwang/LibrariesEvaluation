package baoying.eval.jdk.annotation.example_mybatis;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

//import com.walidake.dbcp.util.DBUtil;
//import com.walidake.dbcp.util.ResultSetMapper;

/**
 * https://github.com/baoyingwang/Annotation/blob/master/src/main/java/com/walidake/annotation/mybatis/DaoOperatorFactory.java
 * TODO 可以用工厂模式把代码抽出  在那之前暂时放在这里吧
 *
 * @author walidake
 *
 */
public final class DaoOperatorFactory {

    /**
     * 针对不同的方法进行不同的操作
     *
     * @param method
     * @param parameters
     * @throws SQLException
     *
     * @return object
     */
    public static Object handle(Method method, Object[] parameters)
            throws SQLException {
        String sql = null;
        if (method.isAnnotationPresent(Insert.class)) {
            sql = checkSql(method.getAnnotation(Insert.class).value(),
                    Insert.class.getSimpleName());
            insert(sql, parameters);
            return null;
        }
        return null;
    }


    /**
     * 插入记录
     *
     * @param sql
     * @param parameters
     * @throws SQLException
     */
    private static void insert(String sql, Object[] parameters) throws SQLException {
        //WARN Baoying: 这里故意去掉了一部分代码，因为只是想表达整个能够工作
        Connection connection = null; // = DBUtil.getConnection();
        PreparedStatement prepareStatement = connection.prepareStatement(sql);
        for (int i = 0; parameters != null && i < parameters.length; i++) {
            prepareStatement.setObject(i + 1, parameters[i]);
        }
        prepareStatement.execute();
        connection.close();
    }

    /**
     * 检查sql语句
     *
     * @param sql
     * @param type
     * @return
     * @throws SQLException
     */
    private static String checkSql(String sql, String type) throws SQLException {
        String type2 = sql.split(" ")[0];
        if (type2 == null || !type2.equalsIgnoreCase(type)) {
            throw new SQLException("Incorrect SQL.");
        }
        return sql;
    }
}