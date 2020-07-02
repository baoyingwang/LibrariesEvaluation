
- System.getenv("keyName") - 这个是系统的环境变量，譬如PATH之类的。 
  - 在intellij中，debug时候配置它为environment variable 
- System.getProperty("keyName")
  - 这个是针对于java的系统环境，通过 -dkeyName=keyValue来获取
  - 譬如 -Djava.io.tmpdir=d:/tmp/t1， 这里java.io.tmpdir是一个java典型的jvm默认使用的jvm环境变量，如果不提供则默认值为 /tmp on linux, windows不一定，可能为当前用户