using System;
using NUnit.Framework;

namespace eval_csharp
{
    public class NumEval
    {
        public NumEval()
        {
        }


        /**
         * 
         * 除法没有四舍五入，都是直接把小数部分舍弃掉
         * - 由于没有进位问题，负数也是直接砍掉小数部分
         * 
         */
        [Test]
        public void intDivide() {


            Assert.AreEqual(20, 4*5);
            Assert.AreEqual(1, 4/4);
            Assert.AreEqual(1, 5 / 4); //1.15 =》1
            Assert.AreEqual(1, 7 / 4); //1.75 =》 1

            Assert.AreEqual(20, 4 * 5);
            Assert.AreEqual(-1, -4 / 4);
            Assert.AreEqual(-1, -5 / 4); // -1.25 =》 -1
            Assert.AreEqual(-1, -7 / 4); //-1.75 =》 -1


        }

        /**
         * 
         * int.MaxValue + 1 != int.MaxValue, 而是一个负数了，就是说overflow了
         * note： python中有无穷大infinity = float("inf")， infinity + 1 == infinity
         * 
         */
        [Test]
        public void intBoundary()
        {
            int max = int.MaxValue;
            int min = int.MinValue;
            Assert.AreNotEqual(int.MaxValue, max + 1);
            Assert.AreNotEqual(int.MinValue, min - 1);

        }

        /**
         * 它也有IEEE754的浮点数表示问题
         * 0.9是一个比较典型的浮点数无法精确表达的数字之一
         */
        [Test]
        public void doubleVal()
        {
            double a = 1-0.1;
            Assert.AreEqual(0.9, a);

            //注意，别写成 1 / 3, 我第一回就写成1/3了
            double x = 1.0 / 3;
            Assert.IsTrue( Math.Abs(x - 0.33333333333333) < 0.0001);

            Assert.AreEqual(1.7976931348623157E+308, double.MaxValue);

        }

        /**
         * decimal表示的小数位比double更多
         * 但是整数部分更小
         * 
         */
        [Test]
        public void decimalVal()
        {
            //用M结尾表示这是一个decimal，不是double
            decimal a = 1M - 0.1M;
            Assert.AreEqual(0.9, a);

            decimal x = 1M / 3M;
            Assert.IsTrue(Math.Abs(x - 0.33333333333333M) < 0.0001M);

            //decimal的最大值(7.9e28)，比double小很多（1.7976931348623157E+308）
            Assert.AreEqual(792281625_1426433759_3543950335m, decimal.MaxValue);

        }

    }
}
