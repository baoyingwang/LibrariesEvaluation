package baoying.eval.db.jdbc;


// javac -cp D:/dev/projects/integrate/build/apps/dlm/cis_ds_oracle/lib/ojdbc6.jar -d . JdbcTest.java
// java -cp D:/dev/projects/integrate/build/apps/dlm/cis_ds_oracle/lib/ojdbc6.jar;. JdbcTest
import java.sql.*;


public class JDBCTest
{
    public static void main(String[] args) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultSet = null;


        try {
            Class.forName("oracle.jdbc.OracleDriver");

            //service name
            connect = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.188:1521:HR92DEV", "SYSADM", "password");
            System.out.println("connect ok");

            statement = connect.createStatement();
            System.out.println("create statement ok");

            resultSet = statement.executeQuery("select 1 from dual");
            System.out.println("execute query ok");



        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(resultSet!=null){
                    resultSet.close();
                }
                if(statement!=null) {
                    statement.close();
                }
                if(connect!=null){
                    connect.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}