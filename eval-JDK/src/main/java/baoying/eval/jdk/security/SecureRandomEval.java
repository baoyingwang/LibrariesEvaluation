package baoying.eval.jdk.security;

import java.security.SecureRandom;
import java.util.Date;

/**
 * 本例子用来验证调用SecureRandom来消耗系统的entropy
 * 调用后检查： /proc/sys/kernel/random/entropy_avail
 *
 * 非常偶尔的发现entropy_avail change is consistent with this app execution.
 * But most of the 99+%, this execution does not work.
 *
 * This eval is introduced because of the known oracle issue on connection(Logon)
 * https://blog.csdn.net/guodaoying/article/details/52421758
 * https://stackoverflow.com/questions/2327220/oracle-jdbc-intermittent-connection-issue/2328353#2328353
 * https://support.tibco.com/s/article/Tibco-KnowledgeArticle-Article-37169
 *
 */
public class SecureRandomEval {

    public static void main(String[] args) throws Exception{

        int count = 5;
        if(args.length > 0){
            count = Integer.parseInt(args[0]);
        }

        System.out.println(new Date()+"loop count:" + count + " begin");
        for(int i=0; i<count; i++) {

            //https://blog.csdn.net/chinoukin/article/details/102566755
            //SecureRandom sr = new SecureRandom();
            SecureRandom sr = SecureRandom.getInstanceStrong();
            byte[] data = new byte[]{1, 2 ,3, new Integer(i).byteValue()};

            sr.nextBytes(data);
            sr.nextBytes(("1234567890"+System.currentTimeMillis()).getBytes());
            sr.generateSeed(5);

        }
        System.out.println(new Date()+"loop count:" + count + " end");

    }
}
