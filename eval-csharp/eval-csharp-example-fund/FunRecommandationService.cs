using eval_csharp_example_fund.db;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace eval_csharp_example_fund
{
    class FunRecommandationService
    {
        private DBService _dbService;
        private MarketDataService _mdService;
        
        public FunRecommandationService(DBService dbService, MarketDataService mdServic) {
            this._dbService = dbService;
            this._mdService = mdServic;
        }

        public async Task addPositionsExampleAsync() 
        {
            //TODO 如果添加多个position，又需要在同一个tx里面，怎么做？
            var p1 = await _dbService.QueryPositionAsync("000001");
            if (p1 == null)
            {
                await _dbService.AddPositionAsync("000001", 700);
            }
            else
            {
                Console.Write("000001 position already there, skip insert");
            }

        }

    }
}
