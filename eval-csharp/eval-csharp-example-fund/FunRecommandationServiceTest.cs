using eval_csharp_example_fund.app;
using eval_csharp_example_fund.db;
using NUnit.Framework;
using System.Threading.Tasks;

namespace eval_csharp_example_fund
{
    class FunRecommandationServiceTest
    {
        private DBService _dbService;
        private IMarketDataService _mdService;
        
        /**
         * 当发现自己的主服务的测试无从下手时，可以清晰意识到自己之前的问题
         * - 没有把精力放在实现业务上，而是更多的关注细节，和语法
         * - 这明确的警示了以后做项目时候要测试先行，可以把问题早早的暴露出来
         * 
         * 如果最早时间就写这个测试，就会发现
         * - 需求需要非常明确，然后再继续
         * - 相关的接口不得不定义，包括dbservice，marketdataservice等待
         */
        public FunRecommandationServiceTest(DBService dbService, IMarketDataService mdServic) {
            this._dbService = dbService;
            this._mdService = mdServic;
        }

        [Test]
        public void addShowLatest_Test()
        {

        }

    }
}
