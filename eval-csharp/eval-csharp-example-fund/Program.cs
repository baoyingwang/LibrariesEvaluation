using eval_csharp_example_fund.db;
using Microsoft.Extensions.Logging;
using System;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace eval_csharp_example_fund
{
    /**
     * c#项目小练习
     * - phase 1
     *   - 当前开放式基金排名周涨幅前10名
     *   - 当前开放式基金排名与本检验周期开始时候的变化
     *     - 这个暂时卡住了，因为没有找到对应oneline api接口。天天基金本来有的，不过网上的那些教程都失败了，感觉天天基金做了更多的限制。
     *     - 除非我自己写一个程序把所有开放式基金过去1个月的数据都爬下来，然后还每天补充。嗯，这也是个主意！
     *       - https://api.doctorxiong.club/v1/fund/detail?code=110022 GET可以获取当前基金过去所有日子的净值（注意，这个网站每天1000次调用限制）
     *       - 获取所有基金列表 http://fund.eastmoney.com/js/fundcode_search.js （https://www.cnblogs.com/xmyzero/p/10319962.html）
     * - phase 2  
     *   - 支持排名不同排名策略
     *   - 支持按照当前策略固定金额股买的回测
     * 
     * - 爬虫参考
     *   - 获取基金列表
     *     - Python爬虫周记之案例篇——基金净值爬取（上） https://www.cnblogs.com/xmyzero/p/10319962.html
     *     - https://github.com/weibycn/fund
     *   - 基金排名
     *     - https://github.com/weibycn/fund
     *    
     */
    class Program
    {

         
        static void Main(string[] args)
        {
            Program app = new Program();

                    //TODO 把这里替换为注入值，而非hardcode
            String ConnectionStringSqlServer =
            //https://www.codeproject.com/questions/203694/connection-string-for-entity-framework-for-sql-ser
            "Data Source=localhost;Initial Catalog=fund_recommend_01;User Id=sa;Password=abcd1234;";

            String ConnectionStringSqlite =
            //https://www.codeproject.com/questions/203694/connection-string-for-entity-framework-for-sql-ser
            "Data Source=fund_recommandation_example.sqlite.db.ignore";

            var ConnectionString = ConnectionStringSqlite;
            var dbType = "sqlite";
            var dbService = new DBService(ConnectionString, dbType);
            var mdService = new MarketDataService(dbService);

            var createDBTask = dbService.CreateTheDtabaseAsync();
            Task.WaitAll(createDBTask);

            var initFundInfoTask = mdService.InitFundInfosAsync();
            Task.WaitAll(initFundInfoTask);

            var queryLastWeekRankTask = mdService.lastWeekGrowthRankAsync();
            var queryLastMonthRankTask = mdService.lastMonthGrowthRankAsync();
            Task.WaitAll(queryLastWeekRankTask, queryLastMonthRankTask);
            Console.WriteLine($"Got last week rank - heading 200 chars:{queryLastWeekRankTask.Result.Substring(0,200)}");
            Console.WriteLine($"Got last month rank - heading 200 chars:{queryLastWeekRankTask.Result.Substring(0, 200)}");


            var mainService = new FunRecommandationService(dbService, mdService);
            var addPositionExampleTask = mainService.addPositionsExampleAsync();
            addPositionExampleTask.Wait();

            Thread.Sleep(30 * 1000);
            
        }




    }
}
