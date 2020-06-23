package baoying.eval.jvm.btrace;

public class Tmp {
    public static void main(String[] args) throws Exception{

        int total = 99;
        for(int i=0; i<total; i++){
            new Thread(()-> {
                System.out.println("run a thread" );
                M1M2Class x = new M1M2Class();
                x.m1();
                x.m2();
                x.m3();

            })
                    .start();

            Thread.sleep(3*1000);

        }

    }

    static class M1M2Class{

        public void m1(){

        }

        public void m2(){

        }

        public void m3(){

        }

    }
}
