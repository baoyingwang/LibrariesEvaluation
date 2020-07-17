using System;
using System.Collections.Generic;
using NUnit.Framework;


namespace eval_csharp
{
    /**
     * 
     * delegate就是回调，把方法当成参数传来传去。和Java8中增加的lambda/Function类似。
     * - delegate可以作为一个普通的变量声明，譬如
     *   - 声明一个delegate类型
     *     - e.g. delegate void Feedback(Int32 value); 这表示一个方法的输入为Int32，输出为void
     *   - 这个类型可以作为一个方法的输入参数
     *     - e.g void Counter(Int32 from, Int32 to, Feedback fb)
     *   - 然后可以创建一个delegate的实例
     *     - e.g. Feedback fb1 = new Feedback(FeedbackToConsole1); 
     *     - e.g. Feedback fb2 = FeedbackToConsole1
     *     - e.g. 这有一个有意思的例子：delegate String GetString(); GetString x = 40.ToString; Console.WriteLine($"val:{x()}");
     *     - 这个实例就是通过fb1(x)来调用真正的背后函数FeedbackToConsole1这个方法
     *   
     * - Action<T> 是没有返回值的委托， 譬如Action<Int32>
     * - Func<T> 是类似的委托，不过返回类型，最后一个类型就是返回的类型,譬如Function<Int32, Int32, String) 表示 两个Int32为参数，String为返回值的委托
     *
     * - 两个delegate相加，则其就形成了一个chain
     *   - 如 fbChain = fb1 + fb2
     *   - fbChain的类型与fb1和fb2一样，不过表示要调用两次
     *   - 可以通过减法去除
     *     - 多次添加则需要多次减法，去删除/恢复。
     *   - 添加两个相同的实例，则调用其两次
     *   - 下面有case去验证这些操作
     * 
     * 
     */
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


        internal delegate void Feedback(Int32 value);

        [Test]
        public void SimpleDelegation()
        {

            //声明一个delegate变量，用于后续Counter调用
            Feedback fb1 = new Feedback(FeedbackToConsole1);
            Feedback fb2 = FeedbackToConsole1; //Counter(1, 2, FeedbackToConsole1); 这样语法也可以，更简单
            
            //Counter的第三个参数为一个delegate方法
            Counter(1, 2, fb1);
            

            Assert.AreEqual(2, callTrace.Count);
            Assert.AreEqual("Console 1 - 1", callTrace[0]);
            Assert.AreEqual("Console 1 - 2", callTrace[1]);
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
