using System.Globalization;

namespace Chapter11.Model
{
    public class Project
    {
        public string Name { get; set; }

        public override string ToString()
        {
            //TODO 难道都要加上CultureInfo.InvariantCulture么？默认的行不？这个好烦啊
            //更改某个线程的默认culture：Thread.CurrentThread.CurrentCulture = System.Globalization.CultureInfo.InvariantCulture; from https://stackoverflow.com/questions/12729922/how-to-set-cultureinfo-invariantculture-default/52237343
            //但是这种代码太难维护了
            //
            //如何设定ignore culture：https://stackoverflow.com/questions/24661362/formatting-datetime-ignore-culture
            return string.Format(CultureInfo.InvariantCulture, "Project: {0}", Name);
        }
    }
}
