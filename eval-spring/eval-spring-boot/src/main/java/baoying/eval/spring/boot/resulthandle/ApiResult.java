package baoying.eval.spring.boot.resulthandle;

import lombok.Data;

@Data
public class ApiResult<T> {

    /**
     * OK or ERROR
     */
    private String status;
    private int errorCode;
    private String errorMessage;
    private T data;

    public static <T> ApiResult<T> success(T data){
        ApiResult result = new ApiResult();
        result.status = "OK";
        result.data = data;
        return result;
    }

    public static <T> ApiResult<T> error(ApiErrorCode errorCode, String errorMessage){
        ApiResult result = new ApiResult();
        result.status = "ERROR";
        result.errorCode = errorCode.code;
        result.errorMessage = errorCode.desc+","+errorMessage;
        return result;
    }
}
