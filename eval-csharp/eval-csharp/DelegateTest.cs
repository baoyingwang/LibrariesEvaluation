using System;
using System.Collections.Generic;
using NUnit.Framework;

/**
 * 第一个c#测试类，做了一些minor但是有趣的配置
 * 1. 使用的nunit，使用nunit是因为我参考的quickfixn用的它。
 *    note： 微软还有一个自己默认的测试框架
 * 
 * 问题：增加nunit的依赖（nunit and nunit test adapter)。但是增加依赖以后，在TestExplorer中点击运行测试也不运行，也不报错
 *      btw：在mac的visual studio中，找不到怎么把Test　Explorer窗口弄出来
 * 过程：网上搜索一圈，没有找到针对性的文章。大体上就是两种，
 * 1）添加测试代码，在test explorer中运行即可
 * 2）创建一个测试project，然后再test explorer中运行即可
 *    我不想创建一个新的测试project，因为我这个本身就是用于测试的，可能可以调整这个项目的设置。而且，我觉得不应该这么复杂。
 * 解决：在增加依赖那里，把我看到的MS的测试依赖都加上了，Microsfot.NET.Test.SDK, Microsoft.TestPlatform, MSTest.TestAdapter
 *      然后TestExplorer中就可以运行测试了。
 *      
 * btw：测试运行的控制台找不到，想看一下Console.WriteLine(..)也不行，也好，死心了只用Assert。但是有时候Console的一些输出有帮助
 *      可能需要配置一些东西，或者用什么秘籍吧
 *      参考：https://stackoverflow.com/questions/11209639/can-i-write-into-console-in-a-unit-test-if-yes-why-doesnt-the-console-window
 *
 * 

 */
namespace eval_csharp
{
    //这是带测试的delegation
    internal delegate void Feedback(Int32 value);

    //[TestFixture]
    public class DelegateTest
    {

        
        List<String> callTrace; 
        [SetUp]
        public void SetUp()
        {
            callTrace = new List<String>();
        }

        [TearDown]
        public void TearDown()
        {
            callTrace.Clear();

        }

        private void Counter(Int32 from, Int32 to, Feedback fb) {
            for (Int32 val = from; val <= to; val++) {
                //与fb?(val)一样
                if (fb != null) {
                    fb(val);
                }
            }
        }

        /**
         * delete就是回调，把方法当成参数传来传去。和Java8中增加的lambda/Function类似。
         * note：这就增加了一些复杂度，因为形成了这么一个chain之后，就涉及到增加重复以及如何删除等。下面增加额外的case来处验证它
         */
        [Test]
        public void SimpleDelegation()
        {
            Feedback fb1 = new Feedback(FeedbackToConsole1);
            Counter(1, 2, fb1);
            //Counter(1, 2, FeedbackToConsole1); 这样语法也可以，更简单

            Assert.AreEqual(2, callTrace.Count);
            Assert.AreEqual("Console 1 - 1", callTrace[0]);
            Assert.AreEqual("Console 2 - 1", callTrace[0]);
        }

        /**
         * delegate支持chain
         * 就是在调用这个delegate的时候，不是调用一个callback，而是把这一串全都调用一遍
         * 
         */
        [Test]
        public void SimpleDelegationChain()
        {
            Feedback fb1 = new Feedback(FeedbackToConsole1);
            Feedback fb2 = new Feedback(FeedbackToConsole2);
            Feedback fb3 = new Feedback(FeedbackToConsole3);

            Feedback fbChain = null;
            fbChain += fb1;
            fbChain += fb2;
            fbChain += fb3;

            Counter(1, 2, fbChain);

            Assert.AreEqual(6, callTrace.Count);

            //下面顺序说明，delegate chain在调用的时候，是每次把chain全调用一边
            //譬如Counter例子中，每次执行fb（val），则会把fb相关这条链上的1/2/3delegate全调用一边
            Assert.AreEqual("Console 1 - 1", callTrace[0]);
            Assert.AreEqual("Console 2 - 1", callTrace[1]);
            Assert.AreEqual("Console 3 - 1", callTrace[2]);

            Assert.AreEqual("Console 1 - 2", callTrace[3]);
            Assert.AreEqual("Console 2 - 2", callTrace[4]);
            Assert.AreEqual("Console 3 - 2", callTrace[5]);
        }

        
        /**
         * 重复添加同一个reference到delegate chain中的效果
         * 
         */
        [Test]
        public void AddDuplicateDelegates()
        {
   
            Feedback fb1 = new Feedback(FeedbackToConsole1);
            Feedback fb2 = new Feedback(FeedbackToConsole2);
            Feedback fb3 = new Feedback(FeedbackToConsole3);

            Feedback fbChain = null;
            fbChain += fb1; //也可以(Feedback)Delegate.Combine(fbChain, fb1)
            fbChain += fb2;
            fbChain += fb3;
            fbChain += fb1; //fb1又添加了一次，什么影响，见下面的检查结果。相当于第4个feedback到chain/列表中

            Counter(1, 2, fbChain);

            //根据下面的测试结果，fb1被添加到chain的末端了。
            //就是说添加两次相同的callback，就调用两次，其并不检查重复
            Assert.AreEqual(8, callTrace.Count);

            Assert.AreEqual("Console 1 - 1", callTrace[0]);
            Assert.AreEqual("Console 2 - 1", callTrace[1]);
            Assert.AreEqual("Console 3 - 1", callTrace[2]);
            Assert.AreEqual("Console 1 - 1", callTrace[3]);

            Assert.AreEqual("Console 1 - 2", callTrace[4]);
            Assert.AreEqual("Console 2 - 2", callTrace[5]);
            Assert.AreEqual("Console 3 - 2", callTrace[6]);
            Assert.AreEqual("Console 1 - 2", callTrace[7]);

        }

        [Test]
        public void DelDelegateFromChain()
        {
            /* 
             * delegate chain的添加删除重复节点有点坑啊，见我下面的测试case和总结
             * - 添加同一个delegate两次将调用两次，这个ok。这说明内部不去重复（也没有hashcode/equal之类的实现，可能也不好去重）
             * - 删除的时候
             *   - 可以使用原引用去删除（这个ok
             *   - 也可以new一个去删除相同名称的delegate，
             *   - 添加多次，则需要删除多次
             */
            Feedback fb1 = new Feedback(FeedbackToConsole1);
            Feedback fb2 = new Feedback(FeedbackToConsole2);
            Feedback fb3 = new Feedback(FeedbackToConsole3);

            Feedback fbChain = null;
            fbChain += fb1;
            fbChain += fb1;
            fbChain += fb1;
            fbChain += fb2;
            fbChain += fb3;
            fbChain -= fb1; //这个可以成功的把fb1删掉，但是因为添加了3次，所以要删除3次才行
            fbChain -= fb1; //这个可以成功的把fb1删掉
            fbChain -= new Feedback(FeedbackToConsole1); //这个可以成功的把fb1删掉
            fbChain -= new Feedback(FeedbackToConsole2);//这个可以也成功的把fb2删掉， 这与上面添加时候的语义不太一致，坑！
            Counter(1, 2, fbChain);

            Assert.AreEqual(2, callTrace.Count);
            Assert.AreEqual("Console 3 - 1", callTrace[0]);
            Assert.AreEqual("Console 3 - 2", callTrace[1]);

        }


        private void FeedbackToConsole1(Int32 value) {
            callTrace.Add("Console 1 - " +value);
        }

        private void FeedbackToConsole2(Int32 value)
        {
            callTrace.Add("Console 2 - " + value);
        }

        private void FeedbackToConsole3(Int32 value)
        {
            callTrace.Add("Console 3 - " + value);
        }
    }

    
}
