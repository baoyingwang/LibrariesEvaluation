using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace eval_csharp
{
    class LinqEval_LazyLoading
    {


        /**
         * 这里给出了一个简化的lazy loading的例子
         * - 可以看到，获取到Enumerable(top2, tail1)之后，callTrace中还是空的
         * - 具体到遍历enumerator，也是需要才执行
         * - note： 可以通过ToArray()来避免lazy loading
         */
        [Test]
        public void testEnumerableLazyLoading_Simple()
        {

            var callTrace = new List<String>();

            //只有clubs和diamonds
            var top2 = (from s in Suits(callTrace) select new { Suit = s }).Take(2);
            //注意，由于默认lazy loading，实际上还没有执行任何东西，所以callTrace中是空的
            Assert.AreEqual(0, callTrace.Count);

            //这里开始真正的取值，所以接下来可以看到了callTrace中有东西了
            foreach (var x in top2)
            {
            }
            //注意：由于只需要前边2个值，所以callTrace size只有2.
            Assert.AreEqual(2, callTrace.Count);


            //新case
            callTrace.Clear();
            var tail1 = (from s in Suits(callTrace) select new { Suit = s }).Skip(3);
            //注意，由于默认lazy loading，实际上还没有执行任何东西，所以callTrace中是空的
            Assert.AreEqual(0, callTrace.Count);

            //这里开始真正的取值，所以接下来可以看到了callTrace中有东西了
            foreach (var x in tail1)
            {
            }
            //注意：虽然只需要tail的1个值，但是因为跳过前3个的过程也调用了到了
            Assert.AreEqual(4, callTrace.Count);

        }

        /**
         * 这个例子中可以看到：重复遍历同一个Enumerable，其都将Linq查询全部重新执行一遍
         * 由此可以看出：如果你的数据集要多次遍历的话，请使用eager evaluation，否则这么重复的evaluation效率不行
         */
        [Test]
        public void testEnumerableLazyLoading_Middle()
        {

            var callTrace = new List<String>();

            //这个变量的类型为
            //{System.Linq.Enumerable.SelectManySingleSelectorIterator<string, <>f__AnonymousType0<string, string>>}
            //string: 为？不知道呢
            //值类型：<>f__AnonymousType0<string, string> 表示new { Suit = s, Rank = r }
            //note：LogQuery是用来分析解决问题的
            var startingDeck = (from s in Suits(callTrace).LogQuery(callTrace, "Suit Generation")
                                from r in Ranks().LogQuery(callTrace, "Rank Generation")
                                select new { Suit = s, Rank = r }).LogQuery(callTrace, "Starting Deck");

            //只要注意一点：其并没有执行任何RankGeneration
            //具体执行细节解释如下：
            //其执行过程如下(参考https://stackoverflow.com/questions/49527642/understanding-lazy-evaluation-in-linq-in-c-sharp)总结而来
            var startingDeck_just_for_compare = 
                Suits(callTrace)                         // 直接返回一个IEnumerable<string> - without eval(所以，其并没有填充callTrace任何值）。 注意，其内部使用yield return。
                .LogQuery(callTrace, "Suit Generation")  // 该方法执行（因为内部没有使用yield return），并返回IEnumerable<string>（without eval）
                .SelectMany(suit =>                      // 直接返回IEnumerable<string> without evaluating it。这里面整体都没执行，因为其是依赖于suit的，而suit都没有load（TODO 不是特别好理解）
                                Ranks()                  
                                .LogQuery(callTrace, "Rank Generation")        
                                .Select(rank =>new { Suit = suit, Rank = rank })
                           )
                .LogQuery(callTrace, "Starting Deck");   // 该方法执行（因为内部没有使用yield return），并返回IEnumerable<string>（without eval）

            Assert.AreEqual(4, callTrace.Count);
            Assert.AreEqual("Suit Generation", callTrace[0]);
            Assert.AreEqual("Starting Deck", callTrace[1]);
            Assert.AreEqual("Suit Generation", callTrace[2]); //后两个是解释startingDeck_just_for_compare时候产生的
            Assert.AreEqual("Starting Deck", callTrace[3]);   //后两个是解释startingDeck_just_for_compare时候产生的

            //清空，重新计数
            callTrace.Clear();

            //注意：执行到这里，上面的Linq表达值还都没有进行Evaluation
            //      接下来的循环中将证明，每次循环都重新从数据源冲拉取数据。
            //**每次**循环：
            // - 把Suits中的值重新重新取出
            // - 针对每一个suit，要把下面方法执行一边
            //    - SelectMany(suit => 
            //    -                 Ranks()
            //    -                 .LogQuery(callTrace, "Rank Generation")
            //    -                 .Select(rank => new { Suit = suit, Rank = rank })
            //    -        )
            // 
            for (int i = 0; i < 8; i++) {
                var enumerator = startingDeck.GetEnumerator();
                while (enumerator.MoveNext()) { 
                }
            }
            Assert.AreEqual(32, callTrace.FindAll(s =>s.Equals("Rank Generation")).Count); //单次循环：4个suits，每个执行一遍SelectManay（每遍得到一个"Rank Generation"），即每次循环得到4个"Rank Generation"；8次循环则32个"Rank Generation"；
            Assert.AreEqual(8, callTrace.FindAll(s => s.Equals("yield return clubs")).Count);//单词循环会被suits全部获取一遍，则这四个花色每个+1；执行完8次之后，则各为8
            Assert.AreEqual(8, callTrace.FindAll(s => s.Equals("yield return diamonds")).Count);
            Assert.AreEqual(8, callTrace.FindAll(s => s.Equals("yield return hearts")).Count);
            Assert.AreEqual(8, callTrace.FindAll(s => s.Equals("yield return spades")).Count);

        }


        IEnumerable<string> Suits()
        {
            yield return "clubs"; //草花
            yield return "diamonds";//方片
            yield return "hearts"; //红桃
            yield return "spades"; //黑桃
        }

        IEnumerable<string> Suits(List<String> callTrace)
        {
            //注意，这个callTrace.Add要放在yiel return前边，否则调用不到了
            callTrace.Add("yield return clubs");
            yield return "clubs"; //草花

            callTrace.Add("yield return diamonds");
            yield return "diamonds";//方片

            callTrace.Add("yield return hearts");
            yield return "hearts"; //红桃

            callTrace.Add("yield return spades");
            yield return "spades"; //黑桃
        }

        IEnumerable<string> Ranks()
        {
            yield return "two";
            yield return "three";
            yield return "four";

            yield return "five";
            yield return "six";
            yield return "seven";
            yield return "eight";
            yield return "nine";
            yield return "ten";
            yield return "jack";
            yield return "queen";
            yield return "king";
            yield return "ace";

        }
    }

}
