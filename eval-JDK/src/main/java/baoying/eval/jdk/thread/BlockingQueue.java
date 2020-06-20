package baoying.eval.jdk.thread;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueue {

    /**
     * drainTo直接返回，不block。没有数据则返回空collection
     */
    public void drainTo(){
        ArrayBlockingQueue<String> q = new ArrayBlockingQueue<String>(100);
        q.offer("abc");

        ArrayList<String> output1 = new ArrayList<>();
        q.drainTo(output1);
        System.out.println("1st drained size:"+ output1.size());

        //本测试为了验证drain操作是非blocking的，就是说空queue的话，其直接返回
        ArrayList<String> output2 = new ArrayList<>();
        q.drainTo(output2);
        System.out.println("2nd drained size:"+ output2.size());

    }

    public static void main(String[] args){

        BlockingQueue b = new BlockingQueue();
        b.drainTo();
    }
}
