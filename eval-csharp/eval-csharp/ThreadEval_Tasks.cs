using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using NUnit.Framework;

namespace eval_csharp
{

    /**
     * C#引入了Tasks去做更精妙的控制
     * 因为ThreadPool.QueueUserWorkItem可以提交一次任务,但是其有一些限制，如没有内建的机制知道什么时候完成
     * 
     */
    class ThreadEval_Tasks
    {
        [Test]
        public void testTasks_quick1()
        {
            //下面3中方式是类似的
            //有些不同的是如何把变量值穿如到delegate/callback中。QueueUserWorkItem多了一个state传变量，但是好像也没啥大用处
            ThreadPool.QueueUserWorkItem(state => int32add_1((Int32)state), 999); //5 as argument of the callback. state=5 here
            new Task(()=>int32add_1(999)).Start();
            Task.Run(() => int32add_1(999));


            //下面可以认为是第4总方式，增加了泛型
            //Task<Int32> 表示调用方法的范围值类型为Int32，所以int32add_1的返回类型为Int32
            //n: 传入的参数，其值就是999
            Task<Int32> t = new Task<Int32>(n => int32add_1((Int32)n), 999);
            //启动任务
            t.Start();

            //等待任务完成,还可以有等待时间，以及一些CancellationToken（干啥的?后续可能有介绍）
            t.Wait();

            //如果task执行的时候有未处理的异常，获取Result时候会抛出System.AggregateException
            Assert.AreEqual(1000, t.Result);

            //TODO 如何注册这个？
            //TaskScheduler.UnobservedTaskException();
        }

        private Int32 int32add_1(Int32 n1) {
            return n1 + 1;
        }


        /**
         * 
         * 取消任务，还是需要CancellationTokenSource，而且被调用方法要检查它，与QueueUserWorkItem一样
         * 
         * 
         */
        [Test]
        public void testTasks_cancel()
        {
            CancellationTokenSource cts = new CancellationTokenSource();
            Task<Int32> t = new Task<Int32>(n => testTasks_cancel_callback(cts.Token, (Int32)n, 2), 999);
            t.Start();
            cts.Cancel();

            //如果task执行的时候有未处理的异常，获取Result时候会抛出System.AggregateException
            //这里，我们cts.Cancel时候，触发了call中的异常(by token.ThroIfCancelationRequested())
            //这个Assert方式学习自 https://stackoverflow.com/questions/3407765/nunit-expected-exceptions
            //  - 还有一个方式在这个Test方法上面增加[ExpectedException("System.AggregateException")]
            Assert.Throws<AggregateException>(() => Console.WriteLine(t.Result));

        }

        /**
         * 注意：这个callback中不要使用token.IsCancellationRequested去判断是否继续执行是不对的
         * - 因为调用方法无法知道其是正常执行完成,还是被cancel的
         * - 要用token.ThrowIfCancellationRequested()， 主动抛出异常。这样外面通过t.Result获取结果的时候就知道没执行完，这个结果不能使用
         * 
         */
        public Int32 testTasks_cancel_callback(CancellationToken token, Int32 n1, Int32 anythingElseWeCanDefine)
        {
            Int32 counter = 0;
            //while ((!token.IsCancellationRequested) && (counter < n1)) wrong，见上面注释
            while (counter < n1)
            {
                token.ThrowIfCancellationRequested();
                counter++;
                Thread.Sleep(1000);
            }

            return 1;

        }

        /**
         * 超级实用的功能：continue，在各种不同的情况下continue调用下一个task
         * - 譬如成功则调用这一个continue，cancel则调用另一个continue等（下面有例子）
         * - 这有点类似于reactive编程的意思了
         * 
         */
        [Test]
        public void testTasks_continue()
        {
            CancellationTokenSource cts = new CancellationTokenSource();
            Task<Int32> t = new Task<Int32>(n => (Int32)n + 1, 999);

            //成功调用这个. 另外，注意这里面的 task=>中的task是null，其本来是用于传入值的，但是我们没用（参考上面的999这个值，对于n）。
            var successT = t.ContinueWith<String>(task =>  "good, success with result:" + t.Result  , TaskContinuationOptions.OnlyOnRanToCompletion);
            //失败调用这个 
            var faultT = t.ContinueWith(task => "bad, fault with :" + t.Exception.InnerException, TaskContinuationOptions.OnlyOnFaulted);
            //Cancel调用这个
            var cancellT = t.ContinueWith(task =>"ok, cancelled", TaskContinuationOptions.OnlyOnCanceled); 

            t.Start();
            //Assert.AreEqual("ok, cancelled", cancellT.Result);
            Assert.AreEqual("good, success with result:1000", successT.Result);

        }

        /**
         * TODO 未解决问题，什么时候调用cancel分支？
         */
        [Test]
        public void testTasks_continue_cancel_fault()
        {
            CancellationTokenSource cts = new CancellationTokenSource();
            Task<Int32> t = new Task<Int32>(n => testTasks_cancel_callback(cts.Token, (Int32)n, 2), 999);

            //成功调用这个 
            var successT = t.ContinueWith<String>(task => {
                return "good, success with result:" + t.Result;
            }, TaskContinuationOptions.OnlyOnRanToCompletion);

            //失败调用这个 
            var faultT = t.ContinueWith(task => {
                return "bad, fault with :" + t.Exception.InnerException;
            }, TaskContinuationOptions.OnlyOnFaulted);

            //Cancel调用这个 <= 我cancel了，但是调用的是上面的Fault的那个，因为抛出了未处理异常
            var cancellT = t.ContinueWith(task => {
                return "ok, cancelled";
            }, TaskContinuationOptions.OnlyOnCanceled);

            t.Start();
            cts.Cancel();
            //Assert.AreEqual("ok, cancelled", cancellT.Result);
            Assert.IsTrue(faultT.Result.StartsWith("bad, fault with :"));

        }
    }


}
