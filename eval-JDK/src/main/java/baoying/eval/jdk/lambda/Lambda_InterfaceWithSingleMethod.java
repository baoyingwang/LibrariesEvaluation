package baoying.eval.jdk.lambda;

/**
 * 简单interface（只有一个方法的），可以用lambda更简单的定义出来
 * - 无参数，如Runnable
 * - 有参数，自定义了一个 int doIt(int n)
 * - note: 代码确实简单了一些，无他
 */
public class Lambda_InterfaceWithSingleMethod {

    public void singleMethodNoArgument() {

        //1 - 使用 ()->{ } 替代只有只有一个方法/且参数为空的interface（e.g. Runnable)
        Runnable r = () -> {
            System.out.println("this is a line in Runnable::run()");
        };
        Thread t1 = new Thread(r);

        //1.1 - 这样也行，剩了一行
        Thread t2 = new Thread(() -> {
            System.out.println("this is a line in Runnable::run()");
        });

        //1.2 - 如果只有一行代码，括号{}可以去掉了
        Thread t3 = new Thread(() -> System.out.println("this is a line in Runnable::run()"));
        Runnable r2 = () -> System.out.println("this is a line in Runnable::run()");

    }

    interface ISingleArgument {
        int doIt(int n);
    }

    public void singleMethodSingleArgument() {

        //这里可以直接写n, 或者 (int n) -> {}
        ISingleArgument x = n -> {
            System.out.println(n);
            return 1;
        };

        ISingleArgument x2 = n -> {
            int r = anotherDoIt(n);
            return r;
        };

        //注意这个使用，不需要显示的return，就能return值（已经验证）
        ISingleArgument x3 = n -> anotherDoIt(n);
        System.out.println("result:"+x3.doIt(7));
    }

    private int anotherDoIt(int n){
        System.out.println(n);
        return n+1;
    }

    public static void main(String[] args){

        Lambda_InterfaceWithSingleMethod x = new Lambda_InterfaceWithSingleMethod();
        x.singleMethodSingleArgument();

    }


}
