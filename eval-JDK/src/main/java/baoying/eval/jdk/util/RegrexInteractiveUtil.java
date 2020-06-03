package baoying.eval.jdk.util;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegrexInteractiveUtil {

	public static void main(String[] args) {

		while (true) {
			doRegex();
		}

	}

	public static void doRegex() {
		String regex = "";
		String string = "";

		Scanner sc = new Scanner(System.in);

		System.out.print("regex:");
		if (sc.hasNextLine())
			regex = sc.nextLine();

		System.out.print("string:");
		if (sc.hasNextLine())
			string = sc.nextLine();

		loopByFind(regex, string);

	}

	public static void loopByFind(String regex, String string) {

		Pattern pStart = Pattern.compile(regex);
		Matcher m = pStart.matcher(string);

		while (m.find()) {
			String matched = m.group();
			int start = m.start();
			int end = m.end();
			System.out.println("matched:" + matched + ", start index:" + start
					+ ", end index:" + end);
			
			int groupCount = m.groupCount();
			for (int i = 1; i <= groupCount; i++) {
				System.out.println("group #" + i + " matched:" + m.group(i));
			}			
		}
	}
}
