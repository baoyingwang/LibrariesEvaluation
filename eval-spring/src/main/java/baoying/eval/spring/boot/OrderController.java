package baoying.eval.spring.boot;

import baoying.eval.spring.boot.resulthandle.ApiErrorCode;
import baoying.eval.spring.boot.resulthandle.ApiResult;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static baoying.eval.spring.boot.resulthandle.ApiErrorCode.DUPLICATED_SUBMIT;

/**
 * 比hello world略微复杂一点点的例子，read for reference
 * 更多例子，参考 https://spring.io/guides/tutorials/rest/
 */
@RestController
public class OrderController {

    //Jackson默认只能识别public（有getter/setting）的field
    //所以加上这个annotation以识别其他的
    //https://www.baeldung.com/jackson-jsonmappingexception
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static class NewOrder{

        @JsonProperty("client_ord_id")
        String clientOrderID;

        @JsonProperty("client_name")
        String clientName;

        String symbol;
        int side; //1:buy, 2:sell
        BigDecimal px;
        int qty;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static class NewOrderResult{

        @JsonProperty("ord_id")
        String orderId; //在数据库中的id
        NewOrder order;
    }

    //Key: clientOrderId
    Map<String, NewOrderResult> ordersByOrdID = new ConcurrentHashMap();
    Map<String, NewOrderResult> ordersByClientID = new ConcurrentHashMap();

    //下单前价格在前段都计算好了，为了防止被攻击内部应该有个再次核查的功能
    @PostMapping("/v1/order")
    public ApiResult<NewOrderResult> newOrder(@RequestBody NewOrder order) {
        int errorCode = 40001;
        if(ordersByClientID.containsKey(order.clientOrderID)){
            return ApiResult.error(DUPLICATED_SUBMIT, "order already exists, order.clientOrderID:"+order.clientOrderID);
        }

        String orderId = String.valueOf(System.currentTimeMillis());

        NewOrderResult result = new NewOrderResult();
        result.orderId = orderId;
        result.order = order;

        ordersByOrdID.put(result.orderId, result);
        ordersByClientID.put(order.clientOrderID, result);
        return ApiResult.success(result);
    }

    @GetMapping("/v1/order/{orderID}")
    public ApiResult<NewOrderResult> queryOrderByOrderID( @PathVariable String orderID) {

        if(! ordersByOrdID.containsKey(orderID)){
            ApiResult.error(ApiErrorCode.NOT_EXISTS,"not find order from order id:"+orderID);
        }

        return ApiResult.success(ordersByOrdID.get(orderID));
    }

    @GetMapping("/v1/order/query_by_clientordid/{clientOrdID}")
    public ApiResult<NewOrderResult> queryOrderByClientID( @PathVariable String clientOrdID) {

        if(! ordersByClientID.containsKey(clientOrdID)){
            ApiResult.error(ApiErrorCode.NOT_EXISTS,"not find order from client order id:"+clientOrdID);
        }

        return ApiResult.success(ordersByClientID.get(clientOrdID));
    }
}
