package baoying.eval.db.concurrentupdate;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LockWithUpdateService {

    private Logger logger = LoggerFactory.getLogger(LockWithUpdateService.class);

    private BasicDataSource dataSource;

    String lockSqlOrderByStkIdAsc = "update" +
            "(select serialnum from BaoyingT3Order " +
            "   where     serialnum=? " +
            "         and ordertime=?" +
            "         and exchid=?"+
            "         and knockqty=0"+
            " order by stkId asc) " +
            " set serialnum=serialnum";

    LockWithUpdateService(BasicDataSource dataSource){
        this.dataSource = dataSource;
    }

    public void start(){

        final int SCHEDULE_BY_NATIVE_TRHEAD = 1;
        final int SCHEDULE_BY_EXECUTOR = 2;


        int scheduleWay = SCHEDULE_BY_NATIVE_TRHEAD;
        switch (scheduleWay){
            case SCHEDULE_BY_EXECUTOR:
                scheduleByExecutor();
                break;
            case SCHEDULE_BY_NATIVE_TRHEAD:
                scheduleByNativeThreads(35);
                break;
        }

    }

    private void scheduleByExecutor(){
        int poolSize = 32;
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(poolSize);

        for(int i=0; i< 500; i++){
            final int j = i+1;
            scheduledExecutorService.scheduleWithFixedDelay(()->{
                this.execute(lockSqlOrderByStkIdAsc);
            }, 3,5, TimeUnit.NANOSECONDS);
        }
    }

    private void scheduleByNativeThreads(int threadNum){
        for(int i=0; i<threadNum; i++){

            Thread t = new Thread(()->{
                while(true){
                    this.execute(lockSqlOrderByStkIdAsc);
                }
            }, "Native-LockUpdate-Thread:"+i);
            t.start();

        }
    }

    String serialNum = "9001";
    int ordertime = 123456789;
    String exchid = "0";

    private int lockWithUpdate(Connection conn, String lockSql) throws Exception{

        long locktStart = System.currentTimeMillis();

        PreparedStatement lockSt = conn.prepareStatement(lockSql);
        lockSt.setString(1, serialNum);
        lockSt.setInt(2, ordertime);
        lockSt.setString(3, exchid);
        int lockedCount = lockSt.executeUpdate();
        if(lockedCount <= 0){
            logger.error("nothing locked, what happened? lockedCount:{}", lockedCount);
        }

        logger.info("lock took:{} ms", System.currentTimeMillis() - locktStart);
        return lockedCount;
    }


    private List<String> selectLockRelatedRecords(Connection conn) throws Exception{

        String selectLockedSql = "select stkId from BaoyingT3Order " +
                "   where     serialnum=? " +
                "         and ordertime=?" +
                "         and exchid=?"+
                "         and knockqty=0"+
                " order by stkId asc";

        List<String> stkIds = new ArrayList<>();
        PreparedStatement selectStatement = conn.prepareStatement(selectLockedSql);
        selectStatement.setString(1, serialNum);
        selectStatement.setInt(2, ordertime);
        selectStatement.setString(3, exchid);
        ResultSet rs = selectStatement.executeQuery();
        while (rs.next()){
            String stkId = rs.getString(1);
            stkIds.add(stkId);

        }

        return stkIds;
    }

    private int sleepDuringExecution = 0;
    public LockWithUpdateService withSleep(int sleep){
        this.sleepDuringExecution = sleep;
        return this;
    }
    private void execute(String lockSql){

        long startMS = System.currentTimeMillis();
        //logger.info("begin");

        Connection conn = null;
        int selected = 0;
        int lockedCount = 0;

        try{

            conn = dataSource.getConnection();

            lockedCount = lockWithUpdate(conn, lockSql);
            //logger.info("locked:{}", lockedCount);

            //可能会选出来更多的记录
            List<String> stkIds = selectLockRelatedRecords(conn);
            if(lockedCount != stkIds.size()){
                logger.debug("diff lock size:{} and select size:{}, because more data inserted", lockedCount, stkIds.size());
            }
            selected = stkIds.size();

            //更新之前既有的值（而不是后来insert的），也是为了模拟真实场景
            //既有的值stkId很大，而新insert的则很小。所以从stkIds尾部获取（已排序）
            String updateStkId = stkIds.get(stkIds.size()-1);

            App.update(conn, updateStkId);

            if(sleepDuringExecution > 0){
                //sleep a well to simulate real env
                TimeUnit.MILLISECONDS.sleep(sleepDuringExecution);
            }

            conn.commit();

        }catch (Exception e){
            logger.error("error" , e);
            App.rollbackConn(conn);
        }finally {
            App.closeConn(conn);
        }

        logger.info("end, locked:{}, selected:{}, cost={}ms", lockedCount, selected, System.currentTimeMillis()-startMS);
    }

}
