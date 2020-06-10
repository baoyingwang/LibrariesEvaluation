
# 1. 如何使用log4j2替换默认的logback
- 很简单，使用引入log4j2, exclude原有的logging， 参考https://docs.spring.io/spring-boot/docs/current/reference/html/howto-logging.html
- 注意：在系统环境变量中添加 -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager以保证spring内部的logging也转到了log4j2上面来（这个在上面文档中也提了，但是还有有人碰到问题，如https://stackoverflow.com/questions/32161616/java-util-logging-redirection-to-log4j2-not-working-for-spring-boot-applications）。
- 注意：如果没加，你自己的代码能够正常使用log4j2，但是spring内部的代码则可能无法打印一些log（譬如https://www.javadevjournal.com/spring/log-incoming-requests-spring/的CommonRequestLoggingFilter就是使用spring内置的logger打印log，则必须保证添加那个环境变量才行）


# 2. 打印每一个Request和Response
- 没想到log所有的request/response竟然是个有点困难的事情，对于内在支持rest api的spring boot来说。
譬如https://www.baeldung.com/spring-http-logging中提到的几个方法（不是很完备的方法）
- 注册一个interceptor。但是因为request只能读取一次，使用一个wrapper：ContentCachingRequestWrapper(源代码：https://github.com/spring-projects/spring-framework/blob/master/spring-web/src/main/java/org/springframework/web/util/ContentCachingRequestWrapper.java)。但是这个request wrapper只支持Content-Type:application/x-www-form-urlencoded + Method-Type:POST。 可以logrequest/response，但是（wrapper）限制太大了。Note：response也有类似的读一次的限制（但是可以通过ContentCachingResponseWrapper破除）。
- OR 使用CommonsRequestLoggingFilter， 但是只支持打印request
- 听起来上述两种方法加一块，第一个打印response，第二个打印request正好，哈哈哈。试过了第二个，挺好。
- 还有个办法就是自己写一个Wrapper来替换上面第一种方法的wrapper以支持更多的content-type和http method。

- 敲黑板：Interceptor无法实现response的打印，只能使用filter - 参考https://stackoverflow.com/questions/45767425/how-to-use-contentcachingresponsewrapper-to-read-httpservletresponse?noredirect=1中的留言区讨论。


- 这里有很多讨论https://stackoverflow.com/questions/7952154/spring-resttemplate-how-to-enable-full-debugging-logging-of-requests-responses
https://stackoverflow.com/questions/48301764/how-to-log-all-the-request-responses-in-spring-rest
- 这个哥们通过实现一个filter做到了（但是好像？没考虑response二次读取问题）https://gist.github.com/int128/e47217bebdb4c402b2ffa7cc199307ba
- 通过借助apache common io的tee output stream，更简单https://stackoverflow.com/questions/2158647/logging-response-body-html-from-httpservletresponse-using-spring-mvc-handlerin/2171633#2171633 。 但是好像还是有二次读取问题（第一次读完position挪到最后了，无法第二次读取）
- 很多人写代码/写博客，如http://laiyijie.me/2017/07/13/spring-accesslog-filter/， https://gist.github.com/baberali/59ad73b2a1dcc5042e53f32585770df7


- 另外：对于使用RestTemplate（not spring boot）的project来说，可以使用下面的方法。注意，其在StreamUtils.copyToString的时候会导致二次读取问题。可以看看能否有类似于ContentCachingResponseRwapper的方法来解决这个问题。作者并没有提这个问题，但留言区好几个人提了。
https://objectpartners.com/2018/03/01/log-your-resttemplate-request-and-response-without-destroying-the-body/


# 打印request 代码如下（直接写到spring boot代码中，不需要单独配置什么，自动发现-除了注意Logging生效意外）
- 注意：使用 -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager以保证spring内部的logging转到了我们应用所使用的log4j2.
- 注意：这个例子中，我们集成了这个filter，这是为了在打印之前，将url中的signature去除掉（不想在log中打印signaure）。如果你没有这个需求，则不需要集成，直接返回new CommonsRequestLoggingFilter就好了
- 注意：使用和不使用继承，log的配置文件使不同的
#logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
logging.level.com.xxx.interceptor.LogRequest=DEBUG
- 注意：这个代码可以直接拿来使用 
```
package baoying.eval.spring.boot.logging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
​
import javax.servlet.http.HttpServletRequest;
​
/**
 * 打印所有的request
 *
 * https://www.baeldung.com/spring-http-logging
 * 1. 添加此bean
 * 2. 添加logger定义，主要是确定log level为DEBUG- 下面例子是因为我用的log4j2。如果用默认的logbak，xml略有不同
 * <logger name="baoying.eval.spring.boot.logging.LogAllRequests" level="DEBUG">
 * </logger>
 * 也可以application.properties中增加
 *  logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
 * 3. 增加 -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager ，保证spring内部的logging转到了我们应用所使用的log4j2.
 *
 */
@Configuration
public class LogAllRequests extends CommonsRequestLoggingFilter{
​
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
​
        CommonsRequestLoggingFilter filter = new LogAllRequests();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}
```

 # 打印Response代码 （直接写到spring boot代码中，不需要单独配置什么，自动发现）
//下面中line 31的log var是Sl4J的annotation，需要intellj按照lombok插件，并且enable annotation processing
//https://stackoverflow.com/questions/14866765/building-with-lomboks-slf4j-and-intellij-cannot-find-symbol-log/46934215#46934215
//https://stackoverflow.com/questions/14866765/building-with-lomboks-slf4j-and-intellij-cannot-find-symbol-log
```
package baoying.eval.spring.boot.logging;
​
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
​
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
​
import java.io.IOException;
​
import static java.nio.charset.StandardCharsets.UTF_8;
​
//打印所有的response
//下面的log var用的这个annotation-@Slf4j。Intellij需要按章lombok plugin，还要 "Enable Annotation Processing"
@Slf4j
@Component
public class LogAllResponses extends OncePerRequestFilter {
​
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
​
        //https://stackoverflow.com/questions/39935190/contentcachingresponsewrapper-produces-empty-response
​
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
​
        filterChain.doFilter(requestWrapper, responseWrapper);
        String responseContent = new String(responseWrapper.getContentAsByteArray(), UTF_8);
        log.info("track response - status:{}, content type:{}, headers:{}, content size:{}, content:{}", responseWrapper.getStatus(), responseWrapper.getContentType(),
                new ServletServerHttpResponse(responseWrapper).getHeaders(), responseWrapper.getContentSize(), responseContent);
        responseWrapper.copyBodyToResponse();
    }
​
}
​````




