基于例子：https://www.baeldung.com/grpc-introduction
其代码于：https://github.com/eugenp/tutorials/tree/master/grpc
我的更改：
1. 更改了package名称
  -proto文件中的对应package要改，因为生成源代码时候需要这个目录
2. pom.xml中增加了解决@javax.annotation.Generated问题的dependency - tomcat
  - 参考https://stackoverflow.com/questions/59079616/generated-protoc-file-creates-a-target-source-with-error

真正的项目中
1. server: proto文件于server放在一起源代码管理
2. proto文件作为release的一个单独部分拿出来，给client使用
3. client拿着带版本的proto文件，自己编译成java代码或者别的代码，因为理论上client可以是任何语言

注意：为何不把proto编译完的java代码交给client使用？因为
- client可以是非java项目
- client自己编译java或者别的，都不会大麻烦。都是自动的build
- proto文件非常清晰，方便问题定义/交接