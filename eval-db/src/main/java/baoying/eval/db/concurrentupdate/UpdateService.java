package baoying.eval.db.concurrentupdate;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UpdateService {

    private Logger logger = LoggerFactory.getLogger(UpdateService.class);

    private BasicDataSource dataSource;

    UpdateService(BasicDataSource dataSource){
        this.dataSource = dataSource;
    }

    public void start(){

        int poolSize = 8;
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(poolSize);

        scheduledExecutorService.scheduleWithFixedDelay(()->{
            String[] stdIds = this.getStkIds(20);
            this.execute(stdIds);
        }, 1,20, TimeUnit.MILLISECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(()->{
            String[] stdIds = this.getStkIds(20);
            this.execute(stdIds);
        }, 3,20, TimeUnit.MILLISECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(()->{

            String[] stdIds = this.getStkIds(20);
            this.execute(stdIds);
        }, 5,20, TimeUnit.MILLISECONDS);

    }

    private String[] getStkIds(int count){
        int stkIdCount = count;
        String[] stdIds = new String[stkIdCount];
        for(int i=0; i<stkIdCount; i++){
            stdIds[i] = String.format("%06d", i+1);
        }

        return stdIds;
    }

    private void execute(String[] stkIds){
        if(stkIds == null || stkIds.length == 0){
            logger.warn("empty stkIds");
            return;
        }
        logger.info("begin");

        String lockSql = "update BaoyingT3Order" +
                "(select seriamnum from BaoyingT3Order " +
                "   where     serialnum=? " +
                "         and ordertime=?" +
                "         and exchid=?"+
                " order by serialmnum) " +
                " set serialnum=serialnum";

        String updateSql = "update BaoyingT3ORder" +
                " set offerregid=?";

        String serialNum = "9001";
        String ordertime = "123456789";
        String exchid = "0";

        Connection conn = null;
        try{

            conn = dataSource.getConnection();

            PreparedStatement lockSt = conn.prepareStatement(lockSql);
            lockSt.setString(1, serialNum);
            lockSt.setString(2, ordertime);
            lockSt.setString(3, exchid);
            int lockedCount = lockSt.executeUpdate();
            if(lockedCount <= 0){
                logger.error("nothing locked, what happened? lockedCount:?", lockedCount);
            }

            for(String stkId: stkIds){
                PreparedStatement updateSt = conn.prepareStatement(updateSql);
                updateSt.setString(1,stkId);
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
