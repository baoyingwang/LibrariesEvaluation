using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;

namespace eva_csharp
{
    class NullableValueTypeTest
    {

        /**
         * 
         * 这里是为了说明null也可以比较，所以没有Assert
         * 
         */
        [Test]
        public void testNullSwitch() {

            //Nullable值类型
            Nullable<Int32> a = 5;
            Int32? b = null; //Int32? b 这是Nullable<Int32> b的简写
            //这个是想告诉大家，这个奇葩的比较，因为b是null，还是能够进入到else的switch
            if (a > b)
            {
                Console.WriteLine("a > b");
            }
            else
            {
                Console.WriteLine("a < b");
            }
        
        }
    }
}
