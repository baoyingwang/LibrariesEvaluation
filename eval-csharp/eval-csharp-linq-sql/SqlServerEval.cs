using System;
using System.Data.SqlClient;
using Microsoft.Data.Sqlite;
using NUnit.Framework;

/**
 * 
 * 本文原来的目标是创建测试C#InDepth 3rd chapter12的数据表和准备数据， 不过不需要了（因为下载到了其数据库）
 * 下面保留作为参考如何操作数据库连接之类的
 *
 * 之所以使用sqlserver，是因为想尝试使用linq to sql designer的界面设计器，用于生成相关model类和编排主外键关系
 *  - 经过了example-fund poc项目，感觉手工写model也可以。自动生成的总是感觉不踏实
 *  - 不过如果有很多表和大量的字段，则手工写model则不太现实，主要是容易出错。必须借助工具，不管是linq to sql designer，还是EF core中的任何工具
 *

 * Sql Server环境搭建比较简单
 * - 在我的虚拟机上面启动一个docker ：sudo docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=abcd1234' -e 'MSSQL_PID=Express' -p 1433:1433 -d mcr.microsoft.com/mssql/server:2017-latest-ubuntu
 * 　　- 注意：其启动需要２G以上内存，我给整个虚拟机４G，够了
 * 　　- 登录直接使用当前最好的ｊｄｂｃ　客户端　DBeaver（驱动自动下载），用户名直接sa/abcd1234@localhost
 * 　　- 建立了端口映射 host:1433/vm:1433 for sql server, host:3020/vm:22 for ssh baoying@localhost -p 3020
 * - 这里的数据结果，是我参考C#InDepth chapter12中用到的几个表，人肉写出来了
 *   - 数据一会儿也加进去
 * 
 * 
 */
namespace eval_csharp.db
{ 

    class SQLServerEval
    {

        [Test]
        public void testConnection()
        {
            //更多链接字符串
            //https://www.connectionstrings.com/sqlite/
            string cs = "Server=localhost;Database=master;User Id=sa;Password=abcd1234;";
            string stm = "SELECT 1";

            using var con = new SqlConnection(cs);
            con.Open();

            using var cmd = new SqlCommand(stm, con);

            string version = cmd.ExecuteScalar().ToString();
            Assert.AreEqual("1",version);
        }



        [Test]
        public void testCreateDB()
        {

            //这里的id使用int（而没有用long），是应为下面在线工具不支持long
            //在线工具 https://codverter.com/src/index
            var tableProejct = @"IF OBJECT_ID('dbo.Project', 'U') IS NOT NULL DROP TABLE dbo.Project; 
create table Project(
    projectid bigint,
    name varchar(255),
    PRIMARY KEY(projectid)
);";

            var notificationSubscription = @"IF OBJECT_ID('dbo.NotificationSubscription', 'U') IS NOT NULL DROP TABLE dbo.NotificationSubscription;
create table NotificationSubscription(
NotificationSubscriptionID bigint,
ProjectID bigint,
EmailAddress varchar(255),
PRIMARY KEY(NotificationSubscriptionID)
)";

            var tableDefect = @"IF OBJECT_ID('dbo.Defect', 'U') IS NOT NULL DROP TABLE dbo.Defect;
create table Defect(
ID             bigint,
Created        datetime, 
LastMonitified datetime, 
Summary        varchar(1024),
Severity       int,
Status         varchar(255),
AssignedToUserId bigint,
CreatedByUserID  bigint,
ProjectID        bigint,
PRIMARY KEY(ID)
)";

            var tableUser = @"IF OBJECT_ID('dbo.DefectUser', 'U') IS NOT NULL DROP TABLE dbo.DefectUser;
create table DefectUser(
UserID bigint,
Name varchar(255),
UserType varchar(255),
PRIMARY KEY(UserID)
)";

            String[] createTablesSql = { tableProejct, notificationSubscription, tableDefect, tableUser };

            string cs = "Server=localhost;Database=master;User Id=sa;Password=abcd1234;";
            using var con = new SqlConnection(cs);
            con.Open();


            foreach(var createTableSql in createTablesSql){
                using var cmd = new SqlCommand(createTableSql, con);
                cmd.ExecuteNonQuery();
            }


        }


    }
}
