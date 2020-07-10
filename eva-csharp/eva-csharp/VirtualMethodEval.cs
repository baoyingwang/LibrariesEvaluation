using System;
namespace eva_csharp
{

    class Parent {

        public void normal_1() {
            Console.WriteLine("Parent - normal_1");
        }

        /**
         * 
         */
        public virtual void normal_2_virtual()
        {
            Console.WriteLine("Parent - normal_2_virtual");
        }

        public virtual void v_normal()
        {
            Console.WriteLine("Parent - v_normal");
        }

        public void v_normal_call_virtual()
        {
            //注意，这里调用的是Parent虚方法，所以真正调用的时候，如果child override了它，一定会使用child的实现
            this.v_normal();
        }

        public void v_normal_call_virtual_2()
        {
            //注意，这里调用的是Parent虚方法，所以真正调用的时候，如果child override了它，一定会使用child的实现
            this.normal_2_virtual();
        }
    }

    class Child : Parent{

        /**
         * 这里考察的是new参数
         */
        public new void normal_1()
        {
            Console.WriteLine("Child - new normal_1");
        }

        /**
         * 
         * 虽然Parent是virtual，但是Child中使用了new，则无法完成虚调用
         * 即：Parent p1 = new Child()中，p1.normal_2_virtual()调用parent的实现（因为Child中的new），而不是Child的实现。
         * 
         * 换句话说：这里考察的是还是new参数：只要new出现，即使parent里面定义为virtual，也不会出现虚拟继承关系。
         * BTW：这里的virtual是作用于Child的子类的，对于当前Parent/Child类的调用没有什么影响。
         * 
         */
        public new virtual void normal_2_virtual()
        {
            Console.WriteLine("Child - new virtual void normal_2_virtual");
        }

        /**
         * 
         * Child通过override覆盖了parent方法，所以即使是Parent p1 = new Child() 也会用这个方法
         * 
         */
        public override void v_normal()
        {
            Console.WriteLine("Child - v_normal");
        }
    }
   
    public class VirtualMethodEval
    {
        public VirtualMethodEval()
        {
        }

        public void eval() {

            Console.WriteLine("== begin p1");
            Parent p1 = new Child();
            p1.normal_1();         //调用父类的方法，因为父类方法不是virtual的，这是与java完全不同的地方。java默认全部使用child的方法（没有virtual这类的修饰符）。
            p1.normal_2_virtual(); //调用父类的方法，虽然父类方法是virtual，单child中用的new，所以Parent与Child之间完全没有
            p1.v_normal(); //调用子类的方法，因为该方法是virtual的
            p1.v_normal_call_virtual(); //这个父类函数内部调用到了virtual(Parent)+override(Child)函数，所以函数内部最终调用为子类
            p1.v_normal_call_virtual_2();//这个父类函数内部调用到了virtual(Parent)+new(Child)函数，所以函数内部最终调用父类方法

            Console.WriteLine("== begin c1");
            Child c1 = new Child();
            c1.normal_1();
            c1.normal_2_virtual();
            c1.v_normal();
            c1.v_normal_call_virtual();


        }
    }
}
