using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace eval_csharp_example_fund.db
{

    [Table("FundInfo")]
    class FundInfo
    {

        //id bigint     IDENTITY(1,1) PRIMARY KEY,
        //fund_id   varchar(255) not null,
        //fund_name varchar(255) not null,
        //lastday_date    char (8),
        //lastday_price decimal (32,3),


        //[Key]
        //[Column("id", TypeName = "bigint")]
        //public long Id { get; set; }

        [Key]
        [Required]
        [Column("fund_id")]
        [StringLength(255)]
        public String FundId { get; set; }

        [Required]
        [Column("fund_name")]
        [StringLength(255)]
        public String FundName { get; set; }

        [Required]
        [Column("lastday_date", TypeName = "char(8)")]
        [StringLength(8)]
        public String LastdayDate { get; set; }

        [Required]
        [Column("lastday_price", TypeName = "decimal(32,3)")]
        public double LastdayPrice { get; set; }
    }
}
