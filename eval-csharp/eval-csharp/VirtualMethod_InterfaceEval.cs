using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;

namespace eval_csharp
{

    /**
     * 
     * 本evalation主要讲解继承接口后的方法覆盖行为。类的继承也是一样的(VritualMethod_ClassEval.cs)
     * 
     * 
     * 这个类的方法表中包含
     * 1. Object的所有virtual方法和非virtual方法
     * 2. IDisposable的所有方法，当然这里只有一个方法：Dispose
     * 3. SimpleType引入的新方法Dispose
     * 注意：2和3两个entry指向了同一个实现，就是SimpleType::Dispose()
     */
        internal sealed class SimpleType : IDisposable
    {
        public void Dispose()
        {
            Console.WriteLine("Dispose");
        }
    }

    //https://stackoverflow.com/questions/4470446/why-virtual-is-allowed-while-implementing-the-interface-methods
    public interface ICanSpeak 
    {
        //virtual by default
        String Speak();

        //virtual by default
        String DoubleSpeak();

    }

    /**
     * 这是一个继承接口的例子
     * 
     */
    public class Duck : ICanSpeak
    {
        public string Speak()
        {
            return "Quack";
        }

        public virtual  string DoubleSpeak()
        {
            return "QuackQuack";
        }
    }


    internal class YellowDuck : Duck, ICanSpeak
    {
        public new string Speak()
        {
            return "YellowDuck-Quack";
        }

        //https://stackoverflow.com/questions/4470446/why-virtual-is-allowed-while-implementing-the-interface-methods
        //这个文章中问为啥这里再加上virtual不行。可以加的，不过override也是要用的
        //我肯定是没明白他到底什么意思
        public override string DoubleSpeak()
        {
            return "YellowDuck-QuackQuack";
        }
    }

    
    /**
     * 关于继承可以参考
     * - CLR&C# 4th CN P264中关于SimpleType类的方法表
     * - C++面向对象模型中（houjie）P157有个单一继承的方法表
     * 
     * 综合描述一下就是编译时候就形成了针对每个类的方法表，执行时候只要查表就好了
     * 所以关键是如何理解这张表
     * 
     */
    class VirtualMethod_InterfaceEval
    {

        /**
         * Duck已经声明好了（方法没有定义为virtual）Speak方法（别管DoubleSpeak方法，那是我测试另外东西用的），但是我想在YellowDuck中override这个调用
         * 方法1：更改Duck中的方法，都加上virtual描述. 这样使用Duck Object（指向YellowDuck）直接就执行YellowDuck中的方法
         * 方法2：不更改Duck，YellowDuck单独实现ICanSpeak接口，不过调用时候需要记得cast为((ICanSpeak)duck).Speak()
         *        - 这种方式非常容易忘记，容易出问题
         *        - 不过如果代码中以接口调用为主的话，出问题的概率大大降低。
         *            - 譬如这是一个IDisposible接口，则关闭的时候直接都是用这个接口作为方法接口，就没啥问题了。
         *            - 我在下面例子中也增加了一个调用ICanSpeak接口的方法： private String speak(ICanSpeak x)。
         * 本例子中展示了方法2
         */
        [Test]
        public void testInterfaceInheritance() {

            Duck duck = new YellowDuck();
            Assert.AreEqual("Quack", duck.Speak()); //这里执行的是Duck，而非YellowDuck，多么容易出错的地方！
            Assert.AreEqual("YellowDuck-Quack", ((ICanSpeak)duck).Speak()); //需要先cast成ICanSpeak才能调用到YellowDuck，很容易犯错误的地方啊
            Assert.AreEqual("YellowDuck-Quack", speak(duck)); //这里是都基于接口编程，就不怎么出问题了
        }

        private String speak(ICanSpeak x) {
            return x.Speak();
        }


    }


}
