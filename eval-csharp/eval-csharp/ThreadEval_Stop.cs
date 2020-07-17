using System;
using System.Collections.Generic;
using System.Text;

namespace eval_csharp
{
    /**
     
     * 
     * - 关于停止一个线程/任务的执行，已经分散在很多地方了，这里做一个汇总的总结
     * - 这里不会把代码重复贴一遍，但是会给出相关代码位置
     * 
     * - 如何停止Thread.Sleep(x milliseconds)
     *     - 通过Thread.Interrupt()
     *     - 参考ThreadEval_basic::testStopThreadSleep()
     * - 如何停止Task.Delay(x milliseconds).Wait() 
     *     - 好像不能停
     * - 如何停止Task.Delay(x milliseconds, CancellationTokenSource cts).Wait()
     *     - 通过cancel token执行Break或者Stop
     *     - 参考ThreadEval_basic::testStopTaskWait()
     * - 如何通过loopstate停止Parallel.For / Parallel.ForEach
     *     - 只能停止那些3个delegates的调用，因为第二个参数是ParallelLoopState，可以用于停止
     *     - https://stackoverflow.com/questions/8818203/what-is-difference-between-loopstate-break-loopstate-stop-and-cancellationt 
     * 
     */
    class ThreadEval_Stop
    {
    }
}
