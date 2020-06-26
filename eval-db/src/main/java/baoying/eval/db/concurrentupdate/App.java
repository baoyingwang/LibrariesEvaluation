package baoying.eval.db.concurrentupdate;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * avoid logging cause latency https://logging.apache.org/log4j/2.x/manual/async.html
 * -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
 *
 */
public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception{

        logger.info("begin:{}", "db lock test app");

        //https://www.baeldung.com/java-connection-pooling
        BasicDataSource ds = new BasicDataSource();
        //ds.setUrl("jdbc:h2:mem:test");
        String magic = System.getenv("magic");
        ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        ds.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:"+magic);
        ds.setUsername(magic);
        ds.setPassword(magic);
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
        ds.setDefaultAutoCommit(false);

        //这个isolation level非常重要，因为这是对lock的测试
        ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);


        PrepareData prepareData = new PrepareData(ds);
        prepareData.recreateTable();
        prepareData.cleanTable();
        insertData(ds, 300000, 300009);
        insertData(ds, 600000, 600009);
        insertData(ds, 999990, 999999);

        InsertService insertService2 = new InsertService(ds, 200000, 299999);
        InsertService insertService4 = new InsertService(ds, 400000, 499999);
        InsertService insertService8 = new InsertService(ds, 800000, 899999);

        insertService2.start();
        insertService4.start();
        insertService8.start();


        new LockWithUpdateService(ds).start();

        TimeUnit.MINUTES.sleep(10);
        System.exit(0);

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


    static void  insertData(DataSource dataSource, int stkIdStart, int stkIdEndEx){

        String insertSql = "insert into BaoyingT3Order( SERIALNUM    ,  ORDERTIME    ,  STKID        ,  "
                + "CONTRACTNUM ,  EXCHID      ,  REGID       ,  "
                + "OFFERREGID  ,  DESKID     , KNOCKQTY)values\n" +
                "(?, ?, ?, "
                + "'CONTRACTNUM1','0','REGID3',"
                + "'OFFREGID4','DESKID5', 0)";

        String serialNum = "9001";
        int ordertime = 123456789;


        Connection conn = null;
        try{

            conn = dataSource.getConnection();
            PreparedStatement lockSt = conn.prepareStatement(insertSql);
            for(int i=stkIdStart; i<stkIdEndEx; i++){
                String stkId = String.format("%06d", i);

                lockSt.setString(1, serialNum);
                lockSt.setInt(2, ordertime);
                lockSt.setString(3, stkId);
                int inserteCount = lockSt.executeUpdate();
                if(inserteCount <= 0){
                    logger.error("insert failed:", inserteCount);
                }
            }

            //more time to simulate real call
            Thread.sleep(2);
            conn.commit();

        }catch (Exception e){
            logger.error("error", e);
            App.rollbackConn(conn);
        }finally {
            App.closeConn(conn);
        }
    }
}
