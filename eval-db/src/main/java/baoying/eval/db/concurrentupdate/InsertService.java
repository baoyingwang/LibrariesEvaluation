package baoying.eval.db.concurrentupdate;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class InsertService {

    private Logger logger = LoggerFactory.getLogger(UpdateService.class);

    private BasicDataSource dataSource;
    InsertService(BasicDataSource dataSource){
        this.dataSource = dataSource;
    }


    public void start(){
        int poolSize = 1;
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(poolSize);

        scheduledExecutorService.scheduleWithFixedDelay(()->{
            this.execute();
        }, 1,500, TimeUnit.MILLISECONDS);

    }

    private String nextStkId(){
        String sql = "select max(stkId) from BaoyingT3Order";

        Connection conn = null;
        String nextStkId = "";
        try{

            conn = dataSource.getConnection();

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            String maxStkId = "";
            while(rs.next()){
                maxStkId = rs.getString(1);
            }

            if(maxStkId.length() == 0){
                maxStkId = "000000";
            }

            int intMaxStkId = Integer.parseInt(maxStkId);
            int intNextStkId = intMaxStkId + 1;
            nextStkId = String.format("%06d", intMaxStkId);

            conn.commit();

        }catch (Exception e){
            logger.error("error", e);
            App.rollbackConn(conn);
        }finally {
            App.closeConn(conn);
        }

        return nextStkId;

    }

    private void execute(){

        logger.info("begin");

        String insertSql = "insert into BaoyingT3Order( SERIALNUM    ,  ORDERTIME    ,  STKID        ,  CONTRACTNUM ,  EXCHID      ,  REGID       ,  OFFERREGID  ,  DESKID     )values\n" +
                "(?, ?, ?, 'CONTRACTNUM1','0','REGID3','OFFREGID4','DESKID5');";

        String serialNum = "9001";
        String ordertime = "123456789";
        String stkId = this.nextStkId();

        Connection conn = null;
        try{

            conn = dataSource.getConnection();

            PreparedStatement lockSt = conn.prepareStatement(insertSql);
            lockSt.setString(1, serialNum);
            lockSt.setString(2, ordertime);
            lockSt.setString(3, stkId);
            int inserteCount = lockSt.executeUpdate();
            if(inserteCount <= 0){
                logger.error("insert failed:", inserteCount);
            }

            conn.commit();

        }catch (Exception e){
            logger.error("error", e);
            App.rollbackConn(conn);
        }finally {
            App.closeConn(conn);
        }


        logger.info("end");

    }
}
