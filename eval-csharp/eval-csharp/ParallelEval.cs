using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace eval_csharp
{
    /**
     * Parallel线程模型: 其调用的所有方法执行完,parallel方法才返回
     * - 注意与Task的区别. Task是任务添加完之后,直接返回
     * 
     * ParallelLinq不在这里，而是在LinqEval_Parallel_for_Objects中
     * - 其实看官方文档就挺好了 
     *   - https://docs.microsoft.com/en-us/dotnet/standard/parallel-programming/
     *   - 譬如 这有各种如何写Parallel的文档 https://docs.microsoft.com/en-us/dotnet/standard/parallel-programming/data-parallelism-task-parallel-library
     *   - 我现在需要做的是如何快速找到各个文档
     * 
     */
    class ParallelEval
    {

        /**

         * - 数据并行性（调用相同代码，不同数据)
         *   - for循环的parallel版
         *   - foreach的parallel版
         * - 任务并行性(调用不同地方）·
         *   - invoke多个方法（并行）
         * 
         * 如果调用方法任意一个有异常，则总方法抛出AggregateException
         */
        [Test]
        public void testParallelFor_Basic() {

            var nums = Enumerable.Range(1, 5);

            //如果两个都能用，用For,因为CLR&C#中说For比ForEach快一点（为啥？）
            Parallel.For(0, 1000, i => DoWork(i));
            Parallel.ForEach(nums, i => DoWork(i));
            Parallel.Invoke(() => DoWork(1),
                () => DoWork(2),
                () => DoWork(3),
                () => DoWork(4),
                () => DoWork(5)
                );

            ParallelLoopResult result = Parallel.ForEach(nums, i => DoWork(i));
            
        }

        /**
         * for/foreach提供任务开始/任务执行/任务结束三个参数
         * 参考
         *   - C#7 and .net core  2.0 - chapter 21.2.3
         *     - 其例子中详细的打印出了对应3个delegate的线程id和taskid
         *   - 
         */
        [Test]
        public void testParallelFor_3delegates()
        {

            var nums = Enumerable.Range(1, 99);

            //ForEach<TSource, TLocal>
            //- TSource: 顾名思义，就是输入数据的类型
            //- TLocal是第一个LocalInit方法返回值。该值在整个上下文中使用
            //  - 之后，其被传入body方法中（最后的那个参数）
            //    - 特别注意：body中调用的方法也要返回该类型
            //  - 最后LocalFinally方法的输入参数类型就是它，值将一路传递下来
            //
            //- 特别注意： 该方法还有更多参数（譬如增加option），参考文档
            //
            //, , 

            //如何Cancel，看这连接https://docs.microsoft.com/en-us/dotnet/standard/parallel-programming/how-to-cancel-a-parallel-for-or-foreach-loop
            ParallelOptions options = new ParallelOptions();
            CancellationTokenSource cts = new CancellationTokenSource();
            options.CancellationToken = cts.Token;
            //cts.Cancel();外部线程执行这个就cancel了
            ParallelLoopResult result = Parallel.ForEach<Int32, Int32>(
                //IEnumerable<TSource> source
                nums,
                options, 
                //特别注意：每个线程运行一次
                //Func<TLocal> localInit - 输入值：empty，返回值TLocal的方法
                () => 
                {
                    ThreadEval_Util.TraceThreadAndTask("localInit");
                    return 0;
                },

                //Func<TSource, ParallelLoopState, long, TLocal, TLocal> body - 输入值：TSource, ParallelLoopState, long, TLocal，返回值：TLocal
                //最后一个输入参数：taskLocalTotal 是从localInit中传过来的，可以在body方法中更新它
                (i, loopState, index, taskLocalTotal) => {

                    //注意：stop/break都不会从当前方法返回，当前方法还会继续执行完
                    //但是新的iteration大概率不会继续开始，这是有Parallel框架来判断的
                    //Stop
                    ThreadEval_Util.TraceThreadAndTask($"004-body:{i}-{index} before stop");

                    //应该用if(options.CancellationToken.IsCancellationRequested)，外部调用
                    //可以使用token.ThrowIfCancellationRequested()， 不过Parallel外部要捕捉异常
                    //参考 https://stackoverflow.com/questions/8818203/what-is-difference-between-loopstate-break-loopstate-stop-and-cancellationt
                    //这里为了测试，直接比较i>10
                    if (i > 10) 
                    { 
                        loopState.Stop();
                        //loopState.Break();
                        ThreadEval_Util.TraceThreadAndTask($"003-body:{i}-{index} stop here");
                    }
                    ThreadEval_Util.TraceThreadAndTask($"003-body:{i}-{index} after stop");

                    Thread.Sleep(100);
                    
                    //loopState.Stop();
                    return DoWork(i);
                },

                //特别注意：每个线程运行一次
                //Action<TLocal> localFinally - 输入值：TLocal（即talksLocalTotal）， 返回值：empty
                //talksLocalTotal是从body中传过来的
                talksLocalTotal =>
                {
                    ThreadEval_Util.TraceThreadAndTask("localFinally");
                }
            );

            Console.WriteLine($"result.LowestBreakIteration:{result.LowestBreakIteration.ToString()}");
        }

        private Int32 DoWork(Int32 i) {
            return 1;
        }
    }
}
