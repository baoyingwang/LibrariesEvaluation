using System;
using System.Collections.Generic;
using NUnit.Framework;

namespace eval_csharp
{
    public class ListEval
    {
        public ListEval()
        {
        }


        [Test]
        public void evalList() {

            var names = new List<String> { "<name>", "Ana", "Felipe" };

            //测试添加重复元素以后，删除它，删除的是哪个?
            //查看Remove的方法解释，很清楚，删除第一次出现的拿个！
            names.Add("Ana");
            names.Remove("Ana");
            Assert.AreEqual("Ana", names[2]);
            Assert.AreEqual(3, names.Count);

            //IndexOf(element) - 查找这个元素在list中的第一次位置，找不到则返回-1
        }
    }
}
