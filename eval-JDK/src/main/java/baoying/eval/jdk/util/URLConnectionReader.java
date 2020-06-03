package baoying.eval.jdk.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * http://docs.oracle.com/javase/tutorial/networking/urls/readingWriting.html
 * 
 * 
 *
 */
public class URLConnectionReader {
    public static String read(URL url) throws Exception {

    	StringBuilder sb = new StringBuilder();
        URLConnection yc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) 
        	sb.append(inputLine);
        in.close();
        
        return sb.toString();
    }
}
