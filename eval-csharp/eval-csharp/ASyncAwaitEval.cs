using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace eval_csharp
{

    /**
     * 
     * async/await的核心思想就是
     * - await之后的结果，在一个其他线程中得到（可以继续处理一下子），然后返回给调用者
     * - 本文又给了几个相对实用一点点的几个方法，从C#Indepth 3rd中copy而来
     * - 勇敢的面对/使用/read相关async代码
     *   - 其与普通操作其实相差非常小
     *   - 见下面给出的一个urls.Select例子
     *   
     * async编程的重要性
     * - 一般都围绕着避免UI长时间给占用展开
     *   - 细节来讲，await的时候会把当前UI线程释放掉。获取结果之后await之后的方法块继续执行（新线程），据说还可以在这里更新UI组件（没有尝试，值得为怀疑/探索）
     * - 还有有地方说对于IO操作有帮助
     *   - 但是暂时还没有找到合理的例子
     *   
     * await时候是否应该使用ConfigureAwait(false) ？
     * - 网上文章一边倒的让使用它ConfigureAwait(false) ， 当你不要求前后线程必须相同的时候。
     * - 其出现的背景是UI线程调用，其要求UI组件的操作必须在UI线程上面，所以希望醒来以后还是之前那个UI线程。
     *   - 所以，在UI项目中，await之后，其默认将返回到UI线程执行，其通过一个synchronization context完成。
     *     - 但是，即使是UI项目，可能也有些await后的调用不操作UI，也就是不需要是UI线程。这时候，就可以通过ConfigureAwait(false)，使得随便一个线程调用都行
     *     - 注意：asp。net项目也有类似行为（但是asp。net core没有这个行为） - https://stackoverflow.com/questions/13489065/best-practice-to-call-configureawait-for-all-server-side-code
     *     - WindowsForm/WPF/WindowsRT都是这样
     *       - https://devblogs.microsoft.com/dotnet/configureawait-faq/
     *     - “The UI Thread has a SynchronizationContext by default. If there is a SynchronizationContext (i.e. we are in the UI thread) the code after an await will run in the original thread context. This is the default and expected behaviour.”
     *       - https://medium.com/bynder-tech/c-why-you-should-use-configureawait-false-in-your-library-code-d7837dce3d7f
     * - 但是，在.net core application项目中，默认情况下没有上面UI调用的线程模型限制，也没有相关的context
     *   - 没有必要使用ConfigureAwait(false)
     *   - 经过我测试，直接await醒来后，其线程id是从pool中获取的（即前后线程不同）。
     *   - 所以说这个官方文档的说法大前提是UI组件，但是他没提
     *     - “When an asynchronous method awaits a Task directly, continuation usually occurs in the same thread that created the task, depending on the async context. ”
     *     - https://docs.microsoft.com/en-us/visualstudio/code-quality/ca2007?view=vs-2019
     *   - 再但是，如果有个3rd party败家程序设定的context，而你的代码又使用的默认await（没有使用ConfigureAwait-false），则你的醒来代码还是运行在原线程上（导致性能有点不太好）
     *     - 见：“I’ve heard ConfigureAwait(false) is no longer necessary in .NET Core. True?” https://devblogs.microsoft.com/dotnet/configureawait-faq/
     * - 但是Fxcop代码扫描的时候，一律提示这玩意儿要改，真烦
     * 
     * 参考资料
     * - C#InDepth 3rd Chapter 15
     * - C#7&.net core 2.0 11th Chapter 15
     * 
     */
    class ASyncAwaitEval
    {


        /**
         * 本例子来源与C#7 and .Net 2.0 - cahpter 21 /P460
         * 我觉得对理解async/await比较有帮助，就copy到这里来
         * note：本例子await对象为Task.Delay(200), 其没有返回结果。其他的例子中有返回结果，相互参考。
         */
        [Test]
        public void testParallelForOfAsync() {

            //2020.07.17 12:12:48:143 S 3 in thread 14 and task 4
            //2020.07.17 12:12:48:143 S 1 in thread 11 and task 2
            //2020.07.17 12:12:48:143 S 4 in thread 15 and task 5
            //2020.07.17 12:12:48:143 S 2 in thread 6 and task 3
            //2020.07.17 12:12:48:143 S 0 in thread 13 and task 1
            //2020.07.17 12:12:48:144 S 6 in thread 18 and task 7
            //2020.07.17 12:12:48:143 S 5 in thread 16 and task 6
            //2020.07.17 12:12:48:144 S 7 in thread 17 and task 8
            //2020.07.17 12:12:49:029 E 7 in thread 17 and task 8
            //2020.07.17 12:12:49:029 E 5 in thread 16 and task 6
            //2020.07.17 12:12:49:029 E 6 in thread 18 and task 7
            //2020.07.17 12:12:49:029 E 0 in thread 13 and task 1
            //2020.07.17 12:12:49:029 S 8 in thread 19 and task 9
            //2020.07.17 12:12:49:029 E 4 in thread 15 and task 5
            //2020.07.17 12:12:49:029 S 9 in thread 13 and task 1
            //2020.07.17 12:12:49:029 E 1 in thread 11 and task 2
            //2020.07.17 12:12:49:029 E 3 in thread 14 and task 4
            //2020.07.17 12:12:49:029 E 2 in thread 6 and task 3
            //2020.07.17 12:12:49:234 E 9 in thread 13 and task 1
            //2020.07.17 12:12:49:234 E 8 in thread 19 and task 9
            //2020.07.17 12:12:49:234 Completed - WITHOUT async/with - True in thread 13 and no task
            //这是没有async/await的情况，用于对比
            //可以看到S/E所使用的线程是一致是一样的；还可以看到这个Delay的时间不是很准确，我要delay200毫秒，而这里显示达到了900毫秒左右。
            ParallelLoopResult result1 = Parallel.For(0, 10, i =>
            {
                ThreadEval_Util.TraceThreadAndTask($"S {i}");
                Task.Delay(200).Wait();
                ThreadEval_Util.TraceThreadAndTask($"E {i}");
            });
            ThreadEval_Util.TraceThreadAndTask($"Completed - WITHOUT async/with - {result1.IsCompleted}");

            //2020.07.17 12:12:49:236 S 0 in thread 13 and task 10
            //2020.07.17 12:12:49:236 S 6 in thread 14 and task 11
            //2020.07.17 12:12:49:236 S 7 in thread 11 and task 12
            //2020.07.17 12:12:49:236 S 2 in thread 19 and task 13
            //2020.07.17 12:12:49:236 S 8 in thread 18 and task 14
            //2020.07.17 12:12:49:236 S 1 in thread 17 and task 15
            //2020.07.17 12:12:49:236 S 3 in thread 16 and task 16
            //2020.07.17 12:12:49:236 S 4 in thread 15 and task 17
            //2020.07.17 12:12:49:236 S 5 in thread 6 and task 18
            //2020.07.17 12:12:49:238 S 9 in thread 13 and task 10
            //2020.07.17 12:12:49:239 Completed - WITH async/with - True in thread 13 and no task
            //2020.07.17 12:12:49:429 E 9 in thread 6 and no task
            //2020.07.17 12:12:49:429 E 3 in thread 11 and no task
            //2020.07.17 12:12:49:429 E 8 in thread 16 and no task
            //2020.07.17 12:12:49:429 E 1 in thread 18 and no task
            //2020.07.17 12:12:49:429 E 4 in thread 17 and no task
            //2020.07.17 12:12:49:429 E 7 in thread 14 and no task
            //2020.07.17 12:12:49:429 E 5 in thread 19 and no task
            //2020.07.17 12:12:49:429 E 2 in thread 15 and no task
            //2020.07.17 12:12:49:429 E 0 in thread 11 and no task
            //2020.07.17 12:12:49:429 E 6 in thread 19 and no task
            //1. 这里可以看到，S/E所对应的线程是不同的
            //2. Parallel.For方法直接返回了（没有等Delay执行完成 - Completed直接打印了）
            //   过了一会儿，await完成后其他线程把await后的结果执行一遍
            ParallelLoopResult result2 = Parallel.For(0, 10,async i =>
            {
                ThreadEval_Util.TraceThreadAndTask($"S {i}");
                await Task.Delay(200);
                ThreadEval_Util.TraceThreadAndTask($"E {i}");
            });
            ThreadEval_Util.TraceThreadAndTask($"Completed - WITH async/with - {result1.IsCompleted}");
            //下面这个Sleep是有必要的，因为使用了async之后，Parallel调用直接返回（碰到await就返回了），不等待Delay。
            //如果不加这个Sleep，测试case就直接完成了，则后续的ThreadEval_Util.TraceThreadAndTask($"E {i}");无法打印了。
            Thread.Sleep(250);
        }


        //这个类中包含一个按照约定而模拟的一个GreetingAsync方法
        //其类似于HttpClient::public Task<string> GetStringAsync(string requestUri);方法
        //调用者将传入参数（如name），其内部将提交一个task，并且将task的handler返回（如Task<String>)
        //note：关于如何使用Task的细节，参考ThreadEval_Tasks.cs
        internal class ThirdPartyAsyncSimulator {
            public Task<String> GreetingAsync(String name)
            {
                Task<String> result = Task.Run<String>(() =>
                {
                    ThreadEval_Util.TraceThreadAndTask($"running {nameof(GreetingAsync)}");
                    return Greeting(name);
                });
                return result;
            }
            private String Greeting(String name)
            {
                ThreadEval_Util.TraceThreadAndTask($"running {nameof(Greeting)}");
                Task.Delay(500).Wait();
                return $"Hello, {name}";
            }

        }

        [Test]
        public void testGreetingClient()
        {
            //下面是这个过程的输出
            //2020.07.17 10:11:55:536 testGreetingClient enter in thread 13 and no task
            //2020.07.17 10:11:55:538 callGreetingClientAsync enter in thread 13 and no task
            //2020.07.17 10:11:55:539 running GreetingAsync in thread 11 and task 1
            //2020.07.17 10:11:55:539 running Greeting in thread 11 and task 1
            //2020.07.17 10:11:55:539 testGreetingClient got Task<String> greetingResult, and next to get greetingResult.Result in thread 13 and no task
            //这里有个500毫秒的gap（当时Greeting方法正在blocking wait）
            //2020.07.17 10:11:56:050 callGreetingClientAsync done in thread 11 and no task
            //2020.07.17 10:11:56:050 testGreetingClient done - got greetingResult.Result:Hello, HanMeiMei in thread 13 and no task
            
            ThirdPartyAsyncSimulator greetingClient = new ThirdPartyAsyncSimulator();
            
            //线程A - 调用者当前线程
            ThreadEval_Util.TraceThreadAndTask($"{nameof(testGreetingClient)} enter");
            Task<String> greetingResult = callGreetingClientAsync();
            ThreadEval_Util.TraceThreadAndTask($"{nameof(testGreetingClient)} got Task<String> greetingResult, and next to get greetingResult.Result");

            //线程A - 前面都很快，而执行这个获取task结果卡住了，因为结果还没有出来
            String greetingResultStr = greetingResult.Result;
            //线程A - 这里线程没变
            ThreadEval_Util.TraceThreadAndTask($"{nameof(testGreetingClient)} done - got greetingResult.Result:{greetingResultStr}");
        }

        public async Task<String> callGreetingClientAsync()
        {

            ThirdPartyAsyncSimulator greetingClient = new ThirdPartyAsyncSimulator();
            //线程A - 就是调用者所在的线程
            ThreadEval_Util.TraceThreadAndTask($"{nameof(callGreetingClientAsync)} enter");
            Task<String> greetingResult = greetingClient.GreetingAsync("HanMeiMei");

            //一旦开始await，这个方法就返回了。调用方法立马得到了Task<String>的结果
            //String greetingResultStr = await greetingResult;

            //这里使用await greetingResult与 await greetingResult.ConfigureAwait(false)是一样的
            //note：但是如果是UI程序，或者asp.net则不一样
            //使用ConfigureAwait(false)的目的是让醒来后的线程可以是随意一个线程池中的线程
            //见本类开头更多总结
            String greetingResultStr = await greetingResult.ConfigureAwait(false);

            //线程B - await之后，task执行之后，处理执行结果是其他线程（不再保证是进入是的线程A）
            ThreadEval_Util.TraceThreadAndTask($"{nameof(callGreetingClientAsync)} done");
            return greetingResultStr;
        }


        /**
         * 
         * 这里给出了1个使用httpclient async方法的例子（其实也没看出来节省时间！因为最外部调用方法还是要等待）
         * 注释掉了[Test]是因为其有外部依赖（baidu.com). 这个调用过程用上面的Greeting完全是一致的
         * 
         */
        //[Test]
        public void testHttpClient() {
            //线程A
            ThreadEval_Util.TraceThreadAndTask("{nameof(testHttpClient)} enter");
            Task<int> lengthTask = GetPageLengthAsync("http://www.baidu.com");

            //在async方法运行的时候，当前task已经返回到这里了，但是如果这里获取结果的话，则继续等待直到async方法执行完毕
            //也就是说，从这儿的角度，其并没有加快什么
            //除非当前这里的业务逻辑不在乎task执行结果（我是说这一行不在乎执行结果，执行结果可以在GetPageLengthAsync里的线程B中做某种处理）································································································································································································································································································································································································································································································································································································································································································································································································································
            Console.WriteLine(lengthTask.Result);

            //线程A - 这里线程没变
            ThreadEval_Util.TraceThreadAndTask("{nameof(testHttpClient)} done");
        }

        static async Task<int> GetPageLengthAsync(string url) {
            using (HttpClient client = new HttpClient()) {

                //线程A
                ThreadEval_Util.TraceThreadAndTask("{nameof(GetPageLengthAsync)} enter"); 
                Task<String> fetchTextTask = client.GetStringAsync(url);

                //代码执行到这里方法就返回了。这个耗时的client.GetStringAsync(url)将在
                //<=====这个await的magic之处在于前后的线程变了
                int length = (await fetchTextTask).Length;

                //线程B 《=== 不再是线程A了！！！！
                ThreadEval_Util.TraceThreadAndTask("{nameof(GetPageLengthAsync)} done"); 

                //注意，函数返回类型为Task<int>, 但是我们这里要返回int
                return length;
            }
        }

        [Test]
        public void anonymousAsyncFunc() {
            //这里从C#Indepth3rd15.4中copy了两个lambda表达式的aync函数
            Func<Task> lambda = async () => await Task.Delay(1000);
            Func<Task<int>> anonMethod = async delegate ()
            {
                Console.WriteLine("Started");
                await Task.Delay(1000);
                Console.WriteLine("Finished");
                return 10;
            };

            Func<int, Task<int>> anonMethod2 = async x =>
            {
                Console.WriteLine("Started");
                await Task.Delay(1000);
                Console.WriteLine("Finished");
                return 10;
            };

            Task<int> first = anonMethod();
            Task<int> second = anonMethod2(5);
        }



        //[Test]
        public async Task<String[]> testUrlSelectGoooodExample()
        {
            var urls = new List<String> { "http://www.baidu.com", "http://www.bing.com" };

            //第一次看到下面方法有点头疼的,稍微仔细看一下就好了
            //- 首先，忘记async/await
            //  - 这是一个Enumerable.Select,  其实就是一个Linq的from url in urls; select new HttpClient().GetStreamAsync(url)
            //  - 对于Linq来说，这个操作有点不那么常见，因为我们的Linq之前都是操作数据的，这个更像操作一个控制流（时机尚未Task<String>),稍微有点不适应
            // - 然后，加上async/await有啥影响呢？
            //  - 我感觉也没啥影响。返回结果是
            var tasks = urls.Select( async url =>
            {
                using (var client = new HttpClient()) {
                    return await client.GetStringAsync(url);
                }
            }).ToList();

            //上面方法，我们打开多个Url链接去获取数据（每个url 1个）
            //什么时候可以使用呢？需要检查结果

            //从performance的角度，我觉得下面的方法await已经不大了
            //最主要的性能问题，集中在上面Select时候执行的那里。那里通过以后，接下来就应该很快
            //不过，当前通过返回Task这种方式，是的外部还可以继续使用async，整盘棋就都是async了
            String[] results = await Task.WhenAll(tasks);
            return results;
        }


    }
}
