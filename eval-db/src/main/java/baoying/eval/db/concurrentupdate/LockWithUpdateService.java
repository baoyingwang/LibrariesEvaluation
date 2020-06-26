package baoying.eval.db.concurrentupdate;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LockWithUpdateService {

    private Logger logger = LoggerFactory.getLogger(LockWithUpdateService.class);

    private BasicDataSource dataSource;

    LockWithUpdateService(BasicDataSource dataSource){
        this.dataSource = dataSource;
    }

    public void start(){

        String lockSqlOrderByStkIdAsc = "update" +
                "(select serialnum from BaoyingT3Order " +
                "   where     serialnum=? " +
                "         and ordertime=?" +
                "         and exchid=?"+
                "         and knockqty=0"+
                " order by stkId asc) " +
                " set serialnum=serialnum";
        int poolSize = 8;
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(poolSize);

        for(int i=0; i< 3; i++){
            final int j = i+1;
            scheduledExecutorService.scheduleWithFixedDelay(()->{
                this.execute(lockSqlOrderByStkIdAsc);
            }, 100,1, TimeUnit.MILLISECONDS);
        }

        for(int i=0; i< 5; i++){
            final int j = i+1;
            scheduledExecutorService.scheduleWithFixedDelay(()->{
                this.execute(lockSqlOrderByStkIdAsc);
            }, 200,1, TimeUnit.MILLISECONDS);
        }

    }

    String serialNum = "9001";
    int ordertime = 123456789;
    String exchid = "0";

    private int lock(Connection conn, String lockSql) throws Exception{
        PreparedStatement updateSt = conn.prepareStatement(lockSql);

        PreparedStatement lockSt = conn.prepareStatement(lockSql);
        lockSt.setString(1, serialNum);
        lockSt.setInt(2, ordertime);
        lockSt.setString(3, exchid);
        int lockedCount = lockSt.executeUpdate();
        if(lockedCount <= 0){
            logger.error("nothing locked, what happened? lockedCount:{}", lockedCount);
        }

        return lockedCount;
    }

    private void execute(String lockSql){

        long startMS = System.currentTimeMillis();
        logger.info("begin");

        String selectLockedSql = "select stkId from BaoyingT3Order " +
                "   where     serialnum=? " +
                "         and ordertime=?" +
                "         and exchid=?"+
                "         and knockqty=0"+
                " order by stkId desc";



        Connection conn = null;
        int selected = 0;
        int lockedCount = 0;
        try{

            conn = dataSource.getConnection();

            lockedCount = lock(conn, lockSql);
            logger.info("locked:{}", lockedCount);

            List<String> stkIds = new ArrayList<>();
            PreparedStatement selectStatement = conn.prepareStatement(selectLockedSql);
            selectStatement.setString(1, serialNum);
            selectStatement.setInt(2, ordertime);
            selectStatement.setString(3, exchid);
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()){
                String stkId = rs.getString(1);
                stkIds.add(stkId);
                selected++;
            }

            if(lockedCount != stkIds.size()){
                logger.warn("diff lock size:{} and select size:{}!=================================", lockedCount, stkIds.size());
            }

            //sleep a well to simulate real env
            TimeUnit.MILLISECONDS.sleep(15);
            
            lockedCount = lock(conn, lockSql);
            logger.info("again locked:{}", lockedCount);
            
            conn.commit();

        }catch (Exception e){
            logger.error("error", e);
            App.rollbackConn(conn);
        }finally {
            App.closeConn(conn);
        }

        logger.info("end, locked:{}, selected:{}, cost={}ms", lockedCount, selected, System.currentTimeMillis()-startMS);
    }

}
