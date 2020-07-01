package baoying.eval.db.concurrentupdate;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class InsertService {

    private Logger logger = LoggerFactory.getLogger(InsertService.class);

    private BasicDataSource dataSource;
    private int stkStart;
    private int stkEndEx;
    private int nextInsertStkId;

    private int delayMS = 2;

    ScheduledExecutorService followingUpdateExecutorService = new ScheduledThreadPoolExecutor(2);


    InsertService(BasicDataSource dataSource, int stkStart, int stkEndEx){
        this.dataSource = dataSource;
        this.stkStart = stkStart;
        this.stkEndEx = stkEndEx;
        this.nextInsertStkId = this.stkEndEx-1; //故意从尾部开始，造成每次update lock都是新记录
    }


    public void start(){
        int poolSize = 1;
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(poolSize);

        scheduledExecutorService.scheduleWithFixedDelay(()->{
            this.execute();
        }, 100,delayMS, TimeUnit.MILLISECONDS);

    }

    public InsertService withDelay(int delay){
        this.delayMS = delay;
        return this;
    }




    private void execute(){

        if(this.nextInsertStkId < this.stkStart){
            logger.warn("no more insert since reached the last:{}", this.nextInsertStkId);
            return;
        }


        String nextStkId = String.format("%06d",this.nextInsertStkId);
        int intNextStkId = Integer.parseInt(nextStkId);

        long startMS = System.currentTimeMillis();
        //logger.info("begin");

        App.insertData(this.dataSource, intNextStkId, intNextStkId+1);

        //模拟真实场景，上面insert之后，再新线程中锁定并更新它
        followingUpdateExecutorService.execute(()->{
            Connection conn= null;
            try{
                long start = System.currentTimeMillis();
                conn = dataSource.getConnection();
                App.lock(conn, nextStkId);
                App.update(conn, nextStkId);
                conn.commit();
                conn.close();
                logger.info("lock&update the inserted row:{} - cost:{} ms", nextStkId, System.currentTimeMillis()-start);
            }catch (Exception e){
                logger.error("exception while update after insert", e);
                App.rollbackConn(conn);
            }finally {
                App.closeConn(conn);
            }

        });

        this.nextInsertStkId = this.nextInsertStkId-1;
        logger.info("end, inserted:{} cost={}ms", intNextStkId, System.currentTimeMillis()-startMS);


    }


}
