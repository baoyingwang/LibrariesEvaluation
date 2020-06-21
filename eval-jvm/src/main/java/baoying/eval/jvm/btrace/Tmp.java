package baoying.eval.jvm.btrace;

public class Tmp {
    public static void main(String[] args) throws Exception{

        int total = 99;
        for(int i=0; i<total; i++){
            new Thread(()-> System.out.println("run a thread" ))
                    .start();

            Thread.sleep(3*1000);

        }

    }
}
