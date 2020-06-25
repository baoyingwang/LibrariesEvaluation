验证高并发下面oracle是否会deadlock
 
thread1:
    begin
        insert， 下面select将多保存一条数据
    end
    
thread2/3/4/5：
    begin
        update(select xxx from where a=b order by x) set a=a
        update one of the records
    commit 