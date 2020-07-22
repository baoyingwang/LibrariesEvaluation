using eval_csharp_example_fund.db;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace eval_csharp_example_fund
{
    class MarketDataService
    {
        private DBService _dbService;

        public MarketDataService(DBService fundRecommandContext)
        {

            _dbService = fundRecommandContext;
        }

        //建议整个app用1个http client
        //It is recommended（https://docs.microsoft.com/en-us/azure/architecture/antipatterns/improper-instantiation/）
        //to instantiate one HttpClient for your application's lifetime and share it unless you have a specific reason not to.
        //https://stackoverflow.com/questions/4015324/how-to-make-an-http-post-web-request
        private static readonly HttpClient client = new HttpClient();


        internal class RankRequest
        {
            [JsonPropertyName("fundType")]
            public String[] fundTypes { get; set; }

            [JsonPropertyName("sort")]
            public String sortBy { get; set; }

            [JsonPropertyName("fundCompany")]
            public String[] fundCompanies { get; set; }
            public Int32 createTimeLimit { get; set; }
            public Int32 fundScale { get; set; }
            public Int32 asc { get; set; }
            public Int32 pageIndex { get; set; }
            public Int32 pageSize { get; set; } = 10; //Property默认值

        }

        public async Task<String> lastWeekGrowthRankAsync() {

            return await growthRankAsync("lastWeekGrowth");
        }
        public async Task<String> lastMonthGrowthRankAsync() {
            return await growthRankAsync("lastMonthGrowth");
        }

        //当前基金排行 post https://www.doctorxiong.club/api/v1/fund/rank
        public async Task<String> growthRankAsync(String sortby)
        {
            String tradeDate = lastTradeDateWithData();
            int rankStrategyId = 1;
            String rankSource = "doctorxiong";


            var cachedContent = await _dbService.QueryRankRawCacheAsync(tradeDate, rankStrategyId, rankSource);
            if (cachedContent != null)
            {
                Console.WriteLine($"got date from cache: tradeDate:{tradeDate} from:{rankSource} with StrategyId:{rankStrategyId}");
                return cachedContent.RankRawContent;
            }


            //https://stackoverflow.com/questions/4015324/how-to-make-an-http-post-web-request
            RankRequest request = new RankRequest
            {
                sortBy = sortby
            };
            String postContentStr = JsonSerializer.Serialize(request);
            var postContent = new StringContent(postContentStr, Encoding.UTF8, "application/json");
            var response = await client.PostAsync("https://api.doctorxiong.club/v1/fund/rank", postContent);
            var content = await response.Content.ReadAsStringAsync();

            //TODO 增加返回数据的验证的方法
            bool isValidContent = true;
            if (!isValidContent)
            {
                Console.WriteLine($"got invalid content when query with tradeDate:{tradeDate} from:{rankSource} with StrategyId:{rankStrategyId}, content:{content}");
                return null;
            }

            //重要设计点： 有可能其他线程/进程在查询期间把数据添加进去了，这时候我应该可以继续运行业务逻辑而不进行更新了 
            //Answer：很简单，这里的添加是允许失败的（不管是因为uniq失败，还是网络问题）。
            //- bad design：只要增加一个函数叫TryAddRankRawCacheAsync就行了，失败就打印一个Warning（并明确说一般不是问题，可以检查uniq之类的）
            //- better design： 只要忽略task的执行错误就行了，不过要记录执行失败情况，作为后续可能的trouble shooting材料
            //- 另外：另一个解决思路：MySql数据库有个insertORupdate特性，发现uniq问题之后变为update语句。不过对于本应用的当前这个cache插入来说，SqlServer有没有这个feature都无伤大雅。上面的解决思路就足够了。
            //add to cache in case we use it later
            Console.WriteLine($"add date to cache: tradeDate:{tradeDate} from:{rankSource} with StrategyId:{rankStrategyId}");
            Task taskAddRecord = _dbService.AddRankRawCacheAsync(tradeDate, rankStrategyId, rankSource, content);
            //我们只关心失败情况，成功的就让他过去吧
            //tech：这里也没有使用await，因为也不想获取结果了，没有必要通过await做什么线程切换了
            _ = taskAddRecord.ContinueWith(task =>
            {
                return "bad, fault with :" + taskAddRecord.Exception.InnerException;
            }, TaskContinuationOptions.OnlyOnFaulted);

            return content;
        }

        //TODO 目前直接获得当天作为trade date当然不对啦，因为1）当天可能是休息日/节日， 2）即使是工作日当天基金净值计算不出来
        //对于当前项目来说，要获取一个可以得到资金资产净值的最近日期
        //可以通过调用3rd接口去获取tradeDate；一般要是工作日，而且还要维护一个tradeDate列表，支持人肉修改这个列表

        private String lastTradeDateWithData()
        {
            return DateTime.Now.ToString("yyyyMMdd");
        }

        //TODO 加载真正的数据，现在是fake数据
        public async Task InitFundInfosAsync()
        {
            {
                var fundInfo = await _dbService.QueryFundInfoAsync("000001");
                if (fundInfo == null)
                {
                    await _dbService.AddFundInfoAsync("000001", "基金001", "20200716", 1.79);
                }
            }


            {
                var fundInfo = await _dbService.QueryFundInfoAsync("000002");
                if (fundInfo == null)
                {
                    await _dbService.AddFundInfoAsync("000002", "基金002", "20200716", 2.54);
                }
            }

            {
                var fundInfo = await _dbService.QueryFundInfoAsync("000003");
                if (fundInfo == null)
                {
                    await _dbService.AddFundInfoAsync("000003", "基金003", "20200716", 0.9);
                }
            }
            
        }

        /**
         * 获取某基金(code)在某日期(date)的净值 
         * - 注意缓存相关数据
         * - 如果无法获取准确值，则获取近似值
         *   - 因为 只提供周一个值
         *   - 如果最准的那个无法获取，则使用这个
         * 
         */
        public double getNetWorth(String code, String date)
        {

            //check cache（exact match）

            //最准确：天天基金（说明：https://www.doctorxiong.club/api/#api-Fund-PostV1FundRank）
            //http://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code=110022&page=1&sdate=2019-01-01&edate=2019-02-13&per=20

            //小熊每周一个值 -用这个的话，那可能cache中就行了，访问小熊之前再次检查cache
            //https://www.doctorxiong.club/api/#api-Fund-PostV1FundRank


            return 0;
        }
    }
}
