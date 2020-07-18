using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace eval_csharp
{
    /**
     * Thread中碰到的各种方便的控制objects，官方文档就挺清楚
     * https://docs.microsoft.com/en-us/dotnet/standard/threading/threading-objects-and-features
     * 
     */
    class ThreadEval_ThreadingObjects
    {


        /**
         * ManualResetEvent
         * https://docs.microsoft.com/en-us/dotnet/api/system.threading.manualresetevent?view=netcore-3.1
         * "You use ManualResetEvent, AutoResetEvent, and EventWaitHandle for thread interaction (or thread signaling). 
         * For more information, see the Thread interaction, or signaling section 
         * of the Overview of synchronization primitives article."
         * 
         * 这玩意儿在两个线程间通信挺有帮助
         * - 初始化 var mre = new ManualResetEvent(false);
         * - 线程1等待线程2完成某间事情，by mre.WaitOne() ， 线程1当前线程阻塞
         *   - 可以增加等待时间，检查其true/false返回值判断是否已经满足条件（Set）
         * - 线程2完成其事情后，调用 mre.Set()
         * - 线程1可以继续了
         * 
         * 另外：这个变量可以通过Reset重置，然后就可以继续使用了
         * 
         */
        [Test]
        public void testCommunicationByManualResetEvent()
        {

            //代码参考 ThreadEval_QueueUserWorkItem::testBasicTaskExecution

        }

        /**
         * Mutext 可以在多个**进程**之间共享(只有有name的mutex才有这个特性）
         * - 参考C#7.0 chapter21.13中，使用它来防止多启动重复进程的例子
         */
        [Test]
        public void testMutex()
        {

            //createdNew：表示这个mutex是不是新创建的
            //第一个参数：false，表示是否当前线程要拥有这个mutex. 如果第一个参数是false，则当前caller不用有个这个mutex，如果然后调用ReleaseMutex则会报错。
            bool createdNew;
            
            var mutexInitOwn = new Mutex(true, "mutexInitOwn", out createdNew);
            Console.WriteLine($"mutexInitOwn-createdNewcreatedNew:{createdNew}");
            //这里可以直接release，因为第一个参数initiallyOwn=true。
            mutexInitOwn.ReleaseMutex();

            var mutexInitNotOwn = new Mutex(false, "mutexInitNotOwn", out createdNew);
            Console.WriteLine($"mutexInitNotOwn-createdNewcreatedNew:{createdNew}");
            //这里可以不能直接release，因为第一个参数initiallyOwn=false。要先获取这个mutex再释放，否则exception
            mutexInitNotOwn.WaitOne();
            mutexInitNotOwn.ReleaseMutex();

        }

        /**
         * https://stackoverflow.com/questions/153877/what-is-the-difference-between-manualresetevent-and-autoresetevent-in-net
         * ManualResetEvent 与 AutoResetEvent的区别就是使用完以后，如果要继续使用，是否需要人肉reset/还是自动reset
         * - System.Threading.AutoResetEvent, which derives from EventWaitHandle and, 
         *   - when signaled, resets automatically to an unsignaled state after releasing a single waiting thread.
         * - System.Threading.ManualResetEvent, which derives from EventWaitHandle and, 
         *   - when signaled, stays in a signaled state until the Reset method is called.
         *   - 如果是signaled之后，多次调用WaitOne都能返回（如果中间没有Reset的话）
         * 注意：如果在Wait之前就发送了信号by Set（），也没关系
         * 
         *  - Mutex在线程1中获取lock，则也必须在线程1中释放它！
         *  - Semophore则没有这个限制. 可以通过使用Semophore（count=1）来完成相同的作用。
         */
        [Test]
        public void testResetEvent()
        {

            //ThreadEval_QueueUserWorkItem::testBasicTaskExecution中，使用了ManualResetEvent

            //注意：如果在Wait之前就发送了信号by Set（），也没关系
            //下面就是证明这个的， WaitOne执行的时候，Set已经执行完了。WaitOne还是顺利的继续执行
            //false：not signaled, 就是需要等待一个新信号才能执行的意思，就是执行WaitOne的时候会block的意思
            //true：signaled, 直接WaitOne就能返回了
            ManualResetEvent mre = new ManualResetEvent(false);
            ThreadPool.QueueUserWorkItem(state => {
                Thread.Sleep(5);
                ThreadEval_Util.TraceThreadAndTask("002 before set");
                mre.Set();
            });
            Thread.Sleep(1000);
            ThreadEval_Util.TraceThreadAndTask("002 before wait");
            for (int i = 0; i < 5; i++) {
                //因为没有执行Reset，其一直是signaled状态，所以WaitOne一直可以返回
                mre.WaitOne(); //很可惜没有等待时间超时设置
            }
            ThreadEval_Util.TraceThreadAndTask("003 got signal");

        }

        [Test]
        public void testCountDownEvent()
        {

            CountdownEvent evt = new CountdownEvent(5);

            //其他子线程中调用这些
            evt.Signal();
            evt.Signal();
            evt.Signal();
            evt.Signal();
            evt.Signal();

            //当前线程中等待
            //等待100毫秒以后，是否已经收集到所有信号了
            //if not，应该继续等（并打印log）
            bool got = evt.Wait(100);
        }

        /**
         * 只是表示C#中也有这类，用于控制多个线程可以万箭齐发！
         */
        [Test]
        public void testBarrier()
        {
        }

        /**
         * 
         * https://docs.microsoft.com/en-us/dotnet/standard/threading/semaphore-and-semaphoreslim
         * 
         * Semophore用来控制资源访问的最大并发数量
         * 
         * Semaphore vs SemaphoreSlim
         * - SemaphoreSlim 只能用于进程内部
         * - Semaphore with name可以用于多个进程之间
         * 
         * 注意：
         *  - Release超过最大数量会抛异常
         *    - “The count on the semaphore is full, and when thread A eventually calls Release, a SemaphoreFullException is thrown.”
         * 
         * TODO 这里的代码写的太仓促，需要重写,现在就是要示意一下api
         */
        [Test]
        public void testSemophore()
        {

            //init count:1
            //max count  : 4
            Semaphore semophore = new Semaphore(1, 4);
            //bool got = semophore.Wait();
            bool got = semophore.WaitOne();


            SemaphoreSlim semophoreSim = new SemaphoreSlim(1, 4);
            bool gotSlim = semophoreSim.Wait(600);

            //典型的异步编程调用，方法名称也是符合convention（以Async结尾）
            Task<bool> taskGotSlim = semophoreSim.WaitAsync(600);


            //得到之后要执行release
            semophoreSim.Release();

        }
    }
}
