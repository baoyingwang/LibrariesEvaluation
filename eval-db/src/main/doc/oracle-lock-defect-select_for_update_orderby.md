
# 11.2 有这个bug
本文由描述，和如何重现
http://orasql.org/2013/02/16/workaround-for-deadlock-with-select-for-update-order-by-on-11-2-0-2-11-2-0-3/

我在11.2.0.1.0上面重现了这个问题（按照上面链接中的例子代码）

这个url中有解决这个bug的defect链接，但是即使继续登陆了oracle也不行看不到（需要什么专有账号）
https://www.eygle.com/Notes/1562142.1-BugList-11.2.0.4.html
13371104	DML lock order for "SELECT .. ORDER BY .. FOR UPDATE" has changed in 11.2 over earlier releases



1个讨论，lock和order by哪个先发生
ask Tom说先lock再order by（http://asktom.oracle.com/pls/asktom/f?p=100:11:0::::P11_QUESTION_ID:839412906735）
但是有人不服，说Tom也会犯错误滴，建议自己试一试
https://community.oracle.com/thread/871785?start=0&tstart=0


# EXT
select for update时候的死锁和建议
https://stackoverflow.com/questions/14755222/oracle-select-order-by-for-update-deadlock
