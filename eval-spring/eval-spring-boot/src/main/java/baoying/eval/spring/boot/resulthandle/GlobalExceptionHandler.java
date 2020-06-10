package baoying.eval.spring.boot.resulthandle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

//TODO partial - error handing - 转化这里的error为统一的error-code格式

//多个级别的error control
//1. 全局 - 即这里
//2. controller（入口） - 入口那里可能出现一些runtime exception之类的，就转到这里了
//3. TODO： 验证filter和interceptor出现的runtime exception是否会到这里处理

//frame work来自 https://blog.csdn.net/daguanjia11/article/details/80059002
//对返回消息增加 json封装（with error code）
@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //验证输入参数而引发的error
    //from https://mkyong.com/spring-boot/spring-rest-error-handling-example/
    // @Validate For Validating Path Variables and Request Parameters
    @ExceptionHandler({ConstraintViolationException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class})
    public void constraintViolationException(HttpServletResponse response, Exception e) throws IOException {
        logger.error("", e);
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> parameterErrorHandler(HttpServletRequest req, IllegalArgumentException e) {
        logger.error("", e);
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //这里只是一个例子，就是说对于某种类型的Exception，我们可以针对性的处理
    class BusinessException01 extends RuntimeException{
    }
    @ExceptionHandler(BusinessException01.class)
    public void handleBusinessException01(HttpServletResponse response) throws IOException {

        response.sendError(HttpStatus.BAD_REQUEST.value());
        //这个的返回给client的结果就是下面默认的
        //https://mkyong.com/spring-boot/spring-rest-error-handling-example/
        /**
         * {
         * 	"timestamp":"2019-02-27T04:21:17.740+0000",
         * 	"status":404,
         * 	"error":"Not Found",
         * 	"message":"Book id not found : 5",
         * 	"path":"/books/5"
         * }
         */
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> othersErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        String url = req.getRequestURI();
        logger.error("request error at " + url, e);
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}