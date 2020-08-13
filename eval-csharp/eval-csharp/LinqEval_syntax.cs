using NUnit.Framework;
using System.Linq;
using System;
using System.Collections.Generic;
using System.Collections;
using Chapter11.Model;
using System.Linq.Expressions;

namespace eval_csharp
{
    /**
     * 
     * 本例子的数据和代码，大部分来自CSharp in depth 3rd CN；小部分我自己编的
     * 
     * LinqEval_LazyLoading_Advance中关于Linq中匿名类的比较/过滤做了一个判断，也写在这里因为其比较通用
     * Q: Equal比较的时候，是如何完成的？因为我没有自定义Equal方法,我怀疑Equals能否正确的比较（按照业务语义进行比较）
     * A: 这是Anonymous类型，c#将对每个property都进行比较
     *    注意：不用使用==（而要使用Equals），因为==操作符没有重载
     *    这个链接做了简要解释：https://stackoverflow.com/questions/23703846/equality-for-anonymous-types
     *    这个链接做了详尽解释：https://docs.microsoft.com/en-us/dotnet/csharp/programming-guide/classes-and-structs/anonymous-types
     *    - “Because the Equals and GetHashCode methods on anonymous types are defined in terms of the Equals and GetHashCode methods of the properties, 
     *       two instances of the same anonymous type are equal only if all their properties are equal.”
     *    匿名类基本用在Linq中，譬如new { prod.Color, prod.Price };
     *    这可以这样var anonArray = new[] { new { name = "apple", diam = 4 }, new { name = "grape", diam = 1 }};
     * 
     */
    class LinqEval_syntax
    {


        [Test]
        public void testSelectBasic() {

            List<String> userNames = new List<String> { "Tom", "Mary" };
            var querySimple = from username in userNames
                              select username;
            Assert.AreEqual(new String[] { "Tom", "Mary" }, querySimple.ToArray());

            var query = (from user in SampleData.AllUsers
                        select user).ToArray(); //or select user.Name
            Assert.AreEqual(6, query.Length);

            //纯粹为了测试而写的例子，就是中间弄了一个变量来使用（by let）
            var queryLet = (from user in SampleData.AllUsers
                            let len = user.Name.Length
                         select new { Name = user.Name, Length = len}).ToArray(); //or select user.Name

        }

        [Test]
        public void testSelectPartialNull()
        {

            List<String> userNames = new List<String> { "Tom", "Mary" };

            //中间Select对于某些结果返回null，然后通过下一个Where过滤掉它
            var r = userNames.Select(x =>
            {
                if (x == "Tom")
                {
                    return x;
                }
                else {
                    return null;
                }
                
            }).Where(s => s!= null);

            Assert.AreEqual(1, r.ToArray().Length);
            Assert.AreEqual("Mary", r.ToArray()[0]);
        }


        [Test]
        public void testSelectMax()
        {
            //这个没啥
            var nums = Enumerable.Range(1, 5);
            Assert.AreEqual(5, nums.Max());


            //这个关键是Max对于空的list操作会报错！加上.DefaultIfEmpty()则list中有一个默认值了
            var r = nums.Where(s => s>6).DefaultIfEmpty().Max();
            Assert.AreEqual(0, r);

        }

        [Test]
        public void testWhereBasic()
        {
            //where/过滤的作用
            //1. 直接字符串过滤
            var timQueryString = from user in SampleData.AllUsers
                                 where user.Name == "Tim Trotter"
                                 select user;

            //2. 直接对象过滤
            var timQueryRef = from user in SampleData.AllUsers
                              where user == SampleData.Users.TesterTim
                              select user;
            Assert.AreEqual(1, timQueryRef.ToArray().Length);
        }


        [Test]
        public void testWhereInDepth()
        {

            //TODO: 其进行等于比较的时候，会执行Equals么？还是每个Property进行比较？
            //      以前看过一个说明，如果是匿名对象的话，就比较每个field。但是这里的User不是匿名对象
            var timQueryRef = from user in SampleData.AllUsers
                              where user == SampleData.Users.TesterTim
                              select user;
            Assert.AreEqual(1, timQueryRef.ToArray().Length);


            //这里的where中是否调用到这个BinaryExpression呢？能打断点么？
            //https://docs.microsoft.com/en-us/dotnet/api/system.linq.expressions.expression.equal?view=netcore-3.1
            //https://docs.microsoft.com/en-us/dotnet/api/system.linq.expressions.binaryexpression?view=netcore-3.1
            User localTesterTim = new User("Tim Trotter", UserType.Tester);
            User localTesterTim2 = new User("Tim Trotter", UserType.Tester);
            var timQueryNewRef = from user in SampleData.AllUsers
                                 where user == localTesterTim
                                 select user;
            Assert.AreEqual(1, timQueryRef.ToArray().Length, "使用new出来的对象作为where中的比较对象，结果应该也是能比较成功（为true的）");

            //与上面的语意相同
            Assert.IsFalse(localTesterTim == localTesterTim2);
            SampleData.AllUsers.Where(user => user == localTesterTim);
            Assert.AreEqual(1, timQueryRef.ToArray().Length, "使用new出来的对象作为where中的比较对象，结果应该也是能比较成功（为true的）");

        }

        [Test]
        public void testOrderBy()
        {
             var query = from defect in SampleData.AllDefects
                                 where defect.AssignedTo.Name == "Tim Trotter"
                                 orderby defect.Created descending, defect.ID  //不要写多个orderby，而是要写一个orderby后面多个字段。如果有多个orderby（别那么干），则最后一个胜出。
                                 select defect;

        }

        /**
         *  这个链接更像数据库中的join
         * 
         */
        [Test]
        public void testJoin()

        {
            //note：尽量左边的（这里为defect）的数量级和大于右边的（这里为subscription）
            //      因为左边的数据获取的时候为stream获取，右边的缓冲
            var query = from defect in SampleData.AllDefects
                        join subscription in SampleData.AllSubscriptions
                            on defect.Project equals subscription.Project //注意，这里是equals而不是==，为啥？
                        select new { defect.Summary, subscription.EmailAddress };
        }

        /**
         * 
         * Group Join是形成1对多的结果关系
         * 这里既有Join，又有Group，厉害不！
         * 
         */
        [Test]
        public void testGroupJoin()
        {

            var query = from defect in SampleData.AllDefects
                        join subscription in SampleData.AllSubscriptions
                            on defect.Project equals subscription.Project //注意，这里是equals而不是==，为啥？
                            into groupedSubscriptions //这里是本case的核心
                        select new { Defect = defect, Subscriptions = groupedSubscriptions, Count = groupedSubscriptions.Count() };
            //查询结果的每一个记录是
            //Defect - the defect
            //Subscriptions - the defect相关的所有subscriptions

            //注意：defect与subscription本来是多对多的关系，一个defect有多个人订阅；一个人可以订阅多个defect
            //      经过这样一处理，得到了一个1对多的关系。注意：1个subscript可能出现在多个defect中

        }

        /**
         * 这个直接看P281的图更加清楚（C#InDepth 3rd CN）
         */
        [Test]
        public void testGroupBy()
        {
            //这个语句好写，关键是要了解其结果是如何组织的
            var query = from defect in SampleData.AllDefects
                        where defect.AssignedTo != null
                        group defect by defect.AssignedTo;

            foreach (var entry in query) {

                //entry.Key // 这个是by后面的AssignedTo（其来行是User）
                //entry.Key.Name 即 User.Name
                Console.WriteLine("{0}", entry.Key.Name);

                foreach (var defect in entry) {
                    Console.WriteLine("  {0}, {1}", defect.Severity, defect.Summary);
                }
            }

        }

        /**
         * 其被翻译为查询延续(11.6.2), 就是把查询结果into到一个变量中继续形成一个新的query结果
         */
        [Test]
        public void testQueryInto()
        {
            //这里的into开始是本语法的作用位置
            //我们把前面的group by结果放到的grouped中
            //当然，其实我们完全可以写成两个分开的语句
            var query = from defect in SampleData.AllDefects
                        where defect.AssignedTo != null
                        group defect by defect.AssignedTo into grouped
                        select new { Assignee = grouped.Key.Name, count = grouped.Count() };
        }


        [Test]
        public void testSelectCast()
        {
            //见C#INDepth 3rd chapter 11.2.4 Cast、OfType
            //下面的ArrayList没有类型，所以为Object
            //在Ling中我们使用了string username，使得背后调用的Enumerable<T>知道这是string类型
            //这里最好调用的是list.Cast<String>().Select(....)
            //Cast过程中有问题则直接报错（OfType则跳过类型不一致的元素）
            ArrayList userNames = new ArrayList { "Tom", "Mary" };
            var querySimple = (from string username in userNames //string username表示用Cast进行类型转换
                              select username).ToArray();
            Assert.AreEqual(2, querySimple.Length);
            Assert.AreEqual("Tom", querySimple[0]);
            Assert.AreEqual("Mary", querySimple[1]);

           
            //在castFail中必须取值，否则因为lazyEvaluation，其不会抛出异常
            //即：如果把ToArry()去掉，就不会抛出异常，则Assert失败
            Assert.Throws<System.InvalidCastException>(() => castFail());
        }

        private void castFail() {

            ArrayList userNames = new ArrayList { "Tom", "Mary", 123 };
            var querySimple = (from string username in userNames //string username表示用Cast进行类型转换, 因为123出现System.InvalidCastException
                           select username).ToArray();

        }
    }
}
