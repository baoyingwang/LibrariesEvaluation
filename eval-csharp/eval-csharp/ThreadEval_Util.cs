using System;
using System.Threading;
using System.Threading.Tasks;

namespace eval_csharp
{
    class ThreadEval_Util
    {
        //这是一个Utility方法
        public static void TraceThreadAndTask(String info)
        {
            String taskInfo = Task.CurrentId == null ? "no task" : "task " + Task.CurrentId;
            String datetimeFormat = "yyyy.MM.dd HH:mm:ss:fff";
            Console.WriteLine($"{DateTime.Now.ToString(datetimeFormat)} {info} in thread {Thread.CurrentThread.ManagedThreadId}" + $" and {taskInfo}");
        }

    }
}
