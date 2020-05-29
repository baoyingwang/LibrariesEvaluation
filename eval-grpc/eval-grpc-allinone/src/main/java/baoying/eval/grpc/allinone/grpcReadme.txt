基于例子：https://www.baeldung.com/grpc-introduction
其代码于：https://github.com/eugenp/tutorials/tree/master/grpc
- 其基本思路
- proto文件定义API接口
- proto文件中还定义生成的stub java问价的java package
- server/client都按照proto完成代码，也使用proto生成都stub文件
  - stub各自生成就行，也不必单独生成出来

我的更改：
1. 更改了package名称
  -proto文件中的对应package要改，因为生成源代码时候需要这个目录
2. pom.xml中增加了解决@javax.annotation.Generated问题的dependency - tomcat
  - 参考https://stackoverflow.com/questions/59079616/generated-protoc-file-creates-a-target-source-with-error

开发中的代码方案
方案0 - server/proto文件/client都在一个项目中
- 简单方便，但是都揉在一起，项目早起可以这么做，快速做出原型，尤其是1个人负责所有事情的时候
- 一旦增加人手，则有必要把client代码，甚至proto文件也分离出去，这样分别开发

方案1 - server与proto文件放在同一个项目中，一起release；client为自己的项目
- server: proto文件于server放在一起源代码管理
- proto文件作为release的一个单独部分拿出来，给client使用
- client拿着带版本的proto文件，自己编译成java代码或者别的代码，因为理论上client可以是任何语言
方案1.2 - server/proto文件/client都是各自的项目，各自编号
- 更合理的安排是proto文件单独一个项目，自己的版本号，则server通过版本号完成依赖
- client 也通过proto文件版本号，这样如果只是server自己bug fix而proto没变，client不必做任何更改
- 不过这样多引入了一个项目，管理复杂度上升

方案2 - 为了避免多个项目的问题，可以使用maven的sub module方式，把server/client/proto文件分成3个sub module



注意：为何不把proto编译完的java代码交给client使用？因为
- client可以是非java项目
- client自己编译java或者别的，都不会大麻烦。都是自动的build
- proto文件非常清晰，方便问题定义/交接