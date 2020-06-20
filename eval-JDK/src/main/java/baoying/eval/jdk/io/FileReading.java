package baoying.eval.jdk.io;

import java.io.FileInputStream;
import java.io.InputStream;

public class FileReading {

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

            System.out.println("got " + readLen + "bytes");
        }

    }

    public  static void main(String[] args) throws Exception{
        new FileReading().byBytes();
    }
}
