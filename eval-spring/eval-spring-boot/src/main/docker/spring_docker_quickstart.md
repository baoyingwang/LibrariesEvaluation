[TOC]
# Overview
- 这里简要介绍通过maven生成docker image以及如何build container情况。
- 这里是基于manning spring microservice in action的chapter2的pom，对其具体的配置增加了详细的解释，以方便日后使用
  - https://github.com/carnellj/spmia-chapter2
  
# 构建spring docker的主要过程
- docker源文件，参考src/main/docker/Dockerfile 和 run.sh
  - 直接参考 manning spring microservice in action 各章的例子中的源码就非常直接
  - 一般来说Dockerfile比较简单，没有太多逻辑
  - run.sh则需要考虑架构上/workflow角度的各种需要，譬如：
    - note：对于all service而言，要确定profile，建议直接使用-Dspring.profiles.active=$PROFILE
    - note：对于app service而言，要知道配置中心位置，通过env传进来-Dspring.cloud.config.uri=$CONFIGSERVER_URI
    - note：对于app service而言，还要知道注册中心位置，通过env传进来：-Deureka.client.serviceUrl.defaultZone=$EUREKASERVER_URI
    - note：参考licensingservice/docker/run.sh 
    
- mvn docker:build通过plugin完成image构建
  - image名称/tag在配置文件中定义
  
- 通过compose来启动测试环境
  - 不建议通过docker container run去启动
  - 因为compose把参数都写好了，非常直观。用docker container run的话，一整行，很多参数，不易读，虽然效果可能一样
  - 还有compose还有帮助与多个系统集成在一起测试
    
# 具体内容

## 源代码：src/main/docker/Dockerfile|run.sh
  - 这里定义了Dockerfile和启动语句。不过里面都使用了变量，所以没有hardcode值
  - Dockerfile和run.sh中的变量替换 @project.build.finalName@
      - 这个变量是maven的内置变量
       - e.g. run.sh 中的 java -jar /usr/local/licensingservice/@project.build.finaleName@.jar

## mvn docker:build通过plugin完成image构建
### maven-resources-plugin , 把target/docker中内容copy到 target/dockerfile
  - target/dockerfile目录用于docker image build
  - target/docker内容，就是来自于源文件：src/main/docker/， compile之后到target中了

### docker-maven-plugin - 进行docker image 构建（container不在里面哈）
  - 其将直接使用target/dockerfile目录，by <dockerDirectory>${basedir}/target/dockerfile</dockerDirectory>
  - dockerfile目录中只有Dockerfile和run.sh两个文件
  - 其他文件通过resources element来指定, 譬如构建的jar就在target目录下（可以参考lib-eval中的mvn pakage之后的输出）

### 创建image - mvn clean package docker:build 
cd spmia-chapter2-master
mvn  clean package docker:build

note:需要java8，因为这个例子依赖java8
note： docker image ls 可以看到它
```
baoyingwang@localhost spmia-chapter2-master % docker image ls 

REPOSITORY                          TAG                 IMAGE ID            CREATED             SIZE

johncarnell/tmx-licensing-service   chapter2            429961688ef2        2 hours ago         142MB

```


## 创建container
这是docker的常识了，使用docker run 或者 compose文件都可以
- 通过docker run创建container
  -  docker run --name licensingservice-by-run-2 -p 8081:8080 -d johncarnell/tmx-licensing-service:chapter2 
  - docker run -d --name container_id -p hostPort:containerPort image_id  
  - docker run  --name container_id -p hostPort:containerPort   -d imangename:imangetag
- 通过compose - 这个在源代码中有定义
直接到compose文件目录执行
docker-compose up 
或者增加 -d (后台运行）
docker-compose up -d
https://beginor.github.io/2017/06/08/use-compose-instead-of-run.html
note: docker-compose rm 清除container/image（？）

```
version: '2'
services:
  licensingservice:
      image: johncarnell/tmx-licensing-service:chapter2
      ports:
        - "8080:8080"

```

## MVN pom.xml例子
```

 <build>
    <plugins>
        <!-- We use the Resources plugin to filer Dockerfile and run.sh, it inserts actual JAR filename -->
        <!-- The final Dockerfile will be created in target/dockerfile/Dockerfile -->
        <plugin>
            <artifactId>maven-resources-plugin</artifactId>
            <executions>
                <execution>
                    <id>copy-resources</id>
                    <!-- here the phase you need -->
                    <phase>validate</phase>
                    <goals>
                        <goal>copy-resources</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>${basedir}/target/dockerfile</outputDirectory>
                        <resources>
                            <resource>
                                <directory>src/main/docker</directory>
                                <filtering>true</filtering>
                            </resource>
                        </resources>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <groupId>com.spotify</groupId>
            <artifactId>docker-maven-plugin</artifactId>
            <version>0.4.10</version>
            <configuration>
                <imageName>${docker.image.name}:${docker.image.tag}</imageName>
                <dockerDirectory>${basedir}/target/dockerfile</dockerDirectory>
                <resources>
                    <resource>
                        <targetPath>/</targetPath>
                        <directory>${project.build.directory}</directory>
                        <include>${project.build.finalName}.jar</include>
                    </resource>
                </resources>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>

```