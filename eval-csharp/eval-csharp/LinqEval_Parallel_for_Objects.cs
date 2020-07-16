using Chapter11.Model;
using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace eval_csharp
{
    /**
     * Parallel Linq 只有Ling to Objects才支持这种类型
     * 各个资料的篇幅都不长，而且介绍不是那么清楚。
     * 使用的时候再仔细看吧（if required）
     * 
     * 资料
     * - C#Indepth 3rd CN chapter 12.4 
     * - CLR&CLR 4th 27.7 PLINQ
     * 
     */
    class LinqEval_Parallel_for_Objects
    {


        /**
         * 例子来自于 C#Indepth 3rd CN chapter 12.4 
         * 
         */
        [Test]
        public void testWhereBasic()
        {
            //普通调用
            var queryCommon = from row in Enumerable.Range(0, 100)
                        from column in Enumerable.Range(0, 100)
                        select ComputeIndex(row, column);

            //增加了AsParallel()
            //注意，这样的调用，返回结果的顺序与普通调用可能不同
            var queryParallel = from row in Enumerable.Range(0, 10).AsParallel()
                        from column in Enumerable.Range(0, 100)
                        select ComputeIndex(row, column);

            //增加.AsOrdered()来保证顺序
            var queryParallelOrdered = from row in Enumerable.Range(0, 10).AsParallel().AsOrdered()
                                from column in Enumerable.Range(0, 100)
                                select ComputeIndex(row, column);

            //还有一些其他的参数，如WithCancellation等等，需要的时候再查吧1

        }

        private Int32 ComputeIndex(Int32 row, Int32 column) {
            Thread.Sleep(1000);
            return row + column;
        }
    }
}
