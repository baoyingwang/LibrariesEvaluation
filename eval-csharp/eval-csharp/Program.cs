using System;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;

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

            foreach (var x in Enumerable.Range(11, 1)) {
                Console.WriteLine(x);
            }

            foreach (var x in Enumerable.Range(11, 4)) {
                Console.WriteLine(x);
            }

            //0,1,2,3,4
            Parallel.For(0, 5, i=>{
                Console.WriteLine($"===>{i}");
            });

            _ = Parallel.For(0, 100, (i, loopState) =>
              {
                  Console.WriteLine($"begin:{i}");
                  if (i >= 10)
                      loopState.Break();
                  Console.WriteLine($"end:{i}");
              });

            _ = Parallel.For(0, 100, (i, loopState) =>
            {
                Console.WriteLine($"==>begin:{i}");
                if (i >= 10)
                    loopState.Stop();
                Console.WriteLine($"==>end:{i}");
            });
        }

    }
}
