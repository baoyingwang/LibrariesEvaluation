package baoying.eval.spring.boot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServiceConfig {

  @Value("${example.property}")
  private String exampleProperty;

  public String getExampleProperty(){
    return exampleProperty;
  }

  @PostConstruct
  public void afterServiceConfigConstruct(){
    System.out.println("example.property:" + exampleProperty);
  }
}
