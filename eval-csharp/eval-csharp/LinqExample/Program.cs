using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace eval_csharp.LinqExample
{

    /**
     * 给在线代码增加一些注释https://docs.microsoft.com/en-us/dotnet/csharp/tutorials/working-with-linq
     * 
     * 
     */
    public class Program
    {
        public Program()
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



        public static void Main(string[] args)
        {
            //这个变量的类型为
            //{System.Linq.Enumerable.SelectManySingleSelectorIterator<string, <>f__AnonymousType0<string, string>>}
            //string: 为？不知道呢
            //值类型：<>f__AnonymousType0<string, string> 表示new { Suit = s, Rank = r }
            //note：LogQuery是用来分析解决问题的
            var startingDeck = (from s in Suits().LogQuery("Suit Generation")
                                from r in Ranks().LogQuery("Rank Generation")
                                select new { Suit = s, Rank = r }).LogQuery("Starting Deck");
                                //.ToArray(); 


            //下面(SelectMany + Select)与上面的语法一样，不过是方法形式的
            //SelectMany与Select相比，多了一个flatten的行为。
            //因为不flatten的话，得到的结果是跨层次的
            var startingDeck_just_for_compare = Suits()
                .SelectMany(suit =>
                                Ranks().Select(rank =>
                                                new { Suit = suit, Rank = rank }
                                              )
                           );

            //下面(Select + Select)没有进行flattern，导致结果不对了
            //其将是长度为4的Enumerable：代表四种花色(suit)
            //每种花色下面有13个元素：当前花色对应的Rank
            //
            //这个变量的类型为：
            //{System.Linq.Enumerable.SelectEnumerableIterator<string, System.Collections.Generic.IEnumerable<<>f__AnonymousType0<string, string>>>}
            //string：
            //值类型：System.Collections.Generic.IEnumerable<<>f__AnonymousType0<string, string>>
            //       这个值类型为一个新的Enumerable，即上面说的每个suit对应的13个元素就在这里
            var startingDeck_no_flattern = Suits()
                .Select(suit =>
                    Ranks().Select(rank =>
                                    new { Suit = suit, Rank = rank }
                                  )
               );

            var counter = 0;
            var shuffle = startingDeck;
            do
            {

                //var top = shuffle.Take(26);
                //var bottom = shuffle.Skip(26);
                ////通过神奇的自定义静态方法扩展，完成top/bottom的shuffle
                //shuffle = top.InterleaveSequenceWith(bottom);

                //8次，而且超快
                var shuffleTakeSkip = shuffle.Take(26)
                            .InterleaveSequenceWith(shuffle.Skip(26));

                //52次，而且超级超级慢，越往后越慢, 区别就是先执行Skip再执行Take
                //后面加上ToArray()来解决这个问题（变lazy evaluatino为eager evaluation）
                //- 越来越慢是因为lazy evaluation导致了指数级别的获取：
                //  - 这里根据生成代码做了仔细解释，不过我还没有完全明白
                //  - https://stackoverflow.com/questions/49527642/understanding-lazy-evaluation-in-linq-in-c-sharp
                var shuffleSkipTake = shuffle.Skip(26).LogQuery("Bottom Half")
                            .InterleaveSequenceWith(shuffle.Take(26).LogQuery("Top Half"))
                            .LogQuery("Shuffle");

                //加上ToArray()以后，每次我都直接拿到了完整的值（可以称之为Eager Evaluation）
                //这样就没有上面lazy evaluation问题了（因为都已经evaluate完了）
                var shuffleSkipTakeToArray = shuffle.Skip(26).LogQuery("Bottom Half")
                            .InterleaveSequenceWith(shuffle.Take(26).LogQuery("Top Half"))
                            .LogQuery("Shuffle")
                            .ToArray();

                shuffle = shuffleTakeSkip;

                counter++;
            } while (!startingDeck.SequenceEquals(shuffle));

            Console.WriteLine($"after {counter} shuttle, it is same between top and bottom");

        }
    }

    //按照visual studio同时，这样的静态扩展要放在一个static + non-generic类中
    //Q： static类啥意思？
    //A： 这样的类，其方法全部为static；你也无法new一个实例出来
    public static class tmp1 {
        //这个是C#的神奇的静态扩展方法
        //直接把IEnumerable类型给增加了一个新的方法：InterleaveSequenceWith
        //这个新方法的参数是：IEnumerable<T> second
        public static IEnumerable<T> InterleaveSequenceWith<T>(this IEnumerable<T> first, IEnumerable<T> second)
        {
            //这个把两个ENumerable进行混编的方式也停简洁
            //注意：如果两个数量不一致，会导致丢失。不过本例子中不会出现两个数量不一致情况
            var firstIter = first.GetEnumerator();
            var secondIter = second.GetEnumerator();
            while (firstIter.MoveNext() && secondIter.MoveNext())
            {
                yield return firstIter.Current;
                yield return secondIter.Current;
            }
        }

        public static bool SequenceEquals<T>(this IEnumerable<T> first, IEnumerable<T> second)
        {

            var firstIter = first.GetEnumerator();
            var secondIter = second.GetEnumerator();

            while (firstIter.MoveNext() && secondIter.MoveNext()) {

                //Q: Equal比较的时候，是如何完成的？因为我没有自定义Equal方法,我怀疑Equals能否正确的比较（按照业务语义进行比较）
                //A: 这是Anonymous类型，c#将对每个property都进行比较
                //注意：不用使用==（而要使用Equals），因为==操作符没有重载
                //这个链接做了简要解释：https://stackoverflow.com/questions/23703846/equality-for-anonymous-types
                //这个链接做了详尽解释：https://docs.microsoft.com/en-us/dotnet/csharp/programming-guide/classes-and-structs/anonymous-types
                //匿名类基本用在Linq中，譬如new { prod.Color, prod.Price };
                //这可以这样var anonArray = new[] { new { name = "apple", diam = 4 }, new { name = "grape", diam = 1 }};
                if (!firstIter.Current.Equals(secondIter.Current)) {
                    return false;
                }

            }

            return true;

        }

        public static IEnumerable<T> LogQuery<T>(this IEnumerable<T> sequence, string tag)
        {
            // File.AppendText creates a new file if the file doesn't exist.
            using (var writer = File.AppendText("debug_.ToArray.log"))
            {
                writer.WriteLine($"{DateTime.Now} Executing Query {tag}");
            }

            return sequence;
        }
    }
}
