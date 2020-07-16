﻿using NUnit.Framework;
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
     * 参考资料
     * - C#InDepth 3rd Chapter 15
     * - C#7&.net core 2.0 11th Chapter 15
     * 
     */
    class ASyncAwaitEval
    {
     
        //这是一个Utility方法
        public static void TraceThreadAndTask(String info) {
            String taskInfo = Task.CurrentId == null ? "no task" : "task " + Task.CurrentId;
            Console.WriteLine($"{DateTime.Now} {info} in thread {Thread.CurrentThread.ManagedThreadId}" + $" and {taskInfo}");
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
                    TraceThreadAndTask($"running {nameof(GreetingAsync)}");
                    return Greeting(name);
                });
                return result;
            }
            private String Greeting(String name)
            {
                TraceThreadAndTask($"running {nameof(Greeting)}");
                Task.Delay(3000).Wait();
                return $"Hello, {name}";
            }

        }

        [Test]
        public void testGreetingClient()
        {
            //下面是这个过程的输出
            //2020/7/16 21:59:50 testGreetingClient enter in thread 13 and no task
            //2020/7/16 21:59:50 callGreetingClientAsync enter in thread 13 and no task
            //2020/7/16 21:59:50 running GreetingAsync in thread 11 and task 1
            //2020/7/16 21:59:50 running Greeting in thread 11 and task 1
            //2020/7/16 21:59:50 testGreetingClient got Task<String> greetingResult, and next to get greetingResult.Result in thread 13 and no task
            //2020/7/16 21:59:53 callGreetingClientAsync done in thread 11 and no task
            //2020/7/16 21:59:53 testGreetingClient done - got greetingResult.Result:Hello, HanMeiMei in thread 13 and no task
            ThirdPartyAsyncSimulator greetingClient = new ThirdPartyAsyncSimulator();
            //线程A - 调用者当前线程
            TraceThreadAndTask($"{nameof(testGreetingClient)} enter");
            Task<String> greetingResult = callGreetingClientAsync();
            
            TraceThreadAndTask($"{nameof(testGreetingClient)} got Task<String> greetingResult, and next to get greetingResult.Result");

            String greetingResultStr = greetingResult.Result;

            //线程A - 这里线程没变
            TraceThreadAndTask($"{nameof(testGreetingClient)} done - got greetingResult.Result:{greetingResultStr}");
        }

        public async Task<String> callGreetingClientAsync()
        {

            ThirdPartyAsyncSimulator greetingClient = new ThirdPartyAsyncSimulator();
            //线程A - 就是调用者所在的线程
            TraceThreadAndTask($"{nameof(callGreetingClientAsync)} enter");
            Task<String> greetingResult = greetingClient.GreetingAsync("HanMeiMei");

            //一旦开始await，这个方法就返回了。调用方法立马得到了Task<String>的结果
            String greetingResultStr = await greetingResult;

            //线程B - await之后，task执行之后，处理执行结果是其他线程（不再保证是进入是的线程A）
            TraceThreadAndTask($"{nameof(callGreetingClientAsync)} done");
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
            TraceThreadAndTask("{nameof(testHttpClient)} enter");
            Task<int> lengthTask = GetPageLengthAsync("http://www.baidu.com");

            //在async方法运行的时候，当前task已经返回到这里了，但是如果这里获取结果的话，则继续等待直到async方法执行完毕
            //也就是说，从这儿的角度，其并没有加快什么
            //除非当前这里的业务逻辑不在乎task执行结果（我是说这一行不在乎执行结果，执行结果可以在GetPageLengthAsync里的线程B中做某种处理）································································································································································································································································································································································································································································································································································································································································································································································································································
            Console.WriteLine(lengthTask.Result);

            //线程A - 这里线程没变
            TraceThreadAndTask("{nameof(testHttpClient)} done");
        }

        static async Task<int> GetPageLengthAsync(string url) {
            using (HttpClient client = new HttpClient()) {

                //线程A
                TraceThreadAndTask("{nameof(GetPageLengthAsync)} enter"); 
                Task<String> fetchTextTask = client.GetStringAsync(url);

                //代码执行到这里方法就返回了。这个耗时的client.GetStringAsync(url)将在
                //<=====这个await的magic之处在于前后的线程变了
                int length = (await fetchTextTask).Length;

                //线程B 《=== 不再是线程A了！！！！
                TraceThreadAndTask("{nameof(GetPageLengthAsync)} done"); 

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