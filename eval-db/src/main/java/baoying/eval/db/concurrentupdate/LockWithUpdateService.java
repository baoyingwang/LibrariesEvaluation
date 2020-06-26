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

    private int lockWithUpdate(Connection conn, String lockSql) throws Exception{
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

    private int lockMinStkIdWithSelectUpdate(Connection conn) throws Exception{

        String minStkId = null;
        {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select min(stkId) from BaoyingT3Order");

            while(rs.next()){
                minStkId = rs.getString(1);
            }
            //no lock until here
            rs.close();
            st.close();
        }


        int locked = 0;
        PreparedStatement pst = conn.prepareStatement("select stkId from BaoyingT3Order " +
                "   where     serialnum=? " +
                        "         and ordertime=?" +
                        "         and exchid=?"+
                        "         and knockqty=0"+
                        "         and stkId=?"
                + " for update");
        pst.setString(1, this.serialNum);
        pst.setInt(2, this.ordertime);
        pst.setString(3, this.exchid);
        pst.setString(4, minStkId);
        ResultSet rs = pst.executeQuery();
        while(rs.next()){
            logger.info("got select-for-update data:", rs.getString(1));
            locked++;
        }
        rs.close();
        pst.close();

        return locked;

    }

    private List<String> selectLockRelatedRecords(Connection conn) throws Exception{

        String selectLockedSql = "select stkId from BaoyingT3Order " +
                "   where     serialnum=? " +
                "         and ordertime=?" +
                "         and exchid=?"+
                "         and knockqty=0"+
                " order by stkId desc";

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

    private void execute(String lockSql){

        long startMS = System.currentTimeMillis();
        logger.info("begin");

        Connection conn = null;
        int selected = 0;
        int lockedCount = 0;
        try{

            conn = dataSource.getConnection();

            lockedCount = lockWithUpdate(conn, lockSql);
            logger.info("locked:{}", lockedCount);

            List<String> stkIds = selectLockRelatedRecords(conn);
            if(lockedCount != stkIds.size()){
                logger.warn("diff lock size:{} and select size:{}, because more data inserted", lockedCount, stkIds.size());
            }

            //sleep a well to simulate real env
            TimeUnit.MILLISECONDS.sleep(15);

            //这里是破坏锁定顺序的关键
            //再次申请锁定的时候，这一小段时间内插入的新数据（我每次插入新数据的stkId都小，保证orderby在前边，先被锁定）。
            // 然后这个新数据可能被其他线程已经锁定，其他线程可能正在等待当前线程释放就数据，造成了死锁问题
            //另外，这里如果使用其他方式的lock去锁定新记录（select for udpate这条新数据），可能也造成死锁
            //下面代码两个方法（lockWithUpdate，lockMinStkIdWithSelectUpdate）都证明了只要动那个新的insert，就可能出问题

            //lockedCount = lockWithUpdate(conn, lockSql);
            //logger.info("again locked:{}", lockedCount);

            lockedCount = lockMinStkIdWithSelectUpdate(conn);

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
