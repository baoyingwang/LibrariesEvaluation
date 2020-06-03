package baoying.eval.jdk.thread;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 系统启动的时候，需要两个Resource init完成才能继续
 * 使用CountdownLatch等待resource完成
 */
public class ExAppStartAfter2ResourcesInitDone_by_CountdownLatch {

    //别在这里加latch，其不需要知道latch
    //外围调用这知道latch就足够了（见submit）
    static class Resource{
        String name;
        Resource(String name){
            this.name = name;
        }

        volatile boolean initSuccess;
        public boolean init(){
            try{
                TimeUnit.SECONDS.sleep(5);
            }catch (Exception e){
                e.printStackTrace();
                initSuccess = false;
                return initSuccess;
            }

            this.initSuccess = true;
            return this.initSuccess;
        }

        boolean isInitSuccess(){
            return this.initSuccess;
        }
    }


    static class MainApp{

        public void start() throws Exception{
            CountDownLatch latch = new CountDownLatch(2);

            Resource[]  resources = new Resource[2];
            resources[0] = new Resource("r1");
            resources[1] = new Resource("r2");

            ExecutorService executorService = Executors.newFixedThreadPool(2);
            for(Resource r: resources){
                executorService.submit(()->{
                    boolean initSuccess = r.init();
                    if(initSuccess){
                        latch.countDown();
                    }else{
                        System.out.println("ERROR: fail to init - "+r.name);
                    }

                });
            }


            Instant initStartTime = Instant.now();
            Duration maxInitTime = Duration.ofSeconds(30);
            while(true){
                //WARN: 如果resource初始化的时候出现问题（其内部没有执行countdown）
                //这里将一直等待下去，并且打印log。排查问题时候可以通过这个来调查
                boolean allInitiated = latch.await(3, TimeUnit.SECONDS);
                if(allInitiated){
                    System.out.println(Instant.now()+" all init done");
                    break;
                }else {
                    System.out.println(Instant.now()+" still waiting");
                }

                Duration alreadyPassed = Duration.between(initStartTime, Instant.now());
                if(alreadyPassed.compareTo(maxInitTime) > 0){

                    System.err.println("ERROR: fail to init - cannot get init done status after waiting:"+maxInitTime.toString());
                    System.exit(-1);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        MainApp app = new MainApp();
        app.start();

    }
}
