using System;
using Microsoft.Data.Sqlite;
using NUnit.Framework;

/**
 * NuGet-添加Microsoft.Data.Sqlite.Core, 和Microsoft.Data.Sqlite
 * - 还有一些别的，如sqlite之类的
 * - 用了这个因为 1）其与参考文档中一致， 2）其更新时间为2020，比较新
 * - note：之前只添加了Microsoft.Data.Sqlite.Core，但是代码无法获取connection，再添加Microsoft.Data.Sqlite之后成功
 * - 代码参考：   http://zetcode.com/csharp/sqlite/
 *   - 这个代码是直接使用native sql的
 *   - 其获取数据库连接符那个面更加有帮助，对于使用linq或者entity framework查询用处不大
 * - 又安装了
 *   - Entity Framework v6 - 因为下面的provider安装的是ErikEJ for EF6
 *   - Entity Framework for Sqlite - 看到顺便装了
 * 
 * 如何增加sqlite3 provider，现在server explorer中没有sqlite3
 * https://stackoverflow.com/questions/48036387/sqlite-data-provider-is-missing-in-visual-studio-2017
 * - by ErikEJ 不灵：下载了https://marketplace.visualstudio.com/items?itemName=ErikEJ.SQLServerCompactSQLiteToolbox
 *     - 已经安装并且可以看到Sqlite/EF6的provider
 *   - 但是在Server Explorer中添加一个sqlite链接失败，总是提示没有安装:"unable to find the requested .net framwork data provider. it maybe not be installed"
 *   - 尝试安装NuGet -Entity Framework v6 和 Entity Framework for Sqlite，但是也没啥用
 * - by devart： 尝试 https://www.devart.com/dotconnect/sqlite/download.html
 *   - 这个例子用了：https://www.bricelam.net/2012/10/entity-framework-on-sqlite.html
 *   - 安装后可以看到多了下面这个element ： C:\Windows\Microsoft.NET\Framework\v4.0.30319\Config\machine.config (note: v2.xx 的config中也增加了）
 *   - 添加server explorer增加了一个SQLite Database选项（之前的ErikEJ 中都标记了自己brand，这个没标，霸气！）
 * <system.data>
        <DbProviderFactories>
            <add name="dotConnect for SQLite" invariant="Devart.Data.SQLite"
                description="Devart dotConnect for SQLite" type="Devart.Data.SQLite.SQLiteProviderFactory, Devart.Data.SQLite, Version=5.15.1666.0, Culture=neutral, PublicKeyToken=09af7300eec23701" />
        </DbProviderFactories>
    </system.data>
 *   
 *   
 * Sqlite链接使用的例子
 * https://stackoverflow.com/questions/28382421/retrieving-data-using-linq
 * https://www.bricelam.net/2012/10/entity-framework-on-sqlite.html
 * https://stackoverflow.com/questions/22561248/sqlite-linq-provider-data-context
 * https://docs.microsoft.com/en-us/ef/ef6/?redirectedfrom=MSDN
 * 
 * 还是从官网文档开始比较好
 * https://docs.microsoft.com/en-us/dotnet/framework/data/adonet/sql/linq/
 * 
 * 
 * 如何生成data model相关的类
 * - 用EF6的话，可以用界面工具（怎么做？TODO）
 * - Linq to SQL的话，可以用linq to sql designer
 *   - 官方文档 https://docs.microsoft.com/en-us/visualstudio/data-tools/linq-to-sql-tools-in-visual-studio2?view=vs-2019
 *   - CN介绍 https://www.cnblogs.com/DebugLZQ/archive/2012/11/14/2770449.html
 *   - 前提：
 *     - 安装Linq to Sql component
 *       - 直接VS界面也能调出来安装界面 https://www.cnblogs.com/lb809663396/p/10943718.html
 *     - 项目类型：Console App(.Net Core)项目无法使用它（new的时候找不到Linq to Sql Class这个类型，即使确认添加好了这个component）；Console App(.Net Framework)可以使用。
 *       - 这个讨论中Markus Hoffmann在Jun 3， 2020给出的精确的答案！ https://developercommunity.visualstudio.com/content/problem/139697/linq-to-sql-is-missing-after-adding-in-the-install.html
 *       - 尝试人肉把我的.net core项目变成.net framework
 *         - https://stackoverflow.com/questions/43788046/how-can-i-convert-a-net-core-project-to-a-net-framework-project
 * - 自己生成的话，有一些工具
 *   - 在线工具 https://codverter.com/src/index
 *      - 该工具在此讨论中给出 https://stackoverflow.com/questions/52225503/generate-c-sharp-class-from-sql-server-table
 *      - 该工具实在本地浏览器中运行的js脚本，stackoverflow讨论组作者（jonathana ）现身提到这一点
 * 
 * - 折腾一大圈回到原点
 *   - Ling to Sql class 只支持Sql Server， 不支持从Sqlite生成这些class之类的。
 *   
 *   EF provider - sqlite
 *   https://docs.microsoft.com/en-us/ef/core/providers/sqlite/?tabs=dotnet-core-cli
 */
namespace eval_csharp.db
{ 

    class EnableSqlite
    {

        //办例子来自于：http://zetcode.com/csharp/sqlite/
        //官方文档https://docs.microsoft.com/en-us/dotnet/standard/data/sqlite/?tabs=netcore-cli
        [Test]
        public void testVersion()
        {
            //更多链接字符串
            //https://www.connectionstrings.com/sqlite/
            string cs = "Data Source=:memory:";

            string stm = "SELECT SQLITE_VERSION()";

            using var con = new SqliteConnection(cs);
            con.Open();

            using var cmd = new SqliteCommand(stm, con);

            string version = cmd.ExecuteScalar().ToString();
            Assert.AreEqual("3.28.0",version);
        }

        [Test]
        public void testCreateTable() {

            var createTableSql = @"create table Project(
	                                projectid long,
	                                name varchar(255),
	                                PRIMARY KEY(projectid)
                                ); ";

            using var con = new SqliteConnection("Data Source=:memory:");
            con.Open();

            using var cmd = new SqliteCommand(createTableSql, con);

        }



        [Test]
        public void testFileDB()
        {

            //这里的id使用int（而没有用long），是应为下面在线工具不支持long
            //在线工具 https://codverter.com/src/index
            var tableProejct = @"drop table if exists Project;
                                create table Project(
	                                projectid int,
	                                name varchar(255),
	                                PRIMARY KEY(projectid)
                                ); ";

            var notificationSubscription = @"drop table if exists NotificationSubscription;
create table NotificationSubscription(
                                    NotificationSubscriptionID int,
                                    ProjectID int,
                                    EmailAddress varchar(255)
                                    )";

            var tableDefect = @"drop table if exists Defect;
create table Defect(
ID long,
Created Text, -- https://www.sqlitetutorial.net/sqlite-date/
LastMonitified Text, -- time
Summary varchar(1024),
Severity int,
Status varchar(255),
AssignedToUserId int,
CreatedByUserID int,
ProjectID int
)";

            var tableUser = @"drop table if exists User;
create table User(
UserID int,
Name varchar(255),
UserType varchar(255)
)";

            String[] createTablesSql = { tableProejct, notificationSubscription, tableDefect, tableUser };

            var sqlite3filename = "d:/sqlite3.db.ignore";
            using var con = new SqliteConnection($"Data Source={sqlite3filename}");
            con.Open();


            foreach(var createTableSql in createTablesSql){
                using var cmd = new SqliteCommand(createTableSql, con);
                cmd.ExecuteNonQuery();
            }


        }


    }
}
