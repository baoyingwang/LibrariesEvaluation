using System;
namespace eval_csharp
{
    public class Field_vs_Property
    {
        public Field_vs_Property()
        {
        }

        //这个代码例子来自于：https://stackoverflow.com/questions/295104/what-is-the-difference-between-a-field-and-a-property

        /**
         * 
         * - C#中的Field和Property的概念，对于Java程序员背景的人来说，有点绕
         *   - 不过，其实不用理解它直接用就行了，不耽误事儿
         *   - 下面是非常好的例子
         *   - 来自于https://stackoverflow.com/questions/295104/what-is-the-difference-between-a-field-and-a-property
         * - 简而言之
         *   - field就是Java中的field
         *   - property就是对外发布的field的方式
         * 
         * 
         */

        // this is a field.  It is private to your class and stores the actual data.
        private string _myField;

        // this is a property. When accessed it uses the underlying field,
        // but only exposes the contract, which will not be affected by the underlying field
        public string MyProperty
        {
            get
            {
                return _myField;
            }
            set
            {
                _myField = value;
            }
        }

        // This is an AutoProperty (C# 3.0 and higher) - which is a shorthand syntax
        // used to generate a private field for you
        public int AnotherProperty { get; set; }
    }
}
