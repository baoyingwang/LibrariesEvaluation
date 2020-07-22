using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using Xunit;

namespace eval_csharp
{
    /**
     * https://docs.microsoft.com/en-us/dotnet/core/testing/unit-testing-with-dotnet-test
     * https://docs.microsoft.com/en-us/dotnet/core/testing/unit-testing-best-practices
     * 
     * 问题：暂时无法把本类中的测试加入到TestExplorer中（这里目前仅有NUnit测试case）。可能是需要配置。
     *   - 可能原因是当前项目创建时候并没有作为测试项目创建，而且当前项目中至少包含了两种测试黄建nunit（先添加）和xunit（后添加），我之后可能还要添加MSTest框架（就是为了熟悉其语法）。
     * 
     */
    class XUnitTestEval
    {

        internal class StringCalculator {

            public Int32 Add(String strNums) {

                Int32 result = 0;
                String[] strNumsArray = strNums.Split(",");
                foreach (String strNum in strNumsArray) {
                    result += Int32.Parse(strNum);
                }

                return result;
            }
        }
        // [Fact] 无参数的xunit测试case
        //- The [Fact] attribute is used by the xUnit.net test runner to identify a 'normal' unit test: a test method that takes no method arguments. 
        //  - https://stackoverflow.com/questions/22373258/difference-between-fact-and-theory-xunit-net
        [Fact] 
        public void Test_Single()
        {
            var stringCalculator = new StringCalculator();
            var actual = stringCalculator.Add("0");
            Assert.Equals(0, actual);
        }

        //[Teory] 直接通过annotation传入参数
        //-The [Theory] attribute, on the other, expects one or more DataAttribute instances to supply the values for a Parameterized Test's method arguments.
        //  - https://stackoverflow.com/questions/22373258/difference-between-fact-and-theory-xunit-net
        [Theory]
        [InlineData("0,0,0", 0)]
        [InlineData("0,1,2", 3)]
        [InlineData("1,2,3", 6)]
        public void Add_MultipleNumbers_ReturnsSumOfNumbers(string input, int expected)
        {
            var stringCalculator = new StringCalculator();

            var actual = stringCalculator.Add(input);

            Assert.Equals(expected, actual);
        }
    }
}
