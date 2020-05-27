package baoying.eval.spring.boot.resulthandle;

public enum ApiErrorCode {
    NOT_EXISTS(400001, "not exists"),
    DUPLICATED_SUBMIT(400002, "duplicated submit");
    private ApiErrorCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
    int code;
    String desc;
}
