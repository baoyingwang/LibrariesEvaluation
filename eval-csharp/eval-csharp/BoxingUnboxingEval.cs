using System;
using NUnit.Framework;

namespace eval_csharp
{

    /**
     * 
     *值类型和class类型还有很大区别，譬如
     *  - 值类型默认stack上分配内存，但是装箱后则在heap上有一份。装箱一般是cast成object时候（方法调用时候类型为object）
     *  - 值类型不用new，也没有nullpointer，直接默认分配了空间
     * 
     */
    public class BoxingUnboxingEval
    {
        public BoxingUnboxingEval()
        {
        }

        /**
         * 这里没有assert什么东西，只是通过comments来解释装箱/拆箱
         * 
         */
        [Test]
        public void eval()
        {
            //验证装箱（boxing - stack=》heap）、拆箱（heap=》stack）
            Point p;
            p.x = p.y = 1;

            Object o = p; //装箱（boxing - stack=》heap）

            p = (Point)o; //拆箱（heap=》stack）

            Point p2 = (Point)o;
            p2.x = 2;
            Object o2 = p2; //装箱（boxing - stack=》heap） again - because we cannot change the o.x directly, since no such signature


        }
    }
}


