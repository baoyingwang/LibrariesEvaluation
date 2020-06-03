package baoying.eval.jdk.thread;


/**
 * https://alvinalexander.com/java/java-8-lambda-thread-runnable-syntax-examples/
 * 注意：你应该尽量避免自己创建一个线程，而是使用线程池
 * 本例子只是作为lambda的一个例子吧
 */
public class SimplifiedNewThreadWithLambda {

    public void newThread1(){

        //1 - 使用 ()->{ } 替代只有只有一个方法/且参数为空的interface（e.g. Runnable)
        Runnable r = ()->{
          System.out.println("this is a line in Runnable::run()");
        };
        Thread t1 = new Thread(r);

        //1.1 - 这样也行，剩了一行
        Thread t2 = new Thread(()->{
            System.out.println("this is a line in Runnable::run()");
        });

        //1.2 - 如果只有一行代码，括号{}可以去掉了
        Thread t3 = new Thread(()-> System.out.println("this is a line in Runnable::run()"));
    }
}
