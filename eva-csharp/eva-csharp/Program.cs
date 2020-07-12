using System;

namespace eva_csharp
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

            BoxingUnboxingEval boxingUnboxingEval = new BoxingUnboxingEval();
            boxingUnboxingEval.eval();

            VirtualMethodEval virtualMethodEval = new VirtualMethodEval();
            virtualMethodEval.eval();


            Int32? a = 5;
            Int32? b = null;
            if (a > b)
            {
                Console.WriteLine("a > b");
            }
            else {
                Console.WriteLine("a < b");
            }

        }
    }
}
