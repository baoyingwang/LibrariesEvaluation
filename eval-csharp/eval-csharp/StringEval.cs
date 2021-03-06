﻿using System;
using System.Linq;
using System.Text.RegularExpressions;
using NUnit.Framework;

namespace eval_csharp
{
    public class StringEval
    {
        public StringEval()
        {
        }

        /**
         * 
         * - 字符串前面加一个$符号，则里面的括号将替代变量。括号中的变量需要提前定义.
         * - 括号里面用方也行，譬如{var1.ToUppper()}
         * 
         * - 这里还有一些更加花哨的用法
         *   - https://docs.microsoft.com/en-us/dotnet/csharp/tutorials/exploration/csharp-6?tutorial-step=5
         * 
         */
        [Test]
        public void replaceVarInString() {

            String var1 = "baoying";
            String varReplace = $"abc-{var1}";
            String varPropertyReplace = $"abc-{var1.Length}";
            Assert.AreEqual("abc-baoying", varReplace);
            Assert.AreEqual("abc-7", varPropertyReplace);


            Assert.AreEqual("abc-BAOYING", $"abc-{var1.ToUpper()}");
        }

        /**
         * 
         * 这个没啥，就是trim start/end/default-both start and end
         * 
         */
        [Test]
        public void trim() {

            string greeting = "      Hello World!       ";
            Assert.AreEqual("[      Hello World!       ]", $"[{greeting}]");


            string trimmedGreeting = greeting.TrimStart();
            Assert.AreEqual("Hello World!       ", trimmedGreeting);

            trimmedGreeting = greeting.TrimEnd();
            Assert.AreEqual("      Hello World!", trimmedGreeting);

            trimmedGreeting = greeting.Trim();
            Assert.AreEqual("Hello World!", trimmedGreeting);
        }

        /**
         * 
         * 一些经常使用的方法
         * 
         */
        [Test]
        public void frequentUseMethods()
        {

            string greeting = "Hello World!";

            //Replace
            Assert.AreEqual("Good World!", greeting.Replace("Hello", "Good"));

            //Contains
            Assert.IsTrue(greeting.Contains("World"));

            //StartsWith / EndsWith
            Assert.IsTrue(greeting.StartsWith("Hello"));
            Assert.IsFalse(greeting.StartsWith("Hello1"));
            Assert.IsTrue(greeting.EndsWith("d!"));
            Assert.IsFalse(greeting.EndsWith("d!0"));
        }

        //就是以@开头的字符串，其里面的字符不进行escape
        //譬如 @"abc\n123", 其中的"\n"就是俩字符
        [Test]
        public void testVerbatim() {
            //https://www.c-sharpcorner.com/uploadfile/hirendra_singh/verbatim-strings-in-C-Sharp-use-of-symbol-in-string-literals/

            String verbatim = @"abc\n123";
            Assert.AreEqual('\\', verbatim[3]);
            Assert.AreEqual('n',verbatim[4]);

        }

        [Test]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Globalization", "CA1305:Specify IFormatProvider", Justification = "<Pending>")]
        public void testPattern()
        {
            Assert.AreEqual(9, this.Parse("AzSet1;IDF9"));
            Assert.AreEqual(9, this.Parse("IDF9"));
        }

        [System.Diagnostics.CodeAnalysis.SuppressMessage("Globalization", "CA1305:Specify IFormatProvider", Justification = "<Pending>")]
        private int Parse(string tag)
        {
            var tagTokens = tag.Split(new char[] { ',', ';' }, StringSplitOptions.RemoveEmptyEntries).Select(token => token.Trim());

            Regex IdfRegex = new Regex(@"IDF(\d+)", RegexOptions.Compiled | RegexOptions.IgnoreCase);
            var colo = 0;
            foreach (var tagItem in tagTokens)
            {
                var idfMatch = IdfRegex.Match(tagItem);

                if (idfMatch.Success)
                {
                    colo = int.Parse(idfMatch.Groups[1].Value);
                }
            }
            return colo;
        }

        [Test]
        public void testPattern02()
        {
            var regex = new Regex("oob01.*stb", RegexOptions.Compiled | RegexOptions.IgnoreCase);
            Assert.IsTrue(regex.IsMatch("oob01.stb"));


            regex = new Regex("-..M3$", RegexOptions.Compiled | RegexOptions.IgnoreCase);
            Assert.IsFalse(regex.IsMatch("abcd1234M3"));
            Assert.IsTrue(regex.IsMatch("-12M3"));
            Assert.IsTrue(regex.IsMatch("102-12M3"));

            regex = new Regex(".*M3$", RegexOptions.Compiled | RegexOptions.IgnoreCase);
            Assert.IsTrue(regex.IsMatch("abcd1234M3"));

            Assert.IsTrue(Regex.IsMatch("STO-0100-0001-02M3", "STO-0100-0001-02M3", RegexOptions.IgnoreCase));
            Assert.IsTrue(Regex.IsMatch("STO-0100-0001-02M3", "STO-0100-000[1,2]-02M3", RegexOptions.IgnoreCase));



        }
    }
}
