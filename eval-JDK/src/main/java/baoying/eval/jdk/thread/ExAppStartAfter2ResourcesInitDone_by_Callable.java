package baoying.eval.jdk.thread;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 系统启动的时候，需要两个Resource init完成才能继续
 * 这代码也太复杂了，参考countdownlatch那个，简单很多
 *
 */
public class ExAppStartAfter2ResourcesInitDone_by_Callable {

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

            List<Resource> resources = new ArrayList<>();
            resources.add(new Resource("r1"));
            resources.add(new Resource("r2"));

            List<Future<Boolean>> tasksFutures = new ArrayList<>();
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            for(Resource r: resources){
                Callable<Boolean> task = new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return r.init();
                    }
                };

                Future<Boolean> future = executorService.submit(task);
                tasksFutures.add(future);

            }

            Instant initStartTime = Instant.now();
            Duration maxInitTime = Duration.ofSeconds(30);
            while(true){
                boolean allSuccess = true;
                for(Future<Boolean> f : tasksFutures){
                    try{
                        boolean itSuccess = f.get(3, TimeUnit.SECONDS);
                        if(!itSuccess){
                            allSuccess = false;
                        }
                    }catch (Exception e){

                    }
                }

                if(allSuccess){
                    System.out.println("all init done");
                    break;
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
