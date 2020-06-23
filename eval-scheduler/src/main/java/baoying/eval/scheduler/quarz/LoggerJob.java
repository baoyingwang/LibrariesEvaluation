package baoying.eval.scheduler.quarz;


import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;


public class LoggerJob implements Job{

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Trigger trigger = context.getTrigger();
        Date startTime = trigger.getStartTime();
        Date endTime = trigger.getEndTime();
        Date previousFireTime = trigger.getPreviousFireTime();
        Date nextFireTime = trigger.getNextFireTime();

        System.out.println("trigger group/name:" + trigger.getKey().getName()+"/" + trigger.getKey().getName());
        System.out.println("job     group/name:" + trigger.getJobKey().getGroup()+ "/" + trigger.getJobKey().getName());
        printTime("startTime       ",startTime);
        printTime("endTime         ",endTime);
        printTime("previousFireTime",previousFireTime);
        printTime("nextFireTime    ",nextFireTime);

        System.out.println("job data size:"+trigger.getJobDataMap().size());
        System.out.println(("job data as below:"));
        trigger.getJobDataMap().forEach((k, v)->{
            System.out.println("k:" + k + ", v:" + v);
        });

        System.out.println("*****************************");
    }

    private void printTime(String name, Date time){
        System.out.println(name +":"+ (time==null?"null":time.toString()));
    }
}

