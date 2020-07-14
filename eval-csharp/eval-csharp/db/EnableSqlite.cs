using System;
using Microsoft.Data.Sqlite;
using NUnit.Framework;

/**
 * NuGet-添加Microsoft.Data.Sqlite.Core, 和Microsoft.Data.Sqlite
 * - 还有一些别的，如sqlite之类的
 * - 用了这个因为 1）其与参考文档中一致， 2）其更新时间为2020，比较新
 * - note：之前只添加了Microsoft.Data.Sqlite.Core，但是无法获取connection，再添加Microsoft.Data.Sqlite之后成功
 * - 代码参考：   http://zetcode.com/csharp/sqlite/
 */
namespace eval_csharp.db
{ 

    class EnableSqlite
    {

        [Test]
        public void testVersion()
        {
            string cs = "Data Source=:memory:";
            string stm = "SELECT SQLITE_VERSION()";

            using var con = new SqliteConnection(cs);
            con.Open();
            using var cmd = new SqliteCommand(stm, con);

            string version = cmd.ExecuteScalar().ToString();
            Assert.AreEqual("3.28.0",version);
        }
    }
}
