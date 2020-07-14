using System;

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
            VirtualMethod_ClassEval virtualMethodEval = new VirtualMethod_ClassEval();
            virtualMethodEval.eval();
        }
    }
}
