package baoying.eval.db.concurrentupdate;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

import static java.sql.Connection.TRANSACTION_REPEATABLE_READ;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception{

        logger.info("begin : ?", "db lock test app");

        //https://www.baeldung.com/java-connection-pooling
        BasicDataSource ds = new BasicDataSource();
        //ds.setUrl("jdbc:h2:mem:test");
        ds.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:test");
        ds.setUsername("user");
        ds.setPassword("password");
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
        ds.setDefaultAutoCommit(false);

        //这个isolation level非常重要，因为这是对lock的测试
        ds.setDefaultTransactionIsolation(TRANSACTION_REPEATABLE_READ);

        PrepareData prepareData = new PrepareData(ds);
        prepareData.cleanTable();
        prepareData.popuateData();

        new InsertService(ds).start();
        new UpdateService(ds).start();

    }

    static void closeConn(Connection conn){
        try{
            if (conn != null) {
                conn.close();
            }
        }catch (Exception ignore){

        }
    }

    static void rollbackConn(Connection conn){
        try{
            if (conn != null) {
                conn.rollback();
            }
        }catch (Exception e){
            logger.error("rollback e", e);
        }
    }
}
