using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using NUnit.Framework;

namespace eval_csharp.LinqEval
{

    /**
     * 给在线代码增加一些注释https://docs.microsoft.com/en-us/dotnet/csharp/tutorials/working-with-linq
     *
     * 下面给了出counter=3的情况下的执行情况，基本解释的指数级增长情况
     * - 通过下面的主次调用关系，可以清楚的看到最后的调用次数的指数级增长
     * - 每次调用(2.4, 3.4, 4.4)自身的循环次数也会变多（不考虑lazy情况下），因为随着shuffle的进行，越来越多的heading元素相同，需要比较多次才能发现不同
     * - 重点解释2.4
     *   - 其同top，bottom两个，达到的翻倍的效果
     *   - 最终调用1.3的时候，每次执行都要把1.2调用一遍，这个是要了命的地方之一
     *   - BTW：去看LinqLazyLoadingEval.testEnumerableLazyLoading_Middle()中的例子可以发现
     *     - 即使不是这种指数级的增长，每次遍历1.3， 都会导致1.2重新执行一遍
     *     - 也就是是即使普通的重新遍历，都会线性增长
     * 1. 1.1 suit - lazy                      .Suits() - 1.1
     *    1.2 rank - lazy                      .SelectMany(suit =>Ranks().LogQuery("Rank Generation").Select(rank =>new { Suit = suit, Rank = rank } 1.2+1.3
     *    1.3 combine 1.1 and 1.2 - lazy
     *    
     * 2. 2.1 top    of 1.3 - lazy
     *    2.2 bottom of 1.3 - lazy
     *    2.3 interleave of 2.1 and 2.2 - lazy
     *    2.4 compare 1.3 and 2.3 这里通过2.4=>2.3=>2.1, 2.2 => 1.3 => 1.1, 1.2 的调用链，执行rank的次数×2
     * 
     * 3. 3.1 top    of 2.3 - lazy
     *    3.2 bottom of 2.3 - lazy
     *    3.3 interleave of 3.1 and 3.2 - lazy
     *    3.4 compare 1.3 and 3.3 这里最后指向上一层 3.4=>3.3=>3.1,3.2=>2.3 调用再次次数×2
     *    
     * 4. 4.1 top    of 3.3 - lazy
     *    4.2 bottom of 3.3 - lazy
     *    4.3 interleave of 4.1 and 4.2 - lazy
     *    4.4 compare 1.3 and 4.3 这里最后指向上一层 4.4=>4.3=>4.1,4.2=>3.3 调用再次次数×2
     */
    public class LinqEval_Advance
    {
        public LinqEval_Advance()
        {
        }

        static IEnumerable<string> Suits()
        {
            yield return "clubs"; //草花
            yield return "diamonds";//方片
            yield return "hearts"; //红桃
            yield return "spades"; //黑桃
        }

        static IEnumerable<string> Ranks()
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


        [Test]
        public void evalLinq_InDepth()
        {
            //这个变量的类型为
            //{System.Linq.Enumerable.SelectManySingleSelectorIterator<string, <>f__AnonymousType0<string, string>>}
            //string: 为？不知道呢
            //值类型：<>f__AnonymousType0<string, string> 表示new { Suit = s, Rank = r }
            //note：LogQuery是用来分析解决问题的
            var startingDeck = (from s in Suits().LogQuery("Suit Generation")
                                from r in Ranks().LogQuery("Rank Generation")
                                select new { Suit = s, Rank = r }).LogQuery("Starting Deck");

            //下面(SelectMany + Select)与上面的语法一样，不过是方法形式的
            //SelectMany与Select相比，多了一个flatten的行为。
            //因为不flatten的话，得到的结果是两层的（第一层为每个suite，然后每个suit有13个ranks）
            //var startingDeck_just_for_compare = Suits().LogQuery("Suit Generation")
            //    .SelectMany(suit =>
            //                    Ranks().LogQuery("Rank Generation")
            //                           .Select(rank =>
            //                                    new { Suit = suit, Rank = rank }
            //                                  )
            //               );


            var counter = 0;
            var shuffle = startingDeck;
            do
            {

                //8次，而且超快
                //top + bottom, 所以8次；因为只有8次，所以超快
                //bottom + top, 则需要52次；因为52次，而且越往后调用Rank Generation越多（指数级增加），所以很慢。其开始8次也很快。
                var top = shuffle.Take(26).LogQuery("Top Half");
                var bottom = shuffle.Skip(26).LogQuery("Bottom Half");

                //由于是lazy loading，这个InterleaveSequenceWith并没有执行。打上断点会发现，这时候根本没进去
                //WARN:只要这个方法中是通过yield return来返回值的，就进不去。如果类似于LogQuery那样的返回this，就能进去！！！
                //     感觉像发现了宝藏一样233
                shuffle = top.InterleaveSequenceWith(bottom);
                EnumerableExtension.LogQuery("counter:" + counter.ToString() + " done");
                counter++;

                //52次，而且超级超级慢，越往后越慢, 区别就是先执行Skip再执行Take
                //后面加上ToArray()来解决这个问题（变lazy evaluatino为eager evaluation）
                //- 越来越慢是因为lazy evaluation导致了指数级别的获取：

                //下面这个SequenceEquals(...)方法是真正执行获取值的地方（上面的InterleaveSequenceWith其实也获取值比较了，但是C#认为那是内部的方法不执行）
            } while (!startingDeck.LogQuery("startingDeck").SequenceEquals(shuffle.LogQuery("shuffle")));

            //这个8次不是这次eval的重点
            //重点是理解整个过程
            Assert.AreEqual(8, counter);

        }
    }

    //按照visual studio同时，这样的静态扩展要放在一个static + non-generic类中
    //Q： static类啥意思？
    //A： 这样的类，其方法全部为static；你也无法new一个实例出来
    static class tmp20200713_1251
    {
        //这个是C#的神奇的静态扩展方法，直接把IEnumerable类型给增加了一个新的方法：InterleaveSequenceWith
        //这个新方法的参数是：IEnumerable<T> second
        public static IEnumerable<T> InterleaveSequenceWith<T>(this IEnumerable<T> first, IEnumerable<T> second)
        {
            EnumerableExtension.LogQuery("InterleaveSequenceWith enter");
            //这个把两个ENumerable进行混编的方式也挺简洁
            //注意：如果两个数量不一致，会导致丢失。不过本例子中不会出现两个数量不一致情况
            var firstIter = first.GetEnumerator();
            var secondIter = second.GetEnumerator();
            while (firstIter.MoveNext() && secondIter.MoveNext())
            {
                EnumerableExtension.LogQuery($"InterleaveSequenceWith running- {firstIter.Current}, {secondIter.Current}");
                yield return firstIter.Current;
                yield return secondIter.Current;
            }

            //WARN：这是一行永远执行不到的代码,因为yield return之后方法就返回了，所以永远无法到达这个位置
            //试过了，放在finally block中也不执行
            EnumerableExtension.LogQuery("InterleaveSequenceWith exit");
        }

        public static bool SequenceEquals<T>(this IEnumerable<T> first, IEnumerable<T> second)
        {

            EnumerableExtension.LogQuery("SequenceEquals enter");

            var firstIter = first.GetEnumerator();
            var secondIter = second.GetEnumerator();

            while (firstIter.MoveNext() && secondIter.MoveNext())
            {

                //Q: Equal比较的时候，是如何完成的？因为我没有自定义Equal方法,我怀疑Equals能否正确的比较（按照业务语义进行比较）
                //A: 这是Anonymous类型，c#将对每个property都进行比较
                //注意：不用使用==（而要使用Equals），因为==操作符没有重载
                //这个链接做了简要解释：https://stackoverflow.com/questions/23703846/equality-for-anonymous-types
                //这个链接做了详尽解释：https://docs.microsoft.com/en-us/dotnet/csharp/programming-guide/classes-and-structs/anonymous-types
                //匿名类基本用在Linq中，譬如new { prod.Color, prod.Price };
                //这可以这样var anonArray = new[] { new { name = "apple", diam = 4 }, new { name = "grape", diam = 1 }};
                if (!firstIter.Current.Equals(secondIter.Current))
                {
                    EnumerableExtension.LogQuery("SequenceEquals exit - false");
                    return false;
                }

            }

            EnumerableExtension.LogQuery("SequenceEquals exit - true");
            return true;

        }

    }
}
