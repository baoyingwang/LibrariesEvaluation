using System;
using System.Linq;

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

            foreach (var x in Enumerable.Range(11, 4)) {
                Console.WriteLine(x);
            }

        }
    }
}
