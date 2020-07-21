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
     * - 给出购买基金的建议
     *   - 策略为最近1个周涨幅前10名
     *   - 支持回测
     *     - 按照当前策略，回测过去X年时间的盈利是多少
     * 
     */
    class Program
    {

         
        static void Main(string[] args)
        {
            Program app = new Program();

                    //TODO 把这里替换为注入值，而非hardcode
            String ConnectionString =
            //@"server=(localdb)\MSSQLLocalDb;database=fund_recommend; trusted_connection=true";
            //https://www.codeproject.com/questions/203694/connection-string-for-entity-framework-for-sql-ser
            "Data Source=localhost;Initial Catalog=fund_recommend_01;User Id=sa;Password=abcd1234;";

            var dbService = new DBService(ConnectionString);
            var mdService = new MarketDataService(dbService);

            var createDBTask = dbService.CreateTheDtabaseAsync();
            Task.WaitAll(createDBTask);

            var queryLatestRankTask = mdService.latestRankAsync();
            var initFundInfoTask = mdService.InitFundInfosAsync();
            Task.WaitAll(queryLatestRankTask, initFundInfoTask);
            Console.WriteLine($"Got latest rank - heading 200 chars:{queryLatestRankTask.Result.Substring(0,200)}");


            var mainService = new FunRecommandationService(dbService, mdService);
            var addPositionExampleTask = mainService.addPositionsExampleAsync();
            addPositionExampleTask.Wait();

            Thread.Sleep(30 * 1000);
            
        }




    }
}
