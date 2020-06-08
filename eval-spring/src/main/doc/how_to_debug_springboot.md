[TOC]

# Overview
开发过程中，debug是必不可少步骤，有几种方法

# 方法1：直接intellij启动spring boot，然后debug
  - 这是最简便的方法，不过前提是你的默认配置文件之类的都要弄好
  - 当然，也可以通过更改intellij的'Edit Congiruations'来更改你的各种配置

# 方法2：通过agentlib完成，
- 用于：如果你无法配置intellj去启动这个服务（譬如只能在远端的机器上启动，或者你的intellj配置有问题）
- 如果是mvn启动，则
  - mvn spring-boot:run -Drun.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8787"
    - refer：https://stackoverflow.com/questions/36217949/maven-spring-boot-run-debug-with-arguments
  - Intellij上面直接attach就能发现这个进程了
    - refer：https://www.jetbrains.com/help/idea/attaching-to-local-process.html
    - FAQ：如果intellij这个项目的jdk与进程启动的jdk不同，则可能提示不让attach。记得改成一致的jdk

- 如果是直接在远端启动（譬如在调查某个问题，只能在某些环境重现）
  - 更改你的java启动，增加下面jvm option即可
    - -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8787