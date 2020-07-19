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
        //建议整个app用1个http client
        //It is recommended（https://docs.microsoft.com/en-us/azure/architecture/antipatterns/improper-instantiation/）
        //to instantiate one HttpClient for your application's lifetime and share it unless you have a specific reason not to.
        //https://stackoverflow.com/questions/4015324/how-to-make-an-http-post-web-request
        private static readonly HttpClient client = new HttpClient();


        static void Main(string[] args)
        {
            Program app = new Program();
            Console.WriteLine("Hello World!");

            using (FundRecommandContext context = new FundRecommandContext())
            {
                var task = context.CreateTheDtabaseAsync();
                task.Wait();

                Task<String> taskRank = app.latestRankAsync();
                Console.WriteLine($"Got latest rank - heading 200 chars:{taskRank.Result.Substring(0,200)}");
            }


            Console.WriteLine("Hello World!");
            Thread.Sleep(30 * 1000);
            
        }



        /**
         * 获取某基金(code)在某日期(date)的净值 
         * - 注意缓存相关数据
         * - 如果无法获取准确值，则获取近似值
         *   - 因为https://www.doctorxiong.club/api/#api-Fund-PostV1FundRank只提供周一个值
         *   - 如果最准的那个无法获取，则使用这个
         * 
         */
        private double getNetWorth(String code, String date) {

            //check cache（exact match）

            //最准确：天天基金（说明：https://www.doctorxiong.club/api/#api-Fund-PostV1FundRank）
            //http://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code=110022&page=1&sdate=2019-01-01&edate=2019-02-13&per=20

            //小熊每周一个值 -用这个的话，那可能cache中就行了，访问小熊之前再次检查cache
            //https://www.doctorxiong.club/api/#api-Fund-PostV1FundRank


            return 0;
        }

        //当前基金排行 post https://www.doctorxiong.club/api/v1/fund/rank
        //目前排名的策略是固定的，如过去一周的涨幅
        private async Task<String> latestRankAsync() {

            String tradeDate = lastTradeDateWithData();
            int rankStrategyId = 1;
            String rankSource = "doctorxiong";
            
            //return directly if found the record from cache
            using (FundRecommandContext context = new FundRecommandContext())
            {
                var cachedContent = await context.QueryRankRawCacheAsync(tradeDate, rankStrategyId, rankSource);
                if (cachedContent != null) {
                    Console.WriteLine($"got date from cache: tradeDate:{tradeDate} from:{rankSource} with StrategyId:{rankStrategyId}");
                    return cachedContent.rankRawContent;
                }
            }

            //TODO 增加查询指定tradeDate参数
            //https://stackoverflow.com/questions/4015324/how-to-make-an-http-post-web-request
            var postContent = new StringContent("{}", Encoding.UTF8, "application/json");
            var response = await client.PostAsync("https://api.doctorxiong.club/v1/fund/rank", postContent);
            var content = await response.Content.ReadAsStringAsync();

            //TODO 增加返回数据的验证的方法
            bool isValidContent = true;
            if (!isValidContent) { 
                Console.WriteLine($"got invalid content when query with tradeDate:{tradeDate} from:{rankSource} with StrategyId:{rankStrategyId}, content:{content}");
                return null;
            }

            //重要设计点： 有可能其他线程/进程在查询期间把数据添加进去了，这时候我应该可以继续运行业务逻辑而不进行更新了 
            //Answer：很简单，这里的添加是允许失败的（不管是因为uniq失败，还是网络问题）。
            //- bad design：只要增加一个函数叫TryAddRankRawCacheAsync就行了，失败就打印一个Warning（并明确说一般不是问题，可以检查uniq之类的）
            //- better design： 只要忽略task的执行错误就行了，不过要记录执行失败情况，作为后续可能的trouble shooting材料
            //- 另外：另一个解决思路：MySql数据库有个insertORupdate特性，发现uniq问题之后变为update语句。不过对于本应用的当前这个cache插入来说，SqlServer有没有这个feature都无伤大雅。上面的解决思路就足够了。
            //add to cache in case we use it later
            using (FundRecommandContext context = new FundRecommandContext())
            {
                Console.WriteLine($"add date to cache: tradeDate:{tradeDate} from:{rankSource} with StrategyId:{rankStrategyId}");
                Task taskAddRecord = context.AddRankRawCacheAsync(tradeDate, rankStrategyId, rankSource, content);
                //我们只关心失败情况，成功的就让他过去吧
                //tech：这里也没有使用await，因为也不想获取结果了，没有必要通过await做什么线程切换了
                _ = taskAddRecord.ContinueWith(task =>
                {
                    return "bad, fault with :" + taskAddRecord.Exception.InnerException;
                }, TaskContinuationOptions.OnlyOnFaulted);
            }
            return content;
        }

        //TODO 目前直接获得当天作为trade date当然不对啦，因为1）当天可能是休息日/节日， 2）即使是工作日当天基金净值计算不出来
        //对于当前项目来说，要获取一个可以得到资金资产净值的最近日期
        //可以通过调用3rd接口去获取tradeDate；一般要是工作日，而且还要维护一个tradeDate列表，支持人肉修改这个列表

        private String lastTradeDateWithData() {
            return DateTime.Now.ToString("yyyyMMdd");
        }

    }
}
