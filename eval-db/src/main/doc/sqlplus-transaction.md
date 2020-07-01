
# oracle 没有repeatable read。如果在java connection中设置RR，则直接报错说只能是这read committed和一个神？
就能设置这俩
set transaction isolation level read committed;
set transaction isolation level serializable;

还能设置这样的
set transaction read only 
  - 则当前语句中不能执行任何dml - 即不能执行delete/update
  - 读取的时候类似当于RR，只能看到transaction开始时候的提交过的记录
    - 这雨Mysql不太一样，mysql是tx中的第一个sql语句打快照，这个好像是tx开始就打快照？？？（待实验确认）ss
set transaction read write

这里是set transaction的全部语法
https://docs.oracle.com/cd/B28359_01/server.111/b28286/statements_10005.htm#SQLRF01705

# tx日常使用
- 默认tx隔离界别为read committed
- 不用执行begin，直接commit就行
