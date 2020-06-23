评估quarz的如下几个方面
- basic
  - 时间间隔任务
    - 间隔5秒执行
    - 如果执行时间超过5秒，加一个任务应该/如何执行，是否/如何设定其行为
    - 创建后立即执行一次
  - 定时任务
    - 每天固定时间执行
    - 从配置文件中设定时间时间设定
    - 
  - 公共方面
    - 如何监控任务执行状态
      - 查看某个任务状态
      - 查看所有任务状态
    - 如何更改任务执行
      - 暂停
      - 取消
      
# 基本概念
Trigger - 最常见的有simple trigger和cron trigger
Trigger 有name和group属性
- 重复的trigger注册会被拒绝
- group不提供的话，就进入默认的Scheduler.DEFAULT_GROUP
- name不提供的话，就生成一个唯一的name

参考：https://www.jianshu.com/p/dc4afa714440 五、Quartz中Trigger理解和使用

    