# 项目介绍
- 对library的评估代码都讲放入本项目
- 如果某些方面比较重要可以/应该独立，则拉出来独立项目，譬如QuickfFixJ
- TODO
  - Mock - 1)mockito, 2)easymock/powermock

# 其他
- 本项目自身组织，可以作为maven module的一个例子
  - 参考了以下链接和自己做了一些小的改动
    - 基本搭建： https://www.baeldung.com/maven-multi-module
    - module之间的dependency： https://stackoverflow.com/questions/51438402/maven-dependency-resolution-between-modules-during-a-multi-module-project-build
      - 见eval-grpc-server depends on eval-grpc-proto， 主要是dependency的版本号直接使用的${project.version}
    - module中避免hardcode parent版本：https://stackoverflow.com/questions/10582054/maven-project-version-inheritance-do-i-have-to-specify-the-parent-version
      - root/pom.xml定义了revision这个properties，在自身和module的pom中都应用这个版本号
    - aggressively 删除了module中生成的各种build/plugin，直接用parent等。即尽量用parent等
  - module之间相互依赖的例子包括
    - eval-grpc-server/eval-grpc-client 都依赖于 eval-grpc-proto
    - eval-vertx 依赖于eval-common 和 eval-perf
    - note：eval-common的引入是为了那些公用的代码和dependency
      - 目前只有logging的dependency定义在了eval-common中  
      - 没有把这些东西放在Parent里面，这事情见仁见智吧
  - deploy-上传到本地nexus中
    - 仅需做两件事情
      - 1 - 在root/pom.xml中增加nexus相关配置，定义于element：distributionManagement - 见 root/pom.xml
      - 2 - 在你的maven settings.xml中，增加nexus用户信息，否则会提示401 认证错误
      - 参考： https://blog.csdn.net/woloqun/article/details/88825552
      - note：如果你都配置好了，还是出现401问题，执行mvn help:effective-settings，确认的mvn的setting配置文件，可能你的xml格式写错了之类的。
        - 当然，你的用户名/密码应该先检查一下
    - 解决无法上传javadoc和source问题，通过定义他们（maven-source-plugin/maven-javadoc-plugin）于install phase， 避免了deploy时候他们还没有执行。
      - install步骤在deploy之前，这保证了deploy（上传）之前，source jar和java doc jar已经生成
      - 见root/pom.xml。 在eval-common/pom.xml, eval-vertx/pom.xml中，只是引用了他们，不需要特别配置，即完成了这两个module的source和doc的build

# 特别感谢
- 项目中很多代码和文章都参考了网上的已有的东西
  - 所有使用到的地方，我都加上来来源的链接
  - 但是百密一疏，如果有遗漏请务必通知我，将增加相关链接，或者删除（如果您不喜欢我这样参考您的内容的话）