using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace eval_csharp.LinqEval
{
    static class EnumerableExtension
    {

        public static String logFile = null;
        public static IEnumerable<T> LogQuery<T>(this IEnumerable<T> sequence, string tag)
        {
            using (var writer = File.AppendText(logFile??"debug.log"))
            {
                var line = $"{DateTime.Now} {tag}";
                writer.WriteLine(line);
                //Console.WriteLine(line);
            }
            return sequence;
        }

        public static IEnumerable<T> LogQuery<T>(this IEnumerable<T> sequence, List<String> callTrace, string tag)
        {
            callTrace.Add(tag);
            return sequence;
        }

        public static void LogQuery(string any)
        {
            // File.AppendText creates a new file if the file doesn't exist.
            using (var writer = File.AppendText(logFile ?? "debug.log"))
            {
                var line = $"{DateTime.Now} {any}";
                writer.WriteLine(line);
                //Console.WriteLine(line);
            }

        }
    }


}
