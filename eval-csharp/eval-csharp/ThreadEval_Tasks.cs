using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using NUnit.Framework;

namespace eval_csharp
{

    /**
     * 
     * Task线程模型：
     * - 调用者添加完task，后直接返回，并不等待Task执行完成
     *   - 可以通过task.Wait()等待线程完成
     *   - 可以通过await/async，使得更外部调用者直接得到task handler（并决定如何继续）
     *     - 注意await之后的线程与之前的线程大概率不同
     *   - 通过task.Result获取结果的话，也会等待
     * 
     * - 如果task的callback执行中出现了exception（包括cancel时候触发的），在获取Result时候会抛出
     *   - 再添加task（或者task start）时候不会出现exception，因为抛出exception的地方是另一个线程（不是caller线程）
     *   - task.Result时候可以抛出线程，是因为Result中保存了结果状态     *   
     *   
     * C#引入了Tasks去做更精妙的控制
     * - 与Task相比：ThreadPool.QueueUserWorkItem可以提交一次任务,但是其有一些限制，如没有内建的机制知道什么时候完成
     * 
     * //TODO 如何注册这个？TaskScheduler.UnobservedTaskException();
     */
    class ThreadEval_Tasks
    {
        [Test]
        public void testTasks_quick1()
        {
            //下面3中方式是类似的
            //有些不同的是如何把变量值穿如到delegate/callback中。QueueUserWorkItem多了一个state传变量，但是好像也没啥大用处
            //#1
            ThreadPool.QueueUserWorkItem(state => int32add_1((Int32)state), 999); //QueueUserWorkItem中第一个参数为callback/delegate，接受第二个参数/999作为输入
            ThreadPool.QueueUserWorkItem(state => int32add_1(999));
            //#2
            new Task(()=>int32add_1(999)).Start();
            //#3
            Task.Run(() => int32add_1(999));


            //下面可以认为是第4种方式，增加了泛型
            //Task<Int32> 表示调用方法的范围值类型为Int32，所以int32add_1的返回类型为Int32
            //n: 传入的参数，其值就是999
            Task<Int32> t = new Task<Int32>(n => int32add_1((Int32)n), 999);
            Task<Int32> t2 = new Task<Int32>(() => int32add_1(999));
            //启动任务
            t.Start();

            //等待任务完成,还可以有等待时间，以及一些CancellationToken（干啥的?后续可能有介绍）
            //如果不等待，直接试图获取结果呢？ 试了一下，也是可以获取解雇哦的。
            t.Wait();

            //Task执行中的异常不会在Caller中有体现，直到调用t.Result的时候。
            //- 如果task执行的时候有未处理的异常，获取Result时候会抛出System.AggregateException
            //- 也就是说，不调用t.Result（或者Continue with Fault），caller不知道发生了exception
            Assert.AreEqual(1000, t.Result);

        }

        private Int32 int32add_1(Int32 n1) {

            Thread.Sleep(1000); //simulate a long task
            return n1 + 1;
        }


        [Test]
        public void testTasks_quick2()
        {
            //下面3中方式是类似的
            //有些不同的是如何把变量值穿如到delegate/callback中。QueueUserWorkItem多了一个state传变量，但是好像也没啥大用处
            //#1
            ThreadPool.QueueUserWorkItem(state=>sum_multiply(1,2,3));
            ThreadPool.QueueUserWorkItem(state => {
                (int a, int b, int c) = ((int a, int b, int c))state;
                sum_multiply(a, b, c);
                }, (1,2,3));
            //#2
            new Task<(Int32 mulitply, Int32 sum)>(() => sum_multiply(1, 2, 3)).Start();
            //#3
            Task.Run(() => sum_multiply(1, 2, 3));


            //下面可以认为是第4种方式，增加了泛型
            //https://stackoverflow.com/questions/18029881/how-to-pass-multiple-parameter-in-task
            var t = new Task<(Int32 multiply, Int32 sum)>(()=>sum_multiply(1,2,3));
            t.Start(); //启动任务

            //等待任务完成,还可以有等待时间，以及一些CancellationToken（干啥的?后续可能有介绍）
            //如果不等待，直接试图获取结果呢？ 试了一下，也是可以获取解雇哦的。
            t.Wait();

            //如果task执行的时候有未处理的异常，获取Result时候会抛出System.AggregateException
            Assert.AreEqual((6, 6), t.Result);

            //TODO 如何注册这个？
            //TaskScheduler.UnobservedTaskException();
        }
        private (Int32, Int32) sum_multiply(Int32 a, Int32 b, Int32 c)
        {

            Thread.Sleep(1000); //simulate a long task
            return (a+b+c, a*b*c);
        }

        /**
         * 
         * 取消任务，还是需要CancellationTokenSource
         * - 一般来说被调用方法是一个循环，每次循环都检查CancellationTokenSource状态。
         *   - 注意：被调用方法检查的时候如果有取消直接抛出异常token.ThroIfCancelationRequested()，而不是直接返回
         *           抛出异常的效果是为了区分正常返回（执行完毕）和被cancel完成（通过该exception）
         * - 这个机制与ThreadPool.QueueUserWorkItem中取消任务是一样的
         *   - 区别在与Task中用exception，而ThreadPool.QueueUserWorkItem中直接返回就行了
         *   - 反正ThreadPool.QueueUserWorkItem中也没人检查返回值
         * 
         * 
         */
        [Test]
        public void testTasks_cancel()
        {
            CancellationTokenSource cts = new CancellationTokenSource();
            //下面两种都行，t2的更简单
            var t1 = new Task<Int32>(n => testTasks_cancel_callback(cts.Token, (Int32)n, 2), 999);//n=999
            var t2 = new Task<Int32>(() => testTasks_cancel_callback(cts.Token, 999, 2));
            var t  = t2;
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
            //continue的task无需调用Start，直接开始
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
            //这里证明不加任何option的话，其将继续执行
            //不过，如果内部出现exception的话，在获取Result时候会抛出exception
            CancellationTokenSource cts = new CancellationTokenSource();
            //调用cts.Cancel将导致内部抛出Exception（因为内部使用了token.ThrowIfCancellationRequested()）
            Task<Int32> t = new Task<Int32>(n => testTasks_cancel_callback(cts.Token, (Int32)n, 2), 999);
            var anyT = t.ContinueWith<String>(task => "continue with any result:"); //这里故意没有加上t.Result,因为其这里调用会抛出异常
            t.Start();
            cts.Cancel();
            Assert.IsTrue(anyT.Result.StartsWith("continue with any result:"));


            //这一段证明不同的情况下的不同调用
            cts = new CancellationTokenSource();
            t = new Task<Int32>(n => testTasks_cancel_callback(cts.Token, (Int32)n, 2), 999);
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

        /**
         * 为啥要用Task.FromResult，就是为了让代码更一致一点；有时候是为了构建测试接口
         * https://stackoverflow.com/questions/19568280/what-is-the-use-for-task-fromresulttresult-in-c-sharp
         * 
         */
        [Test]
        public void testTaskFromResult() {

            //全部都是在同步调用，只不过可以融入到async调用的moshi
            //2020.07.17 22:11:27:820 forgedTaskFromResultAsync - before await in thread 13 and no task
            //2020.07.17 22:11:27:822 longRunTask in thread 13 and no task
            //2020.07.17 22:11:30:825 forgedTaskFromResultAsync - after await in thread 13 and no task
            //2020.07.17 22:11:30:825 task got in thread 13 and no task
            //2020.07.17 22:11:30:825 task result got in thread 13 and no task

            var task = forgedTaskFromResultAsync();
            ThreadEval_Util.TraceThreadAndTask("task got");

            var result = task.Result;
            ThreadEval_Util.TraceThreadAndTask("task result got");
        }

        //尝试过去掉async/await，结果没啥两样。加上他们是为了让代码看起来更一致
        private async Task<String> forgedTaskFromResultAsync() {
            ThreadEval_Util.TraceThreadAndTask("forgedTaskFromResultAsync - before await");
            var result =  await Task.FromResult(longRunTask());
            ThreadEval_Util.TraceThreadAndTask("forgedTaskFromResultAsync - after await");
            return result;
        }

        private String longRunTask() {
            ThreadEval_Util.TraceThreadAndTask("longRunTask");
            Task.Delay(3000).Wait();
            return "abc";
        }

    }


}
