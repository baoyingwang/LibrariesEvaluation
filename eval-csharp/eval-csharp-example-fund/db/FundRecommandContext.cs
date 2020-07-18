using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.Extensions.Logging;
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
        //TODO 把这里替换为注入值，而非hardcode
        private const String ConnectionString =
            //@"server=(localdb)\MSSQLLocalDb;database=fund_recommend; trusted_connection=true";
            //https://www.codeproject.com/questions/203694/connection-string-for-entity-framework-for-sql-ser
            "Data Source=localhost;Initial Catalog=fund_recommend;User Id=sa;Password=abcd1234;";

        public DbSet<RankRawCache> RankRawCaches { get; set; }

        //本来是照着Wrox书做的，不过这个地方Wrox上的找不到methodptionBuild.UseSqlServer method
        //可能是我这里用的EF core，而书上是EF 5？6？
        //不过不管怎样，我是搜索工程师!
        //https://stackoverflow.com/questions/38878140/how-can-i-implement-dbcontext-connection-string-in-net-core
        public FundRecommandContext()//: base(GetOptions(ConnectionString))
        {
        }

        private static DbContextOptions GetOptions(string connectionString)
        {
            return SqlServerDbContextOptionsExtensions.UseSqlServer(new DbContextOptionsBuilder(), connectionString).Options;
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder) => optionsBuilder
        .UseLoggerFactory(MyLoggerFactory) // Warning: Do not create a new ILoggerFactory instance each time
        .UseSqlServer(ConnectionString);

        //配置文件的话在这里 https://stackoverflow.com/questions/56646186/how-to-enable-logging-in-ef-core-3
        //https://docs.microsoft.com/en-us/ef/core/miscellaneous/logging?tabs=v3
        public static readonly ILoggerFactory MyLoggerFactory = LoggerFactory.Create(builder => {
               builder.AddConsole(); 
        });

        //注意，这个相当于Task<Avoid>,就是本方法的Task里边没有返回值
        public async Task CreateTheDtabaseAsync()
        {

            using (var context = new FundRecommandContext())
            {
                bool created = await context.Database.EnsureCreatedAsync();
                String creationInfo = created ? "created" : "exists";
                Console.WriteLine($"database {creationInfo}");
            }
        }

        public async Task AddRankRawCacheAsync(String tradeDate, Int32 rankStrategyId, String rankSource, String rankRawContent) {

            using(var context = new FundRecommandContext()){
                var rankRawCache = new RankRawCache
                {
                    tradeDate = tradeDate,
                    rankStrategyId = rankStrategyId,
                    rankSource = rankSource,
                    rankRawContent = rankRawContent
                };

                await context.RankRawCaches.AddAsync(rankRawCache);

                int records = await context.SaveChangesAsync();
                Console.WriteLine($"{records} record added");
            }

            Console.WriteLine();
        }

        private async Task<List<RankRawCache>> ReadAllRankRawCachesAsync() {
            using(var context = new FundRecommandContext()){
                List<RankRawCache> caches = await context.RankRawCaches.ToListAsync();
                return caches;
            }
        }

        public async Task<RankRawCache> QueryRankRawCacheAsync(String tradeDate, Int32 rankStrategyId, String rankSource)
        {
            
            using (var context = new FundRecommandContext())
            {

                //- 查不到就返回null（因为其是reference类型） by SingleOrDefault()
                //  - https://stackoverflow.com/questions/21196253/check-if-single-linq-return-null
                //- 另外  Single/First/SingleOrDefault/FirstOrDefault的行为
                //  - https://stackoverflow.com/questions/7809745/linq-code-to-select-one-item -
                var cache = await context.RankRawCaches
                    .Where(c => c.tradeDate == tradeDate && c.rankStrategyId == rankStrategyId && c.rankSource == rankSource)
                    .SingleOrDefaultAsync();
                return cache;
            }
        }

        private async Task<List<RankRawCache>> QueryRankRawCachesAsync(String tradeDate, Int32 rankStrategyId)
        {
            using (var context = new FundRecommandContext())
            {
                List<RankRawCache> caches = await context.RankRawCaches
                    .Where(c => c.tradeDate == tradeDate && c.rankStrategyId == rankStrategyId)
                    .ToListAsync();
                return caches;
            }
        }

        private async Task<List<RankRawCache>> QueryRankRawCachesAsync(String tradeDate)
        {
            using (var context = new FundRecommandContext())
            {
                List<RankRawCache> caches = await context.RankRawCaches
                    .Where(c => c.tradeDate == tradeDate)
                    .ToListAsync();
                return caches;
            }
        }
    }
}
