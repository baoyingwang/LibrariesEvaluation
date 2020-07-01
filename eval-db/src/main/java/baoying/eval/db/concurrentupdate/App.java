package baoying.eval.db.concurrentupdate;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

/**
 * avoid logging cause latency https://logging.apache.org/log4j/2.x/manual/async.html
 * -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
 *
 */
public class App {

    static String serialNum = "9001";
    static int ordertime = 123456789;
    static String exchid = "0";

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception{

        App app = new App();
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
        ds.setMaxTotal(40);

        //这个isolation level非常重要，因为这是对lock的测试
        ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);


        PrepareData prepareData = new PrepareData(ds);
        prepareData.recreateTable();
        prepareData.cleanTable();
        app.insertData(ds, 900000, 900999);
        //app.insertData(ds, 700000, 700999);
        //app.insertData(ds, 300000, 600999);


        new InsertService(ds, 200000, 299999).withDelay(2000).start();
        //new InsertService(ds, 800000, 899999).withDelay(300).start();


        new LockWithUpdateService(ds).withSleep(5).start();

        TimeUnit.MINUTES.sleep(30);
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

            //故意混淆插入顺序，使得排序非常关键
            for(int i=stkIdStart; i<stkIdEndEx; i++){

                if(i %2 == 0){
                    continue;
                }
                String stkId = App.toStrStkId(i);

                lockSt.setString(1, serialNum);
                lockSt.setInt(2, ordertime);
                lockSt.setString(3, stkId);
                int inserteCount = lockSt.executeUpdate();
                if(inserteCount <= 0){
                    logger.error("insert failed:", inserteCount);
                }
            }

            for(int i=stkIdStart; i<stkIdEndEx; i++){
                if(i %2 == 1){
                    continue;
                }

                String stkId = App.toStrStkId(i);

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
            conn.close();

        }catch (Exception e){
            logger.error("error", e);
            App.rollbackConn(conn);
        }finally {
            App.closeConn(conn);
        }
    }


    static void update(Connection conn, String stkId) throws Exception{

        String selectLockedSql = "update BaoyingT3Order " +
                " set serialnum=?" +
                "   where     serialnum=? " +
                "         and ordertime=?" +
                "         and exchid=?"+
                "         and knockqty=0" +
                "         and stkId =?"
                ;

        PreparedStatement selectStatement = conn.prepareStatement(selectLockedSql);
        selectStatement.setString(1, serialNum);
        selectStatement.setString(2, serialNum);
        selectStatement.setInt(3, ordertime);
        selectStatement.setString(4, exchid);
        selectStatement.setString(5, stkId);
        selectStatement.executeUpdate();

        selectStatement.close();

    }

    static void lock(Connection conn, String stkId) throws Exception{

        String selectLockedSql = "select * from BaoyingT3Order " +
                "   where     serialnum=? " +
                "         and ordertime=?" +
                "         and exchid=?"+
                "         and knockqty=0" +
                "         and stkId =?"
                + " for update"
                ;

        PreparedStatement selectStatement = conn.prepareStatement(selectLockedSql);
        selectStatement.setString(1, serialNum);
        selectStatement.setInt(2, ordertime);
        selectStatement.setString(3, exchid);
        selectStatement.setString(4, stkId);
        selectStatement.executeUpdate();

        selectStatement.close();

    }

    static String toStrStkId(int intStkId)
    {
        return String.format("%06d", intStkId);
    }
}
