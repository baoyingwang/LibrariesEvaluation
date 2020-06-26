# Overview
 这里重现一个死锁的场景

# 基本情况
  - 多个线程使用update(select x from t order by y) set x=x的方式进行资源锁定
    - e.g. UPDATE (SELECT r.serialnum FROM etforder r WHERE r.serialnum =1213555 AND r.ordertime =20200623144538 AND r.exchid = '0' AND r.knockqty = 0 ORDER BY r.stkid) SET serialnum = serialnum
  - 然后发现了deadlock
    - oracle主动发现这个问题，然后到oracle的trace file目录可以找到细节

# update lock的基本原理
  - 使用update而没有使用select * from t order by y for update
    - 因为据说，这样的for udpate排序无效
    - 排序非常重要，因为我们要保证锁定顺序。
    - 多个线程之间锁定时候要保证锁定顺序才能避免死锁
  - 但是这个锁定其实也有一定的问题（风险）要认证考虑业务实现的时候是否会有这样的问题
    - 就是接下来要进行额外的select操作去拿到对应锁定的数据
    - 如果update lock之后，相应条件的记录增加了（有人执行了commit）
      - 则可能后序取出没有锁定的数据进行操作   

# 细节情况的深究 
- 多个线程调用udpate lock
- 期间发现一条insert到锁定表（刚好符合update lock条件）的insert
  - 高度怀疑其导致问题
  - 这条insert插入的数据，在顺序上（update lock的order by条件）是位于顶端的
  - 这意味着
    - 后序执行update lock的线程将被首先试图锁住它，必然成功因为它是新数据
    - 前面没有执行完的