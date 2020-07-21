using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Threading.Tasks;

namespace eval_csharp_example_fund.db
{

    /**
     * 关于字段上的annotation
     * https://docs.microsoft.com/en-us/ef/core/modeling/entity-properties?tabs=data-annotations%2Cwithout-nrt
     * 
     * 这里讲了如何在代码中添加index/uniq constraint等。
     * https://stackoverflow.com/questions/41246614/entity-framework-core-add-unique-constraint-code-first
     * 但是我还是更偏向于提前准备好数据库建库脚本，因为
     * - 我们还是以数据为中心的，这样数据库有什么升级之类的，可以直接通过sql脚本完成，以确保数据完整性
     * 
     */
    [Table("RankRawCache")]
    class RankRawCache
    {
        /*
        id bigint     IDENTITY(1,1) PRIMARY KEY,
        trade_date       char (8)     , -- YYYYMMDD
        rank_strategy_id int         , -- 1: last week increment percentage
        rank_source varchar(255),
        rank_raw_content varchar(max),
        */
        [Key]
        [Column("id", TypeName= "bigint")]
        public long Id { get; set; }

        [Required]
        [Column("trade_date")]
        [StringLength(50)]
        public String TradeDate { get; set; }

        [Required]
        [Column("rank_strategy_id")]
        public int RankStrategyId { get; set; }

        [Required]
        [StringLength(255)]
        [Column("rank_source")]
        public String RankSource { get; set; }


        //关于varchar(max) https://stackoverflow.com/questions/11235847/how-do-i-set-a-column-in-sql-server-to-varcharmax-using-asp-net-ef-codefirst-d
        //Name: "rank_raw_content" 作为Contructor参数，与 TypeName并存，这写法也没谁了！
        [Required]
        [Column("rank_raw_content", TypeName = "nvarchar(MAX)")]
        public String RankRawContent { get; set; }


    }
}
