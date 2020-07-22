using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text;

namespace eval_csharp_example_fund.db
{

    [Table("Position")]
    class Position
    {
        [Key]
        //[Column("id", TypeName = "bigint")]
        [Column("id")] //sqlite 不支持bigint
        public long Id { get; set; }

        [Required]
        //https://stackoverflow.com/questions/45759143/how-to-insert-a-record-into-a-table-with-a-foreign-key-using-entity-framework-in
        //注意，这个Foreign key会关联到FundInfo的主键上面。如果其主键设定为自增id，则逻辑就错误了
        [ForeignKey("fund_id")] 
        [StringLength(255)]
        public FundInfo FundInfo { get; set; }

        [Required]
        [Column("amount", TypeName = "decimal(18,0)")]
        public double Amount { get; set; }
    }
}
