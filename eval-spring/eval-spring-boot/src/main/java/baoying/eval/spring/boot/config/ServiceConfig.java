package baoying.eval.spring.boot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Sl4j(import lombok.extern.slf4j.Slf4j) - 1) intellij 要安装lombok 插件 2）然后代码中就能够使用log了（不要用再声明）
 */
@Slf4j
@Component
public class ServiceConfig {

  @Value("${example.property}")
  private String exampleProperty;

  public String getExampleProperty(){
    return exampleProperty;
  }

  @PostConstruct
  public void afterServiceConfigConstruct(){
    log.info("example.property:" + exampleProperty);
  }
}
