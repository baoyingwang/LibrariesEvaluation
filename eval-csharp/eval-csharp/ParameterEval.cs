using NUnit.Framework;
using System;


namespace eval_csharp
{
    class ParameterEval
    {

        class PointC
        {
            public String s;
        }

        /**
         * - out 参数可以是值类型或者class类型，而且可以不必提前new
         *   - 但是在函数内部必须new！
         *   - out关键在方法声明和方法调用时候全部使用
         * 
         */
        [Test]
        public void evalOut() {

            PointC  p1;
            outParamTest(out p1);
            Assert.AreEqual("hello", p1.s);


            PointC p2 = new PointC(); //这里是否new不重要
            outParamTest(out p2);
            Assert.AreEqual("hello", p2.s);
        }

        static void outParamTest(out PointC p)
        {
            //这个new是必须的，否则visual studio都会鄙视报错！
            //这就是out的限制之一
            p = new PointC();

            p.s = "hello";
        }
    }
}
