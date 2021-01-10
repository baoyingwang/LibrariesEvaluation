using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace eval_csharp
{
    class NullableValueTypeTest
    {

        /**
         * 这里是Nullable的语法，还要特别注意下面的testEasyNullCheck() which applys s?.Length, s?.toCharArray都挺好用
         */
        [Test]
        public void testNullableObject() {

            //Nullable值类型, 只能是值类型struct! 即：Nullable<T which is struct>

            //语法Nullable<T which is struct>.GetValueOrDefault()
            Int32? c = null;  //等价于 Nullable<Int32> c = null;
            Assert.AreEqual(null, c);
            Assert.IsFalse(c.HasValue);
            Assert.Throws<InvalidOperationException>(()=> {
                //c是null，直接拆箱不行，会报错. 
                //使用之前先判断是否有值c.HashValue, 或者使用GetValueOrDefault
                Int32 x = c.Value;
            });
            Assert.AreEqual(6, c.GetValueOrDefault(6));
            Assert.AreEqual(0, c.GetValueOrDefault()); 

            Int32? d = 7; //等价于 Nullable<Int32> d = 7;
            Assert.AreEqual(7, d.GetValueOrDefault(6));
            Assert.AreEqual(7, d);

            
            Nullable<Int32> a = 5;
            Int32? b = null; //Int32? b 这是Nullable<Int32> b的简写
            //这个是想告诉大家，这个奇葩的比较，因为b是null，还是能够进入到else那里
            if (a > b)
            {
                Console.WriteLine("a > b");
            }
            else
            {
                //判断走到这里
                Console.WriteLine("a < b");
            }
        }


        [Test]
        public void testEasyNullCheck() {

            //Quick and easy null checks
            //https://docs.microsoft.com/en-us/dotnet/csharp/tutorials/exploration/csharp-6?tutorial-step=6
            string s = null;
            //下面如果s为null，则返回null int
            int? sLength = s?.Length;
            Assert.IsNull(sLength);

            //下面这个要特别注意, 返回普通的reference了，普通reference的值是可以为null的
            char[] sCharArray = s?.ToCharArray();
            Assert.IsNull(sCharArray);

            
            //下面来个长的，因为一开始s就是null，所以最后得到的hashMore就是null object
            bool? hasMore = s?.ToCharArray()?.GetEnumerator()?.MoveNext();
            Assert.IsFalse(hasMore.HasValue);
            Assert.Throws<InvalidOperationException>(() => {
                //c是null，直接拆箱不行，会报错. 
                //使用之前先判断是否有值c.HashValue, 或者使用GetValueOrDefault
                var x = hasMore.Value;
            });

            //https://docs.microsoft.com/en-us/dotnet/csharp/language-reference/operators/member-access-operators#null-conditional-operators--and-
            //
            var a = new A();
            bool? aR = a?.f1?.Contains("x", StringComparison.OrdinalIgnoreCase);
            //不支持 if(bool?) 下面这样的判断
            //if (aR)
            //{ 
            //
            //}
            if (aR.HasValue && aR.Value) //不得不先判断HasValue, 否则直接拆箱在aR为null时候会有异常（见上面的test case)
            { 
            }

            A b = null;
            bool? bR = b?.f1?.Contains("x", StringComparison.OrdinalIgnoreCase); //后面的Contains不会执行，因为short curcuiting
            if (bR.HasValue && bR.Value) //不得不先判断HasValue, 否则直接拆箱在bR为null时候会有异常（见上面的test case)
            {
            }
        }

        class A
        {
            public string f1 { get; set; }
        }

        [Test]
        public void testTransitiveNull()
        {
            Person p = null;
            var a = p?.hands?.Select(x => x).DefaultIfEmpty();
            Assert.IsNull(a);
            a?.Select(x => x);

            //下面这个会出空指针错误
            Assert.Throws<NullReferenceException>(() => {
                //对null做foreach有空指针。这个是c#不太好的地方，java就没有这个问题。
                //https://stackoverflow.com/questions/6455311/is-ifitems-null-superfluous-before-foreacht-item-in-items
                //
                //btw：linq结果做Max()的话，如果前面选择结果为空集，也出错。所以用a.Select(...).DefaultIfEmpty().Max(), 
                //e.g. pointList.DefaultIfEmpty().Max(p => p == null ? 0 : p.X)
                //https://stackoverflow.com/questions/5047721/linq-max-with-nulls
                foreach (var x in a)
                {

                }
            });


            Person p2 = new Person();
            p2.hands = null;
            a = p2?.hands?.Select(x => x).DefaultIfEmpty();
            Assert.IsNull(a);

        
        }

        class Person {

            public IEnumerable<Hand> hands;
        }

        class Hand { 
        
        }

        

    }
}
