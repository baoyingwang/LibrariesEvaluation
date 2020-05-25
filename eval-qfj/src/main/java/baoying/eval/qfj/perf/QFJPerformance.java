package baoying.eval.qfj.perf;


import org.junit.Test;
import quickfix.DataDictionary;
import quickfix.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class QFJPerformance {


    @Test
    public void testUnmarshal() throws Exception{

        String fixMessageERX = "8=FIXT.1.1%9=219%35=8%34=2%49=BaoyingMatchingCompID%52=20180104-18:11:35.920%56=LxTxCx_FIX_RT_B1515089489728_0%11=LxTxCx_FIX_RT_B1515089489728_01515089495916_1%14=0%17=18664163457445%37=1515089370474_119%39=0%54=1%55=USDJPY%150=0%151=2%10=035%";
        String fixMessage = fixMessageERX.replace('%',(char)0x01);

        String messageData = fixMessage;
        DataDictionary dd = new DataDictionary("FIX50SP1.xml");

        //if false, checksum(and maybe others) will NOT be validated.
        boolean doValidation = true;

        int loopCount = 1000_000;

        long start = System.nanoTime();
        for(int i=0; i<loopCount; i++ ){
            Message x = new Message();
            x.fromString(messageData,dd,doValidation);
        }
        long end = System.nanoTime();

        //avg: 2370 nano seconds
        System.out.println("avg nano second per quickfixj.Message.fromString():" + (end - start)/loopCount);

    }

    @Test
    public void testMarshal() throws Exception{


        Message x = buildHarcodedNewOrderSingleForTest();
        int loopCount = 1000_000;

        long start = System.nanoTime();
        for(int i=0; i<loopCount; i++ ){
            x.toString();
        }
        long end = System.nanoTime();
        //avg : 1681 nano seconds
        System.out.println("avg nano second per quickfixj.Message.toString():" + (end - start)/loopCount);

    }

    static Message buildHarcodedNewOrderSingleForTest() {
        /**
         * <message name="NewOrderSingle" msgtype="D" msgcat="app">
         * <field name="ClOrdID" required="Y"/>
         * <component name="Instrument" required="Y"/>
         * <field name="Side" required="Y"/>
         * <field name="TransactTime" required="Y"/>
         * <component name="OrderQtyData" required="Y"/>
         * <field name="OrdType" required="Y"/> </message>
         */
        // NewOrderSingle
        Message newOrderSingle = new Message();
        // It is not required to set 8,49,56 if you know SessionID. See
        // DefaultSQFSingleSessionInitiator.java

        newOrderSingle.getHeader().setString(35, "D");
        newOrderSingle.setString(11, "ClOrdID_" + System.currentTimeMillis());
        newOrderSingle.setString(55, "USDJPY"); // non-repeating group
        // instrument->Symbol 55
        newOrderSingle.setString(54, "1");// Side 54 - 1:buy, 2:sell
        newOrderSingle.setUtcTimeStamp(60, LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC), true); // TransactTime
        newOrderSingle.setString(38, "200"); // non-repeating group
        // OrderQtyData->OrderQty
        // 38
        newOrderSingle.setString(40, "1"); // OrdType 1:Market

        return newOrderSingle;

    }
}
