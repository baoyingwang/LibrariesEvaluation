# 项目介绍
- 对library的评估代码都讲放入本项目
- 如果某些方面比较重要可以/应该独立，则拉出来独立项目，譬如QuickfFixJ

# 其他
- 本项目自身组织，可以作为maven module的一个例子
  - 参考了以下链接和自己做了一些小的改动
    - 基本搭建： https://www.baeldung.com/maven-multi-module
    - module之间的dependency： https://stackoverflow.com/questions/51438402/maven-dependency-resolution-between-modules-during-a-multi-module-project-build
      - 见eval-grpc-server depends on eval-grpc-proto， 主要是dependency的版本号直接使用的${project.version}
    - module中避免hardcode parent版本：https://stackoverflow.com/questions/10582054/maven-project-version-inheritance-do-i-have-to-specify-the-parent-version
    - aggressively 删除了module中生成的各种build/plugin，直接用parent等。即尽量用parent等
  - module之间相互依赖的例子包括
    - eval-grpc-server/eval-grpc-client 都依赖于 eval-grpc-proto
    - eval-vertx 依赖于eval-common 和 eval-perf
  - note：eval-common的引入是为了那些公用的代码和dependency
    - 目前只有logging的dependency定义在了eval-common中  
    - 没有把这些东西放在Parent里面，这事情见仁见智吧