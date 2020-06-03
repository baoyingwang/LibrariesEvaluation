package baoying.eval.jdk.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileWriterUtil {

	public static void write(String file, String content) {
		boolean append = false;
		write(file, content, append);



	}

	public static void write(String file, String content,boolean append) {
			
			File f = new File(file);
			write(f, content, append);
	}
	
	public static void write(File file, String content,boolean append) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(file, append);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(content);

			//not required to flush because it will auto flush when close.
			//but I like to flush obviosly.
			out.flush();
			
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}	
}
