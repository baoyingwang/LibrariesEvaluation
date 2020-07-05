package baoying.eval.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Jackson默认只能识别public（有getter/setting）的field
 * 所以加上这个annotation以识别其他的
 * https://www.baeldung.com/jackson-jsonmappingexception
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NewOrder{

    @JsonProperty("client_ord_id")
    String clientOrderId;

    @JsonProperty("client_name")
    String clientName;

    String symbol;
    int side; //1:buy, 2:sell
    BigDecimal px;
    int qty;
}