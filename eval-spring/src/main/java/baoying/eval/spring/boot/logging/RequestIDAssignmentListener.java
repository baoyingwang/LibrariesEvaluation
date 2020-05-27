package baoying.eval.spring.boot.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import java.util.UUID;

/**
 * - 功能： 给每个request都assign一个uuid，并且打印在log中，方便调查问题 - ready for use
 * - 使用ServletRequestListener as that is executed before all filters.
 *   - 这里还提到使用filter，或者interceptor， https://stackoverflow.com/questions/18823241/assign-a-unique-id-to-every-request-in-a-spring-based-web-application
 * - 这里注册为WebListener之后，还需要两个步骤
 *   - TODO - required - global uuid - 更新/添加main类的annotation package信息（指向本package），e.g.@ServletComponentScan(basePackages="baoying.eval.spring.boot.logging")
 *   - TODO - required - global uuid - 在你的log 配置文件layout pattern中，增加这个设定的RequestID，譬如log4j2.xml中 - <PatternLayout pattern="%d{HH:mm:ss.SSS} %X{RequestId} [%t] %-5level %logger{36} - %msg%n"/>
 *      - 参考https://www.baeldung.com/mdc-in-log4j-2-logback
 * - note：本链接例子也很完整： https://blog.csdn.net/mxlgslcd/article/details/89521351
 */
@WebListener
@Slf4j
public class RequestIDAssignmentListener implements ServletRequestListener {

    public void requestInitialized(ServletRequestEvent arg0) {
        log.debug("++++++++++++ REQUEST INITIALIZED +++++++++++++++++");

        MDC.put("RequestId", UUID.randomUUID().toString());

    }

    public void requestDestroyed(ServletRequestEvent arg0) {
        log.debug("-------------REQUEST DESTROYED ------------");
        MDC.clear();
    }
}
