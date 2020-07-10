using System;
namespace eva_csharp
{
    public class BoxingUnboxingEval
    {
        public BoxingUnboxingEval()
        {
        }
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



            Console.WriteLine(p.x);
            Console.WriteLine(p2.x);

            Console.WriteLine(o.ToString());
            Console.WriteLine(o2.ToString());


        }
    }
}


