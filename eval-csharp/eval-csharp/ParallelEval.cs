using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace eval_csharp
{
    /**
     * ParallelLinq不在这里，而是在LinqEval_Parallel_for_Objects中
     * 
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
        }

        /**
         * for/foreach提供任务开始/任务执行/任务结束三个参数
         * 
         */
        [Test]
        public void testParallelFor_3delegates()
        {

            var nums = Enumerable.Range(1, 5);

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
            Parallel.ForEach<Int32, Int32>(
                //IEnumerable<TSource> source
                nums,

                //特别注意：每个人物
                //Func<TLocal> localInit - 输入值：empty，返回值TLocal的方法
                () => 
                {
                    return 0;
                },

                //Func<TSource, ParallelLoopState, long, TLocal, TLocal> body - 输入值：TSource, ParallelLoopState, long, TLocal，返回值：TLocal
                //最后一个输入参数：taskLocalTotal 是从localInit中传过来的，可以在body方法中更新它
                (i, loopState, index, taskLocalTotal) => {
                    return DoWork(i);
                },

                //特别注意：每个任务开始的时候调用一次
                //Action<TLocal> localFinally - 输入值：TLocal（即talksLocalTotal）， 返回值：empty
                //talksLocalTotal是从body中传过来的
                talksLocalTotal =>
                {

                }
            );
        }

        private Int32 DoWork(Int32 i) {
            return 1;
        }
    }
}
