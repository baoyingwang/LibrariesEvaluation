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

    private Logger logger = LoggerFactory.getLogger(InsertService.class);

    private BasicDataSource dataSource;
    private int stkStart;
    private int stkEndEx;
    private int nextInsertStkId;
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
        }, 1,5, TimeUnit.MILLISECONDS);

    }




    private void execute(){

        if(this.nextInsertStkId < this.stkStart){
            logger.warn("no more insert since reached the last:{}", this.nextInsertStkId);
            return;
        }


        String nextStkId = String.format("%06d",this.nextInsertStkId);
        int intNextStkId = Integer.parseInt(nextStkId);

        long startMS = System.currentTimeMillis();
        logger.info("begin");

        App.insertData(this.dataSource, intNextStkId, intNextStkId+1);

        logger.info("end, inserted:{} cost={}ms", intNextStkId, System.currentTimeMillis()-startMS);

        this.nextInsertStkId = this.nextInsertStkId-1;


    }
}
