package baoying.eval.spring.boot.logging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * 打印所有的request - ready for use
 *
 * https://www.baeldung.com/spring-http-logging
 * 1. 添加此bean
 * 2. TODO - required - log all requests - 添加logger定义，主要是确定log level为DEBUG- 下面例子是因为我用的log4j2。如果用默认的logbak，xml略有不同
 * <logger name="baoying.eval.spring.boot.logging.LogAllRequests" level="DEBUG"></logger>
 * 也可以application.properties中增加
 *  logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
 * 3. TODO - required- log all requests - 增加 -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager ，保证spring内部的logging转到了我们应用所使用的log4j2.
 *
 */
@Configuration
public class LogAllRequests extends CommonsRequestLoggingFilter{

    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix){
        String loggingMsg = super.createMessage(request, prefix, suffix);
        //这里是一个例子，可以对即将打印的消息做一些更新，譬如去除一些敏感信息
        String safeLoggingMessage = loggingMsg==null?null:loggingMsg.replaceFirst("Signature=.*?&","Signature=hided");
        return safeLoggingMessage;
    }
    //https://www.javadevjournal.com/spring/log-incoming-requests-spring/
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {

        CommonsRequestLoggingFilter filter = new LogAllRequests();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}