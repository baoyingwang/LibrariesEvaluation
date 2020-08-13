using System;
using System.Collections.Generic;
using NUnit.Framework;

namespace eval_csharp
{
    public class Loop
    {
        public Loop()
        {
        }


        /**
         * 
         * 在微软的官方文档其实已经非常清楚了，见https://docs.microsoft.com/en-us/dotnet/csharp/iterators
         * 这里只是简要的说一下有这样的语法
         * 1. iterator 通过foreach 访问
         * 2. 可以通过yield return生成结果
         * 
         * 注意：如文中（online）所讲，如果对应类型是Disposable，其翻译后的代码将在final中执行Dispose
         * 
         */
        [Test]
        public void testGetSingleDigitNumbers() {
            GetSingleDigitNumbers();
        }

        //下面方法的用以是返回一个列表，值为：0,1,2,3,4,5,6,7,8,9,50,100, 101, 102, 103, 104, 105, 106, 107, 108, 109 
        //这个列表的用意是说，其返回值是动态的。你可以添加更多逻辑，使得在不同的情况下返回不同的list值
        public IEnumerable<int> GetSingleDigitNumbers()
        {
            //copy directly from https://docs.microsoft.com/en-us/dotnet/csharp/iterators
            //从中可以看出，我们可以在任何时候调用yield return，其将作为结果中的一条
            int index = 0;
            while (index < 10)
                yield return index++;

            yield return 50;

            var items = new int[] { 100, 101, 102, 103, 104, 105, 106, 107, 108, 109 };
            foreach (var item in items)
                yield return item;
        }

        /**
         * 有些时候，要返回一个空的Inumerable，这里是一个例子
         */
        [Test]
        public void testEmptyEnumerable() {
            IEnumerable<Int32> emptyOne = new Int32[0];

            foreach (var x in emptyOne) {
                //如果到达这里就fail了
                Assert.Fail();
            }
        }
    }
}
