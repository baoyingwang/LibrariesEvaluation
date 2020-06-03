package baoying.eval.jdk.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Sometimes, the exported CSV file is broken because of additional carriage
 * return in some fields. This util will try to remove the carriage return, and
 * merge them in expected single lines e.g. 
 * "a","b", "c d 
 * hell","x" Above will
 * be merged to "a","b", "c d hell","x"
 * 
 * @author shirley
 *
 */
public class BrokenCSVFixer {
	
	/**
	 * TODO : class path, or os file path?
	 * @param csvFile2BeFixed
	 * @param newFile
	 */
	public void fix(String csvFile2BeFixed, String newFile){
		
		File csvFile2BeFixedFile = new File(csvFile2BeFixed);
		
		final StringBuilder sb = new StringBuilder();
		BufferedReader bis = null;

		try {
			bis = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile2BeFixedFile)));

			for (String line = bis.readLine(); line != null; line = bis
					.readLine()) {
				sb.append(line);
				sb.append("\n");

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(bis);
		}
	}
	
	public static void close(Closeable ioObj) {
		try {
			if (ioObj != null) {
				ioObj.close();
			}
		} catch (IOException e) {
		}
	}

}
