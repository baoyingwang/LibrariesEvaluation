package baoying.eval.jdk.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
U0127650@U0127650-TPL-A /d/baoying.wang/360CloudDisk/00_myprojects/BaoyingUtil/b
in (master)
$ cat 1.txt
abcd123
abcd123xxx123
abcd123xxx123444
abcd666xxx123444

U0127650@U0127650-TPL-A /d/baoying.wang/360CloudDisk/00_myprojects/BaoyingUtil/b
in (master)
$ cat 1.txt
abcd123
abcd123xxx123
abcd123xxx123444
abcd666xxx123444

U0127650@U0127650-TPL-A /d/baoying.wang/360CloudDisk/00_myprojects/BaoyingUtil/b
in (master)
$ java baoying.util.RegrexCLI "\d+" 1.txt
123
123
123
666

U0127650@U0127650-TPL-A /d/baoying.wang/360CloudDisk/00_myprojects/BaoyingUtil/b
in (master)
$ echo "abcd898s" |  java baoying.util.RegrexCLI "\d+"
898
 */
public class RegrexCLI {

	//RegrexCLI pattern file
	//RegrexCLI pattern , it will read from pipe
	public static void main(String[] args) throws IOException {

		if(args.length <1){
			System.err.println("usage ");
			return;
		}
		
		String pattern = args[0];
		InputStream in = null;
		OutputStream out = System.out;
		if(args.length == 2){
			in = new FileInputStream(args[1]);
		}else{
			in = System.in;
		}
		
		BufferedReader bis = new BufferedReader(new InputStreamReader(in));
        for (String line = bis.readLine(); line != null; line = bis.readLine())
        {
        	loopByFind(pattern,line, out);
        }
		
		if(args.length == 2){
			in.close();
		}

	}

	public static void loopByFind(String regex, String string, OutputStream out) throws IOException {

		Pattern pStart = Pattern.compile(regex);
		Matcher m = pStart.matcher(string);

		if (m.find()) {
			String matched = m.group();
			out.write(matched.getBytes());
			out.write('\n');
		}
	}
}
