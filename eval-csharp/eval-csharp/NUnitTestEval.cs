using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
/**
 * 
 * https://docs.microsoft.com/en-us/dotnet/core/testing/unit-testing-with-nunit
 * 
* 第一个c#测试类，做了一些minor但是有趣的配置
* 1. 使用的nunit，使用nunit是因为我参考的quickfixn用的它。
*    note： 微软还有一个自己默认的测试框架
* 
* 问题：增加nunit的依赖（nunit and nunit test adapter)。但是增加依赖以后，在TestExplorer中点击运行测试也不运行，也不报错
*      btw：在mac的visual studio中，找不到怎么把Test　Explorer窗口弄出来
* 过程：网上搜索一圈，没有找到针对性的文章。大体上就是两种，
* 1）添加测试代码，在test explorer中运行即可
* 2）创建一个测试project，然后再test explorer中运行即可
*    我不想创建一个新的测试project，因为我这个本身就是用于测试的，可能可以调整这个项目的设置。而且，我觉得不应该这么复杂。
* 解决：在增加依赖那里，把我看到的MS的测试依赖都加上了，Microsfot.NET.Test.SDK, Microsoft.TestPlatform, MSTest.TestAdapter
*      然后TestExplorer中就可以运行测试了。
*      
* btw：测试运行的控制台找不到，想看一下Console.WriteLine(..)也不行，也好，死心了只用Assert。但是有时候Console的一些输出有帮助
*      可能需要配置一些东西，或者用什么秘籍吧
*      参考：https://stackoverflow.com/questions/11209639/can-i-write-into-console-in-a-unit-test-if-yes-why-doesnt-the-console-window
*
* 
*/
namespace eval_csharp
{
    //The [TestFixture] attribute denotes a class that contains unit tests. The [Test] attribute indicates a method is a test method.
    [TestFixture] 
    class NUnitTestEval
    {

        List<String> callTrace;

        //每个子case执行前都执行一遍Setup
        [SetUp]
        public void SetUp()
        {
            Console.WriteLine("setup");
            callTrace = new List<String>();
        }

        //每个子case执行前都执行一遍TearDown
        [TearDown]
        public void TearDown()
        {
            Console.WriteLine("teardown");
            callTrace.Clear();

        }

        [Test]
        public void test1() {

            Assert.AreEqual("Console 1 - 1", "Console 1 - 1");
        }

        //xunit的theory类似，可以多个input，对应多个result
        [TestCase(2.1, ExpectedResult = 2)]
        [TestCase(5.6, ExpectedResult = 6)]
        [TestCase(-1.2, ExpectedResult = -1)]
        public int testRound(double input)
        {
            return (int)Math.Round(input);
        }

        [Test]
        public void testExceptionInternal() {

            //这个Assert方式学习自 https://stackoverflow.com/questions/3407765/nunit-expected-exceptions
            //  - 还有一个方式在这个Test方法上面增加[ExpectedException("System.AggregateException")]
            //这个语法是期望someMethod()抛出Exception
            Assert.Throws<NullReferenceException>(() => someMethodWtihNullReferenceException());
        }

        void someMethodWtihNullReferenceException() {

            throw new NullReferenceException();
        }
    }
}
