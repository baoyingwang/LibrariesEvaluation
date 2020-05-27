package baoying.eval.spring.boot.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 打印所有的response - ready for use
 * 下面的log var用的这个annotation-@Slf4j。Intellij需要按章lombok plugin，还要 "Enable Annotation Processing"
 */
@Slf4j
@Component
public class LogAllResponses extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //https://stackoverflow.com/questions/39935190/contentcachingresponsewrapper-produces-empty-response

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);
        String responseContent = new String(responseWrapper.getContentAsByteArray(), UTF_8);
        log.info("track response - status:{}, content type:{}, headers:{}, content size:{}, content:{}", responseWrapper.getStatus(), responseWrapper.getContentType(),
                new ServletServerHttpResponse(responseWrapper).getHeaders(), responseWrapper.getContentSize(), responseContent);
        responseWrapper.copyBodyToResponse();
    }

}
