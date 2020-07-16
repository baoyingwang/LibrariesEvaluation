using NUnit.Framework;
using System;
using System.CodeDom;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace eval_csharp_linq
{
    /**
     * 这里都没啥，因为使用方法与Linq to Objects都是一样的
     * 
     */
    class LinqToSqlEval
    {

        [Test]
        public void testConnection()
        {
            using (var context = new DataClasses1DataContext())
            {
                context.Log = Console.Out;
                var query = from user in context.DefectUsers
                            select new { Name = user.Name, Length = user.Name.Length};

                Assert.AreEqual(6, query.ToArray().Length);

            }
        }
    }
}
