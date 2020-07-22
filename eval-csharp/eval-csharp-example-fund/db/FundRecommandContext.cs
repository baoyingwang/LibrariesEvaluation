using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace eval_csharp_example_fund.db
{
    
    class FundRecommandContext: DbContext
    {

        private String _ConnectionString;
        private String _dbType = ""; //SqlServer, ot Sqlite
        public DbSet<RankRawCache> RankRawCaches { get; set; }
        public DbSet<Position> Positions { get; set; }
        public DbSet<FundInfo> FundInfos { get; set; }

        //本来是照着Wrox书做的，不过这个地方Wrox上的找不到methodptionBuild.UseSqlServer method
        //可能是我这里用的EF core，而书上是EF 5？6？
        //不过不管怎样，我是搜索工程师!
        //https://stackoverflow.com/questions/38878140/how-can-i-implement-dbcontext-connection-string-in-net-core
        public FundRecommandContext(String connectionString, String dbType)//: base(GetOptions(ConnectionString))
        {
            this._ConnectionString = connectionString;
            this._dbType = dbType;
        }

        //TODO remove this method once verified
        //private static DbContextOptions GetOptions(string connectionString)
        //{
        //    return SqlServerDbContextOptionsExtensions.UseSqlServer(new DbContextOptionsBuilder(), connectionString).Options;
        //}

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder
                .UseLoggerFactory(MyLoggerFactory);// Warning: Do not create a new ILoggerFactory instance each time

            if ("sqlite".Equals(this._dbType.ToLower()))
            {
                optionsBuilder.UseSqlite(_ConnectionString);
            }
            else {
                optionsBuilder.UseSqlServer(_ConnectionString);
            }
        }
        

        //配置文件的话在这里 https://stackoverflow.com/questions/56646186/how-to-enable-logging-in-ef-core-3
        //https://docs.microsoft.com/en-us/ef/core/miscellaneous/logging?tabs=v3
        public static readonly ILoggerFactory MyLoggerFactory = LoggerFactory.Create(builder => {
               builder.AddConsole(); 
        });

    }
}
