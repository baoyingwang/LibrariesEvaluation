using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Threading.Tasks;

namespace eval_csharp_example_fund.db
{

    /**
     * entity core 官方文档： https://docs.microsoft.com/en-us/ef/core/
     * 
     */
    class DBService
    {

        private String _ConnectionString;
        private String _dbType;
        public DBService(String ConnectionString, String dbType) {
           this._ConnectionString = ConnectionString;
           this._dbType = dbType;
        }

        private FundRecommandContext newContext() {
            return new FundRecommandContext(_ConnectionString, _dbType);
        }

        //注意，这个相当于Task<Avoid>,就是本方法的Task里边没有返回值
        public async Task CreateTheDtabaseAsync()
        {
            //DBContext生命周期比较短，别全局共享
            //https://stackoverflow.com/questions/7647912/why-re-initiate-the-dbcontext-when-using-the-entity-framework
            using (var context = newContext())
            {
                bool created = await context.Database.EnsureCreatedAsync();
                String creationInfo = created ? "created" : "exists";
                Console.WriteLine($"database {creationInfo}");
            }
        }

        public async Task AddRankRawCacheAsync(String tradeDate, Int32 rankStrategyId, String rankSource, String rankRawContent)
        {

            using (var context = newContext())
            {
                var rankRawCache = new RankRawCache
                {
                    TradeDate = tradeDate,
                    RankStrategyId = rankStrategyId,
                    RankSource = rankSource,
                    RankRawContent = rankRawContent
                };

                await context.RankRawCaches.AddAsync(rankRawCache);
                int records = await context.SaveChangesAsync();
                Console.WriteLine($"{records} record added");
            }

            Console.WriteLine();
        }

        private async Task<List<RankRawCache>> ReadAllRankRawCachesAsync()
        {
            using (var context = newContext())
            {
                List<RankRawCache> caches = await context.RankRawCaches.ToListAsync();
                return caches;
            }
        }

        public async Task<RankRawCache> QueryRankRawCacheAsync(String tradeDate, Int32 rankStrategyId, String rankSource)
        {

            using (var context = newContext())
            {
                //列出两种不同的语法，用于对比不同

                //- 查不到就返回null（因为其是reference类型） by SingleOrDefault()
                //  - https://stackoverflow.com/questions/21196253/check-if-single-linq-return-null
                //- 另外  Single/First/SingleOrDefault/FirstOrDefault的行为
                //  - https://stackoverflow.com/questions/7809745/linq-code-to-select-one-item -
                var cache = await context.RankRawCaches
                    .Where(c => c.TradeDate == tradeDate && c.RankStrategyId == rankStrategyId && c.RankSource == rankSource)
                    .SingleOrDefaultAsync();

                var cacheLinqQ = await (from c in context.RankRawCaches
                                        where c.TradeDate == tradeDate && c.RankStrategyId == rankStrategyId && c.RankSource == rankSource
                                        select c).SingleOrDefaultAsync();
                return cacheLinqQ;
            }
        }

        private async Task<List<RankRawCache>> QueryRankRawCachesAsync(String tradeDate, Int32 rankStrategyId)
        {
            using (var context = newContext())
            {
                List<RankRawCache> caches = await context.RankRawCaches
                    .Where(c => c.TradeDate == tradeDate && c.RankStrategyId == rankStrategyId)
                    .ToListAsync();
                return caches;
            }
        }

        private async Task<List<RankRawCache>> QueryRankRawCachesAsync(String tradeDate)
        {
            using (var context = newContext())
            {
                List<RankRawCache> caches = await context.RankRawCaches
                    .Where(c => c.TradeDate == tradeDate)
                    .ToListAsync();
                return caches;
            }
        }


        public async Task AddFundInfoAsync(String fundId, String fundName, String lastdayDate, double lastdayPrice)
        {

            using (var context = newContext())
            {
                var fundInfo = new FundInfo
                {
                    FundId = fundId,
                    FundName = fundName,
                    LastdayDate = lastdayDate,
                    LastdayPrice = lastdayPrice
                };

                await context.FundInfos.AddAsync(fundInfo);
                int records = await context.SaveChangesAsync();
                Console.WriteLine($"{records} record added");
            }

        }

        public async Task<FundInfo> QueryFundInfoAsync(String fundId)
        {
            using (var context = newContext())
            {

                //列出两种不同的语法，用于对比不同

                //- 查不到就返回null（因为其是reference类型） by SingleOrDefault()
                //  - https://stackoverflow.com/questions/21196253/check-if-single-linq-return-null
                //- 另外  Single/First/SingleOrDefault/FirstOrDefault的行为
                //  - https://stackoverflow.com/questions/7809745/linq-code-to-select-one-item -
                var fundInfo = await context.FundInfos
                    .Where(c => c.FundId == fundId)
                    .SingleOrDefaultAsync();

                var fundInfoLinqQ = await (from c in context.FundInfos
                                           where c.FundId == fundId
                                           select c).SingleOrDefaultAsync();
                return fundInfoLinqQ;
            }


        }

        public async Task<List<FundInfo>> QueryFundInfosAsync()
        {
            using (var context = newContext())
            {

                var fundInfoLinqQ = await (from c in context.FundInfos
                                           select c).ToListAsync();
                return fundInfoLinqQ;
            }

        }

        public async Task AddPositionAsync(String fundId, Int32 amount)
        {

            using (var context = newContext())
            {

                var fundInfo = await context.FundInfos.FirstOrDefaultAsync(s => s.FundId == fundId);
                if (fundInfo == null)
                {
                    throw new BusinessException($"not find fund info for {fundId} while add postion amount:{amount}");
                }

                var p = new Position
                {
                    //- 这里，必须从当前context中查询出来相关的object关联上。这样保证上下文一致
                    //  - 参考： https://stackoverflow.com/questions/45759143/how-to-insert-a-record-into-a-table-with-a-foreign-key-using-entity-framework-in
                    //否则，如果我自己new一个新的fundinfo，其将试图插入一个新的fundinfo（当然会导致我的uniq constraint violation）
                    //  - 还有一些别的技巧，譬如对于ef（not core） 来说，可以用 IObjectContextAdapter https://yq.aliyun.com/articles/568942
                    //  - 再譬如一些detach技巧，或者设置null技巧 https://www.it-swarm.asia/zh/c%23/%e5%a6%82%e4%bd%95%e9%98%bb%e6%ad%a2entity-framework%e5%b0%9d%e8%af%95%e4%bf%9d%e5%ad%98%e6%8f%92%e5%85%a5%e5%ad%90%e5%af%b9%e8%b1%a1%ef%bc%9f/1048114987/
                    FundInfo = fundInfo,

                    Amount = amount
                };

                await context.Positions.AddAsync(p);
                int records = await context.SaveChangesAsync();
                Console.WriteLine($"{records} Position added");
            }

        }

        public async Task<List<Position>> QueryPositionsAsync()
        {
            using (var context = newContext())
            {
                List<Position> positions = await (from position in context.Positions
                                              select position).ToListAsync();
                return positions;
            }
        }

        public async Task<Position> QueryPositionAsync(String fundId)
        {
            using (var context = newContext())
            {

                var position = await context.Positions.FirstOrDefaultAsync(s => s.FundInfo.FundId == fundId);
                return position;
            }
        }
    }
}
