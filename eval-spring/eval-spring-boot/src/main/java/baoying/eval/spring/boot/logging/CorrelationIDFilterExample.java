package baoying.eval.spring.boot.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * 本例子是将发送端得到的请求中特定header，返回回去
 *
 * OncePerRequestFilter与Filter基本相同，不过为了兼容不同container（tomcat，jetter，。。），
 * 以及为了是的forward（或者其他操作？）时候执行一次，就弄了它
 *
 * https://www.cnblogs.com/shanshouchen/archive/2012/07/31/2617412.html
 */
@Component
@Slf4j
public class CorrelationIDFilterExample extends OncePerRequestFilter {

    @Value("${header-correlationid-name:tx-correlation-id}")
    String exampleCorrelationIDName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //TODO - required - 如果使用这个功能，此处的header名称应该从外面传进来，而不是hardcode
        //传进来方式就是通过环境变量设置某个值，譬如启动时候设置java -Dheader-correlationid-name='example-corelation-id'
        //然后本类里面声明一个@Value("${header-correlationid-name:example-corelation-id}")
        //String exampleCorrelationIDName = "example-corelation-id";

        String exampleCorrelationID = request.getHeader(exampleCorrelationIDName);
        log.info("correlation id header {}:{}", exampleCorrelationIDName, exampleCorrelationID);
        if(exampleCorrelationID != null && !exampleCorrelationID.equals("")){
            response.setHeader(exampleCorrelationIDName, exampleCorrelationID);
        }

        //警告：别忘了把下面这行加上，是的filter能够继续走下去
        filterChain.doFilter(request, response);

    }
}
