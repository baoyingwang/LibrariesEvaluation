using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace eval_csharp_example_fund.app
{
    interface IMarketDataService
    {
        public Task<String> lastWeekGrowthRankAsync();
        public Task<String> lastMonthGrowthRankAsync();
    }
}
