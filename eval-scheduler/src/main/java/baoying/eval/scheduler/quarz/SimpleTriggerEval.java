package baoying.eval.scheduler.quarz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.Instant;


public class SimpleTriggerEval {
    public static void main(String[] args) throws Exception{


        SimpleTrigger simpleTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity("dupTrigger", "group")
                .startNow() //从现在开始
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(5) // 每5秒钟运行一次
                        .withRepeatCount(2)) // 共执行两次（开始那一次不算），所以要执行最开始1次的话这里设置为0，start()就行了
                .build();


        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();

        JobDetail jobDetail = JobBuilder.newJob(LoggerJob.class)
                .usingJobData("jobDataKey1","jobDataVal1") //这个JobDataLoggerJob中打印不出来，原因需要调查一下
                .withIdentity("job1", "group").build();
        scheduler.scheduleJob(jobDetail, simpleTrigger);

        System.out.println("scheduled - let's check");
    }

}
