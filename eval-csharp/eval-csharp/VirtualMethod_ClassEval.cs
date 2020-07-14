using System;
using NUnit.Framework;

namespace eval_csharp
{
   
    /**
     * 
     * 这里一定要以查表的思路去考虑问题。接口继承也是类似的（VirtualMethod_InterfaceEval）
     * 
     * 
     */
    public class VirtualMethod_ClassEval
    {
        public VirtualMethod_ClassEval()
        {
        }

        /**
         * 
         * CLR&C# CN v4 P151/152/153中讲述了这个
         * 
         */
        [Test]
        public void eval() {

            Console.WriteLine("== begin p1");
            Parent p1 = new Child();

            //注意，现在p1虽然声明为Parent，但是实际上为Child类型。Child类型的方法表在编译时候就确定了
            //Child的方法表如下：
            //- Object.*
            //- Parent.*
            //   - normal_1                 : 指向Parent::normal_1, 因为其自身声明为sealed/non-virtual
            //   - normal_2_virtual(virtual): 指向Parent::normal_2_virtual, 虽然Parent为virtual，但Child并没有override，而是new了一个新的。
            //   - v_normal(virtual)        : 指向Child::v_normal,因为child override了它
            //   - v_normal_call_virtual    : 指向Parent，与normal1一样. 内部调用了v_normal(virtual).调用时候继续查表（key为Parent,可见最后调用Child.v_normal)
            //   - v_normal_call_virtual_2  : 指向Parent，与normal1一样. 内部调用了normal_2_virtual(virtual).调用时候继续查表（key为Parent,可见最后调用Parent.normal_2_virtual)
            //   - nomal_no_inherit         : 指向Parent，child根本没有定义这个方法
            //- Child.*                     : 全部指向自己对应方法
            //   - normal_1(new)                  ：指向自己
            //   - normal_2_virtual(new virtual)  ：指向自己
            //   - v_normal(override) ： note： 这个影响了上面Parent.v_normal的指向（没有指向Parent，而是指向了Child)
            //
            //如何查表：以Parent p1 = new Child()为例
            //  - 本例子中因为根本上的类型为Child，所以查Child的方法表（即上表）
            //  - 查表的key，根据声明类型，这里为p1, 即查找Parent的条目，即Parent.*中的条目（而不是找Child对应的条目）
            //

            //下面全部查找Parent.*下面的项目
            Assert.AreEqual("Parent - normal_1"        , p1.normal_1());         //调用父类的方法，因为父类方法不是virtual的，这是与java完全不同的地方。java默认全部使用child的方法（没有virtual这类的修饰符）。
            Assert.AreEqual("Parent - normal_2_virtual", p1.normal_2_virtual()); //调用父类的方法，虽然父类方法是virtual，但child中用的new，所以Parent与Child之间完全没有
            Assert.AreEqual("Child - v_normal"         , p1.v_normal());         //调用子类的方法，因为该方法是virtua(Parent)+override(Child)
            Assert.AreEqual("Child - v_normal"         , p1.v_normal_call_virtual());  //这个方法自身not virtual on Parent/not defined on Child的;父类函数内部调用到了virtual(Parent)+override(Child)函数，所以函数内部最终调用***子类方法***
            Assert.AreEqual("Parent - normal_2_virtual", p1.v_normal_call_virtual_2());//这个方法自身not virtual on Parent/not defined on Child的;父类函数内部调用到了virtual(Parent)+new(Child)函数，所以函数内部最终调用***父类方法***
            Assert.AreEqual("Parent - no in herit"     , p1.nomal_no_inherit());

            //这里只是演示了一下void eval(Parent p1)
            //因为p1对应的真正类型为Child，去检查方法内部调用所查的表还是Child方法表（即使该方法参数定义的不是Child，而是Parent）
            this.eval(p1);

            Console.WriteLine("== begin c1");
            Child c1 = new Child();
            //下面查找Child方法表中的Child.*对应的条目
            Assert.AreEqual("Child - new normal_1"                     , c1.normal_1());        //Child中定义了这该方法，直接查表（key为Child）
            Assert.AreEqual("Child - new virtual void normal_2_virtual", c1.normal_2_virtual());//Child中定义了这该方法，直接查表（key为Child）
            Assert.AreEqual("Child - v_normal"                         , c1.v_normal());        //Child中定义了这该方法，直接查表（key为Child）
            Assert.AreEqual("Child - v_normal"    , c1.v_normal_call_virtual()); //Child中没有定义这3个方法，查表时候直接看Parent的
            Assert.AreEqual("Parent - no in herit", c1.nomal_no_inherit());      //Child中没有定义这3个方法，查表时候直接看Parent的

            //特别注意，这里虽然c1作为参数，但是还是查询查询Child表中的Parent条目(因为方法参数为Parent，即调用结果与this.eval(p1);是一样的）
            //WARN:与上面c1的直接调用结果是不同的
            this.eval(c1);
        }

        //特别注意，因为这里的参数是Parent类型（即使调用时候用的是this.eval(c1)）
        //即使你传进来的时候是Child类型，则只有Parent（virtual）+Child（override）才会调用child，其余全部parent
        private void eval(Parent p1) {

            //与上面eval()中的code block一模一样的，没有放在一起消除dup，是故意为之
            //因为我要考察不同的方法参数类型对行为的影响
            Assert.AreEqual("Parent - normal_1", p1.normal_1());         //调用父类的方法，因为父类方法不是virtual的，这是与java完全不同的地方。java默认全部使用child的方法（没有virtual这类的修饰符）。
            Assert.AreEqual("Parent - normal_2_virtual", p1.normal_2_virtual()); //调用父类的方法，虽然父类方法是virtual，但child中用的new，所以Parent与Child之间完全没有
            Assert.AreEqual("Child - v_normal", p1.v_normal());         //调用子类的方法，因为该方法是virtua(Parent)+override(Child)
            Assert.AreEqual("Child - v_normal", p1.v_normal_call_virtual());  //这个方法自身not virtual on Parent/not defined on Child的;父类函数内部调用到了virtual(Parent)+override(Child)函数，所以函数内部最终调用***子类方法***
            Assert.AreEqual("Parent - normal_2_virtual", p1.v_normal_call_virtual_2());//这个方法自身not virtual on Parent/not defined on Child的;父类函数内部调用到了virtual(Parent)+new(Child)函数，所以函数内部最终调用***父类方法***
            Assert.AreEqual("Parent - no in herit", p1.nomal_no_inherit());

        }
    }

    class Parent
    {

        public String normal_1()
        {
            return "Parent - normal_1";
        }

        /**
         * 
         */
        public virtual String normal_2_virtual()
        {
            return "Parent - normal_2_virtual";
        }

        public virtual String v_normal()
        {
            return "Parent - v_normal";
        }

        public String v_normal_call_virtual()
        {
            //注意，这里调用的是Parent虚方法，所以真正调用的时候，如果child override了它，一定会使用child的实现
            return this.v_normal();
        }

        public String v_normal_call_virtual_2()
        {
            //注意，这里调用的是Parent虚方法，所以真正调用的时候，如果child override了它，一定会使用child的实现
            return this.normal_2_virtual();
        }

        public String nomal_no_inherit() {
            return "Parent - no in herit";
        }
    }

    class Child : Parent
    {

        /**
         * 这里考察的是new参数
         */
        public new String normal_1()
        {
            return "Child - new normal_1";
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
        public new virtual String normal_2_virtual()
        {
           return "Child - new virtual void normal_2_virtual";
        }

        /**
         * 
         * Child通过override覆盖了parent方法，所以即使是Parent p1 = new Child() 也会用这个方法
         * 
         */
        public override String v_normal()
        {
           return "Child - v_normal";
        }
    }
}
