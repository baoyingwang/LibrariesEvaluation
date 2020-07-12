using System;
using NUnit.Framework;

namespace eva_csharp
{
    //这是带测试的delegation
    internal delegate void Feedback(Int32 value);

    [TestFixture]
    public class DelegationEval
    {
        
        [SetUp]
        public void SetUp()
        {
            Console.WriteLine("unit test setup");
        }

        [TearDown]
        public void TearDown()
        {
            Console.WriteLine("unit tear down");

        }

        private void Counter(Int32 from, Int32 to, Feedback fb) {
            for (Int32 val = from; val <= to; val++) {
                //与fb?(val)一样
                if (fb != null) {
                    fb(val);
                }
            }
        }

        [Test]
        public void SimpleDelegation()
        {
            Feedback fb1 = new Feedback(FeedbackToConsole1);
            Counter(1, 2, fb1);

        }

        [Test]
        public void SimpleDelegationChain()
        {
            Feedback fb1 = new Feedback(FeedbackToConsole1);
            Feedback fb2 = new Feedback(FeedbackToConsole2);
            Feedback fb3 = new Feedback(FeedbackToConsole3);

            Feedback fbChain = null;
            fbChain += fb1;
            fbChain += fb2;
            fbChain += fb3;

            Counter(1, 2, fbChain);
        }


        [Test]
        public void AddDuplicateDelegation()
        {
   
            Feedback fb1 = new Feedback(FeedbackToConsole1);
            Feedback fb2 = new Feedback(FeedbackToConsole2);
            Feedback fb3 = new Feedback(FeedbackToConsole3);

            Feedback fbChain = null;
            fbChain += fb1;
            fbChain += fb2;
            fbChain += fb3;
            fbChain += fb1; //fb1又添加了一次，什么影响
            
            Counter(1, 2, fbChain);

        }

        [Test]
        public void BoolConverterTest2()
        {
            Assert.That(true, Is.EqualTo(true));
        }

        private static void FeedbackToConsole1(Int32 value) {
            Console.WriteLine("Console 1 : Item = " + value);
        }

        private static void FeedbackToConsole2(Int32 value)
        {
            Console.WriteLine("Console 2 : Item = " + value);
        }

        private static void FeedbackToConsole3(Int32 value)
        {
            Console.WriteLine("Console 3 : Item = " + value);
        }
    }

    
}
