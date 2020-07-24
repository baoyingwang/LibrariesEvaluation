using System;
using NUnit.Framework;

namespace eval_csharp
{
    public class GrammarEval
    {

        
        public GrammarEval()
        {
        }

        /**
         * 这个方法只是作为一个place holder
         * 1. const是静态的，编译时候值必须给定
         * 2. readonly是运行时给值的(只能在构造器中赋值，编译器会提醒），给完之后不能再改了
         *   - 类似于java中的final
         * 特别注意：如下面链接中所讲，const类型对外发布之后，client端将把这个值hardcode到本地代码中。
         *   - 即：如果更新了const相关模块（conts值做了更改），client端代码也要重新编译才行！
         *   - 所以，如果你的变量后边可能会变化，则声明为readonly更合适一些
         * https://stackoverflow.com/questions/55984/what-is-the-difference-between-const-and-readonly-in-c
         * 
         */
        readonly String ro_v1;
        [Test]
        public void const_vs_readonly() {
            //看上面注释，for more how to use
            //ro_v1 = "abc"; 编译器提示错误（只能在constructor中赋值）
        }


        //另外还有nullableObject, e.g. int? a = null; int?b =5; 参考NullableValueTypeTest.cs
        [Test]
        public void question_mark() {

            //x = a??b  如果a不为null则x=a；如果a为null，则x=b；可以认为是x = a ?? defaultValue_if_a_is_Null
            //等价于 x = a!=null?a:b
            //也等价于
            //if    a=!null: x = a
            //else           x = b


            String strNULL = null;
            Assert.AreEqual("abc", strNULL ?? "abc"); //如果strNULL 是 null的话，后面的默认值

            String strNotNULL = "xyz";
            Assert.AreEqual("xyz", strNotNULL ?? "abc"); //如果strNULL 是 null的话，后面的默认值        
        }

        /**
         * - 针对int类型的overflow，可以设定是否抛出overflow异常（默认不抛出），编译器能够识别一下（见下面link）
         * - 可以通过3中方式设置这种检查
         *   - compiler options           - ？什么参数？如何工作？
         *   - environment configuration  - ？什么参数？如何工作？
         *   - use of the checked keyword - 这里讲的是这个
         * 这个官方文档中有足够的例子，相当清楚，我不必再写一些代码
         * https://docs.microsoft.com/en-us/dotnet/csharp/language-reference/keywords/checked
         * 
         * 特别注意：double和float不会抛出overflow exception，即使使用了checked
         * 特别注意：decimal会抛出overflow exception，行为类似于int
         * 
         */
        [Test]
        public void checked_vs_unchecked_int_overflow() {

            //compiler error
            //_ = 2147483647 + 10;

            unchecked {
                //compiler ok
                _ = 2147483647 + 10;
            }

            //compiler ok, but when running, it will NOT throw exception because default behavior is not throwing
            {
                int ten = 10;
                int i2 = 2147483647 + ten;
            }

            //checked block
            Assert.Throws<OverflowException>(() => {
                checked
                {
                    int ten = 10;
                    int i2 = 2147483647 + ten;
                }
            });

            //checked bracket
            Assert.Throws<OverflowException>(() => {
                int ten = 10;
                int i2 = checked(2147483647 + ten);
            });


            //decimal与int类似都有overflow 的行为
            Assert.Throws<OverflowException>(() => {
                int ten = 10;
                decimal decimal1 = checked(decimal.MaxValue + ten);
            });


            //特别注意：double和float不会抛出overflow exception，即使使用了checked
            //下面都overflow了，即使用了checked也抛异常
            Assert.DoesNotThrow(() => {
                float floatMaxDouble = checked(float.MaxValue + float.MaxValue);
                float floatMaxPlus100 = checked(float.MaxValue + 100);

                //float.MaxVale:3.4028235E+38,
                //floatMaxDouble: Infinity,
                //floatMaxPlus100: 3.4028235E+38
                Console.WriteLine($"float.MaxVale:{float.MaxValue}" +
                    $",floatMaxDouble:{floatMaxDouble}" +
                    $",floatMaxPlus100:{floatMaxPlus100}");

                double doubleMaxDouble = checked(double.MaxValue + double.MaxValue);
                double doubleMaxPlus100 = checked(double.MaxValue + 100);

                //double.MaxVale:1.7976931348623157E+308,
                //doubleMaxDouble: Infinity,
                //doubleMaxPlus100: 1.7976931348623157E+308
                Console.WriteLine($"double.MaxVale:{double.MaxValue}" +
                    $",doubleMaxDouble:{doubleMaxDouble}" +
                    $",doubleMaxPlus100:{doubleMaxPlus100}");
            });

        }
    }
}
