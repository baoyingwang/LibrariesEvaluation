package baoying.eval.jdk.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileWriting {

    public void byBytes() throws Exception{
        String fileName = "D:/setup.exe";
        InputStream in = new FileInputStream(fileName);


        byte[] buffer = new byte[256];

        long totalBytes = 0;
        while(true){
            int readLen = in.read(buffer);
            if(readLen== -1){
                System.out.println("reach end, exit loop. total got:" + totalBytes +" bytes");
                break;
            }

            totalBytes += readLen;
            //out.write(buffer, 0, readLen); //别用out.write(buffer)因为可能最后一段buffer不满

            System.out.println("got " + readLen + "bytes");
        }

    }

    public  static void main(String[] args) throws Exception{
        new FileWriting().byBytes();
    }
}
