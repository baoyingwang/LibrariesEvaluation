using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace eval_csharp
{
    /**
     * 官方文档比较清楚，这里暂时作为place-holder了
     * https://docs.microsoft.com/en-us/dotnet/standard/serialization/system-text-json-how-to
     * https://docs.microsoft.com/en-us/dotnet/standard/serialization/system-text-json-how-to#enums-as-strings
     * - 注意：enum的值要特别处理一下，可以自己写converter
     */
    class JsonEval
    {

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
            public Int32 pageIndex { get; set; } = 1; //Property默认值
            public Int32 pageSize { get; set; } = 10; //Property默认值

        }

        [Test]
        public void testSerialization() {
            //{"fundType":[],"sort":"lastWeekGrowth","fundCompany":[],"createTimeLimit":0,"fundScale":0,"asc":0,"pageIndex":1,"pageSize":10}
            RankRequest request = new RankRequest
            {
                sortBy = "lastWeekGrowth"
            };

            var jsonString = JsonSerializer.Serialize(request);
            var expectedString = "{\"fundType\":null,\"sort\":\"lastWeekGrowth\",\"fundCompany\":null,\"createTimeLimit\":0,\"fundScale\":0,\"asc\":0,\"pageIndex\":1,\"pageSize\":10}";
            Assert.AreEqual(expectedString, jsonString);
        }

        [Test]
        public void testDeSerialization()
        {
            var jsonString = "{\"fundType\":null,\"sort\":\"lastWeekGrowth\",\"fundCompany\":null,\"createTimeLimit\":0,\"fundScale\":0,\"asc\":0,\"pageIndex\":1,\"pageSize\":10}";
            var request = JsonSerializer.Deserialize<RankRequest>(jsonString);

            Assert.AreEqual(null, request.fundTypes);
            Assert.AreEqual("lastWeekGrowth", request.sortBy);
            Assert.AreEqual(null, request.fundCompanies);

            Assert.AreEqual(0, request.createTimeLimit);
            Assert.AreEqual(0, request.fundScale);
            Assert.AreEqual(0, request.asc);
            Assert.AreEqual(1, request.pageIndex);
            Assert.AreEqual(10, request.pageSize);
        }
    }
}
