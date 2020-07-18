using NUnit.Framework;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace eval_csharp
{
    /**
     * 关于线程看这个官方文档就可以了
     * https://docs.microsoft.com/en-us/dotnet/standard/threading/
     */
    class ThreadEval_basic
    {

        /**
         * - 1）Task.Delay(3000) vs 2）Thread.Sleep(3000) vs 3）Task.Delay(3000).Wait()
         * - 如果想blocking wait（当前线程暂停）
         *   -2） Thread.Sleep(3000) 与 3）Task.Delay(3000).Wait() 都可以，两者作用几乎一样
         *     - 当前线程会停止 - 见 testBlockingWait()
         *     - Thread.Sleep(...) 创建一个event，X毫秒后event出发其醒来
         *       - Thread.Sleep(...) creates an event to wake you up in X millisec, 
         *       -   then puts your Thread to sleep... in X millisec, the event wakes you up.
         *     - Task.Delay(3000).Wait()
         *       - Task.Delay(...).Wait() creates an event to start a Task in X millisec, 
         *       -    then puts your Thread to sleep until the Task is done (with Wait)... in X millisec, 
         *       -    the event starts the Task which ends immediately and then wakes you up.
         *       - Task.Delay(...)内部使用Timer
         *         - https://stackoverflow.com/questions/17258428/thread-sleep-vs-task-delay
         *     - 参考： https://stackoverflow.com/questions/29356139/should-i-always-use-task-delay-instead-of-thread-sleep
         *   - 两者都支持cancel，见下面测试case
         *     - testStopThreadSleep()
         *     - testStopTaskWait()
         *   
         * - 如果想通过async方式执行，则通过 1）Task.Delay(3000)来完成
         *     - 见 testTaskAsyncDelay
         *     - 这个最有效的方式是说调用者有机会执行一些其他的东西
         */
        [Test]
        public void testBlockingWait()
        {
            //下面是两种等价的blocking wait方式

            //外部获取当前线程的handler的话，可以interrup停止它（参考testStopThreadSleep()）
            Thread.Sleep(100);

            //与上面的效果一样，当前线程block了,要停止它需要Delay的时候传入cancel token，并调用Cancel（参考testStopTaskWait()）
            Task.Delay(100).Wait();
        }


        [Test]
        public async Task testTaskAsyncDelay() {

            //2020.07.17 10:09:59:294 before delay in thread 13 and no task           - step 1 - 调用者线程
            //调用者碰到await直接返回，继续执行其他工作(这里的调用者实际上为NUnit测试框架） - step 2
            //2020.07.17 10:09:59:394 after delay in thread 9 and no task             - step 3 - 100毫秒之后，新线程继续执行

            Int32 delayInterval = 100;
            //线程A
            ThreadEval_Util.TraceThreadAndTask("before delay"); //step 1
            //线程A执行到await之后，立马返回
            await Task.Delay(delayInterval);                    //step 2

            //过delayInterval毫秒之后，线程B继续执行下面逻辑
            //线程B
            ThreadEval_Util.TraceThreadAndTask("after delay");  //step 3
        }



        /***
         * 对于Thread.Sleep的线程，可以调用其线程的Interrupt方法来停止它,同时抛出Interrupted异常
         * 调用方式与java一样
         * 这篇官方文档写的非常清楚https://docs.microsoft.com/en-us/dotnet/standard/threading/pausing-and-resuming-threads
         * 
         * 不过有人问如何停止ThreadSleep时候，有人建议使用ManualResetEvent
         * https://stackoverflow.com/questions/7448589/interrupt-a-sleeping-thread
         * 本类中有一个测试ManualResetEvent的case ： testCommunicationByManualResetEvent
         * 
         */
        [Test]
        public void testStopThreadSleep() { 
            //没啥好写的，看官方链接
        }

        /**
         * 要停止的话通过CancellationToken, 当前task将抛出TaskCancelledException
         * - 关于通过token cancel，还可以参考 
         *   - ThreadEval_QueueUserWorkItem::testCancellationTokenSource_Register()
         *   - ThreadEval_Tasks::testTasks_cancel()
         * - 本例子的cancel相对场景特别简单，因为就是停止Wait，但是上面两个例子中是针对实际运行的程序进行cancel，
         *   - 值得看一看
         */
        [Test]
        public void testStopTaskWait()
        {
            //2020/7/17 9:47:55 before cts.cancel in thread 10 and no task       // step 1
            //执行cts.Cancel()                                                   // step 2
            //2020/7/17 9:47:55 cancel token reg - 03 in thread 10 and no task   // step 3
            //2020/7/17 9:47:55 cancel token reg - 02 in thread 10 and no task   // step 4
            //2020/7/17 9:47:55 cancel token reg - 01 in thread 10 and no task   // step 5
            //Delay地方捕捉到异常                                                 // step 6  
            //2020/7/17 9:47:55 after delay in thread 13 and no task             // step 7
            CancellationTokenSource cts = new CancellationTokenSource();
            //这些注册的delegate，将在执行Cancel的时候调用到
            cts.Token.Register(() => { ThreadEval_Util.TraceThreadAndTask("cancel token reg - 01"); }); // step 5
            cts.Token.Register(() => { ThreadEval_Util.TraceThreadAndTask("cancel token reg - 02"); }); // step 4
            cts.Token.Register(() => { ThreadEval_Util.TraceThreadAndTask("cancel token reg - 03"); }); // step 3

            ThreadPool.QueueUserWorkItem(state => {
                ThreadEval_Util.TraceThreadAndTask("before cts.cancel");  // step 1
                Thread.Sleep(10);
                cts.Cancel(); // step 2
            });

            //System.AggregateException : One or more errors occurred. (A task was canceled.)
            //  ----> System.Threading.Tasks.TaskCanceledException : A task was canceled.
            Assert.Throws<AggregateException>(() => Task.Delay(100, cts.Token).Wait()); // step 6

            ThreadEval_Util.TraceThreadAndTask("after delay"); // step 7
        }

    }
}
