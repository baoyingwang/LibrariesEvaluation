

# 指定配置文件位置
## log4j2
```
-Dlog4j.configurationFile=qfjtutorial/start/FirstQFJServer.log4j2.xml
```
https://stackoverflow.com/questions/16716556/how-to-specify-log4j-2-x-config-location
note: the path could be 
* relative path, or 
* absolute path, or 
* classpath (there is no slash in the heading). I did not test whether log4j support class path. I believe so.


## log4j 1.X
 指定logj配置文嘉你位置 / specify your log4j configuration file
-Dlog4j.configuration=./conf/log4j.xml 

