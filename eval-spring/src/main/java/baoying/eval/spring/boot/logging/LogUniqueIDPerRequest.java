package baoying.eval.spring.boot.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import java.util.UUID;

/**
 * - 功能： 给每个request都assign一个uuid. 这样，只要是相同的web context thread打印log，就可以看到这个id，
 *   - 方便调查问题 - ready for use
 *   - 原理是借助于MDC（ThreadContextMap）方法，实现针对指定线程上下文一直有效的一个值。
 *     - 注意：如果这个消息转换为指定的其他entity并放到其他线程池中运行，则这个id就失效了
 *     - 换而言之，只对当前线程有效。
 *     - 一般而言，大部分的消息都是当前线程完成并返回给client的，这种情况下则更合适于添加这个id
 *   - 参考：https://logging.apache.org/log4j/2.x/manual/thread-context.html
 *   - 原理参考：https://medium.com/@d.lopez.j/spring-boot-setting-a-unique-id-per-request-dd648efef2b
 * - 使用ServletRequestListener as that is executed before all filters.
 *   - 这里还提到使用filter，或者interceptor， https://stackoverflow.com/questions/18823241/assign-a-unique-id-to-every-request-in-a-spring-based-web-application
 * - 这里注册为WebListener之后，还需要两个步骤
 *   - TODO - required - global uuid - 更新/添加main类的annotation package信息（指向本package），e.g.@ServletComponentScan(basePackages="baoying.eval.spring.boot.logging")
 *   - TODO - required - global uuid - 在你的log 配置文件layout pattern中，增加这个设定的RequestID，譬如log4j2.xml中 - <PatternLayout pattern="%d{HH:mm:ss.SSS} %X{RequestId} [%t] %-5level %logger{36} - %msg%n"/>
 *      - 参考https://www.baeldung.com/mdc-in-log4j-2-logback
 * - note：本链接例子也很完整： https://blog.csdn.net/mxlgslcd/article/details/89521351
 * - 扩展：客户端可能也通过header发上来co-relationid，这样方便来回消息的对应 P114 section 5.9.1 Manning 2017 - spring microservice in action by John C
 *
 */
@WebListener
@Slf4j
public class LogUniqueIDPerRequest implements ServletRequestListener {

    public void requestInitialized(ServletRequestEvent arg0) {
        log.debug("++++++++++++ REQUEST INITIALIZED +++++++++++++++++");

        MDC.put("RequestId", UUID.randomUUID().toString());

    }

    public void requestDestroyed(ServletRequestEvent arg0) {
        log.debug("-------------REQUEST DESTROYED ------------");
        MDC.clear();
    }
}
