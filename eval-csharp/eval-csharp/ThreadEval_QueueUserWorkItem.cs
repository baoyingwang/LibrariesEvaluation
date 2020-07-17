using System;
using System.Diagnostics.CodeAnalysis;
using System.Threading;
using NUnit.Framework;

namespace eval_csharp
{
    /**
     * 
     * ThreadPool.QueueUserWorkItem可以提交一次任务
     * - 可以通过token来cancel操作（不过要求内部不断检查这个token）
     * - 但是其有一些限制，如没有内建的机制知道什么时候完成
     *   - 在下面的例子中，我使用了ManualResetEvent完成线程间的通信
     *   - C#引入了Tasks去做更精妙的控制，见ThreadEval_Tasks.cs
     * 
     */
    public class ThreadEval_QueueUserWorkItem
    {
        public ThreadEval_QueueUserWorkItem()
        {
        }

        class Tmp1
        {
            public String x1;
        }

        [Test]
        public void testBasicTaskExecution()
        {

            String x = null;
            Tmp1 tmp1 = new Tmp1();

            //等待一个线程完成后，当前线程继续执行的典型用法之一，通过ManualResetEvent的Set通知等待者可以继续执行
            //微软的文档是真给力，太清楚了，见https://docs.microsoft.com/en-us/dotnet/api/system.threading.manualresetevent?view=netcore-3.1
            ManualResetEvent mre = new ManualResetEvent(false); //false表示：not signaled
            ThreadPool.QueueUserWorkItem(state => { //state is null, 因为这个方法中只有callback这一个参数
                x = "123"; //注意：与java的区别，java要求这里的x必须是final的（？），而C#中可以该值，而且外边能看见
                tmp1.x1 = "123";
                mre.Set();
            });
            mre.WaitOne(); //等待signal
            Assert.AreEqual("123", tmp1.x1);
            Assert.AreEqual("123", x);

            //Reset之后，这个event恢复原有状态，可以继续完成之前的工作
            mre.Reset();
            ThreadPool.QueueUserWorkItem(state => { //state = "any data for task"，作为该方法的第二个参数，交给task执行
                x = (String)state;
                tmp1.x1 = (String)state;
                mre.Set();
            }, "any data for task");
            mre.WaitOne();
            Assert.AreEqual("any data for task", tmp1.x1);
            Assert.AreEqual("any data for task", x);


            //这个方法的功能需要很多的背景信息（P614， Section 27.3 CLR&C# 4th CN）
            //一个线程创建时候，将从caller线程赋值context。通过SuppressFlow方法，这个复制动作取消。
            //即：其创建的新线程将不再有当前线程的context，性能上高很多，不管可能如果使用这个context的话，则无法使用
            //TODO：什么情况下需要这个context？我看CallContext.LogicalSetData(...)在。net core上面都没有这个类了
            var asyncFlowControl = ExecutionContext.SuppressFlow(); //阻止当前线程执行上线问

            //asyncFlowControl.Undo();
            ExecutionContext.RestoreFlow();//用asyncFlowControl.Undo()也可以，不管因为都是当前线程，所以只用用这些也行
        }

        /**
         * 这个测试case很有意思，主要是如何把state穿如到callback方法里面去
         */
        [Test]
        public void testBasicTaskExecution_method()
        {
            Int32 sum = 0;
            String outState = "";
            ManualResetEvent mre = new ManualResetEvent(false); //false表示：not signaled
            ThreadPool.QueueUserWorkItem(state => testBasicTaskExecution2_add(1, 2, mre, (String)state, out sum, out outState), "any data for task");
            mre.WaitOne();
            Assert.AreEqual(3, sum);
            Assert.AreEqual("any data for taskXXX", outState);
        }

        public void testBasicTaskExecution2_add(Int32 n1, Int32 n2, ManualResetEvent mre, String callState, out Int32 sum, out String resultState) {
            sum = n1 + n2;
            resultState = callState+"XXX";
            mre.Set();
        }

        /**
         * 提交的task可以被cancel（via token），不过task内部需要不断检查token状态
         * - 有这个一个辅助类CancellationTokenSource，在callback执行的时候（一般是循环）可以不管检查这个辅助类状态
         * - 外部线程可以设定其状态，使得callback执行可以提前结束
         * - java中，我们设定一个volatile的变量， 不是特别清楚这个的用意         * 
         */
        [Test]
        public void testCancellationTokenSource()
        {
            CancellationTokenSource cts = new CancellationTokenSource();
            ThreadPool.QueueUserWorkItem(state=> testCancellationTokenSource_callback(cts.Token, 1, 2));
            cts.Cancel();

            //还没想好设计什么样的case去检查运行结果
        }

        public void testCancellationTokenSource_callback(CancellationToken token,Int32 anyILike, Int32 anyMoreILike) {
            Int32 counter = 0;
            while ((!token.IsCancellationRequested) && (counter < 100)) {
                counter++;
                Thread.Sleep(1000);
            }
            
        }

        /**
         * Cancel时候额外执行一些提前register的delegate方法
         * - 这个继续测试CancellationTokenSource，增加了Cancel时候要执行的若干操作
         * 
         */
        [Test]
        public void testCancellationTokenSource_Register()
        {
            CancellationTokenSource cts = new CancellationTokenSource();
            //这些注册的delegate，将在执行Cancel的时候调用到
            cts.Token.Register(() => { });
            cts.Token.Register(() => { });
            cts.Token.Register(() => { });

            ThreadPool.QueueUserWorkItem(state => testCancellationTokenSource_callback(cts.Token, 1, 2));
            cts.Cancel();//看文档描述，这个空参数的类似与Cancel(true)
            //cts.Cancel(true/false) 是否：throwOnFirstException，即是否有问题时候执行一半 。 
            //注意：这里callback的检查方式是token.IsCancellationRequested，它是不会throw异常的
            //     但是，在ThreadEval_Tasks::testTasks_cancel（）中，我们是通过抛异常来停止的token.ThrowIfCancellationRequested()
            //     使用不同的检查方式，是因为本case（QueueUserWorkItem不返回值），而Task那里需要返回值（需要区别正常返回还是cancel返回，如果是cancel返回就有异常）


            //其他功能：还能link多个token到一起，这样cancel1个的话，另一个也cancel了
            CancellationTokenSource cts1 = new CancellationTokenSource();
            CancellationTokenSource cts2 = new CancellationTokenSource();
            var linkedCts = CancellationTokenSource.CreateLinkedTokenSource(cts1.Token, cts2.Token);
            linkedCts.Token.Register(() => { }); //注册cancel执行的delegate
            //这里写加入threadpool代码
            cts2.Cancel(); //这时候cts1也cancel了，同时执行上面的Register

            //还没想好设计什么样的case去检查运行结果
        }

        /**
         * Cancel的动作可以把多个token关联起来
         * 
         */
        [Test]
        public void testCancellationTokenSource_LinkToken()
        {

            //其他功能：还能link多个token到一起，这样cancel1个的话，另一个也cancel了
            //note：其实还有别的，如多久以后cancel等，这里就不写case了
            CancellationTokenSource cts1 = new CancellationTokenSource();
            CancellationTokenSource cts2 = new CancellationTokenSource();
            var linkedCts = CancellationTokenSource.CreateLinkedTokenSource(cts1.Token, cts2.Token);

            linkedCts.Token.Register(() => { }); //注册cancel执行的delegate
            
            //这里写加入threadpool代码
            
            cts2.Cancel(); //这时候cts1也cancel了，同时执行上面的Register

            //还没想好设计什么样的case去检查运行结果
        }
    }
}
