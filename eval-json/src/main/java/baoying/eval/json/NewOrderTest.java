package baoying.eval.json;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class NewOrderTest {

    /**
     * https://www.baeldung.com/jackson-object-mapper-tutorial
     * - 注意；SpringBoot的controller中不必手工进行拆装，其自动进行
     *   - 参考我的eval-spring-boot中的OrderController.java
     */
    @Test
    public void testNewOrderJsonMarshalling() throws Exception{

        NewOrder ord = new NewOrder();
        ord.clientName="baoying";
        ord.clientOrderId="baoying0123abcd456789";
        ord.px=new BigDecimal("0.9");
        ord.side=1;
        ord.symbol="IBM";

        // 这个类ObjectMapper是线程安全的，这个jvm用同一个static的即可
        // https://stackoverflow.com/questions/3907929/should-i-declare-jacksons-objectmapper-as-a-static-field
        ObjectMapper objectMapper = new ObjectMapper();
        //ObjectWriter也是线程安全的/immutable的，全局唯一即可。用writter感觉更好，不过其实没关系
        ObjectWriter objectWriter = objectMapper.writer();

        String jsonString = objectWriter.writeValueAsString(ord);
        Assert.assertEquals("","{\"symbol\":\"IBM\"," +
                "\"side\":1," +
                "\"px\":0.9," +
                "\"qty\":0," +
                "\"client_ord_id\":\"baoying0123abcd456789\"," +
                "\"client_name\":\"baoying\"}",
                jsonString);

    }

}