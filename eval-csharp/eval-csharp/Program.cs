﻿using System;

namespace eval_csharp
{
    struct Point {
        public Int32 x, y;

        override
        public String ToString() {
            return x + "," + y;
        }
    }
    class Program
    {
        static void Main(string[] args)
        {

            Console.WriteLine("args length:"+ args.Length);
            //VirtualMethodEval virtualMethodEval = new VirtualMethodEval();
            //virtualMethodEval.eval();
            eval_csharp.LinqExample.Program.Main(null);

        }
    }
}
