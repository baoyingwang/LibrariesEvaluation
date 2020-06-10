[TOC]
# Overview
各种小问题

# 问题列表
## 为啥fat jar没有build出来？
https://stackoverflow.com/questions/52750248/spring-boot-application-is-not-creating-fat-uber-jar
增加：<goal>repackage</goal>
e.g.
```
     <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring.version}</version>
        <executions>
          <execution>
            <goals>
              <goal>repackage</goal>
            </goals>
            <configuration>
              <mainClass>
                baoying.eval.spring.boot.Application
              </mainClass>
            </configuration>
          </execution>
        </executions>
      </plugin>
```