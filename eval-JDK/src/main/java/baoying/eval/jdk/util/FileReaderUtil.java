package baoying.eval.jdk.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

/**
 * It is still valuable, because of classpath utility method.
 * Use apache common io - FileUtils.java, and  IOUtils.java for others.
 *
 * @Deprecated 新jdk中的Files就够了
 * https://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file
 * 
 *
 */
public class FileReaderUtil {

	public static String readFromClasspath(final String file)
			throws FileNotFoundException {
		InputStream inputStream = FileReaderUtil.class
				.getResourceAsStream(file);
		String result = read(inputStream);
		close(inputStream);

		return result;
	}

	public static String read(final String fileWithPath)
			throws FileNotFoundException {

		return read(new FileInputStream(fileWithPath));
	}
	
	public static String read(final File file)
			throws FileNotFoundException {

		return read(new FileInputStream(file));
	}	

	public static String read(final InputStream inStream) {
		
		if(inStream == null){
			throw new IllegalArgumentException("inSteam is null unexpectedly");
		}
		
		final StringBuilder sb = new StringBuilder();
		BufferedReader bis = null;

		try {
			bis = new BufferedReader(new InputStreamReader(inStream));

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

		String result = sb.toString();
		if (result.lastIndexOf('\n') == result.length() - 1) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	public static String readInOneLine(final String fileWithPath) {
		File file = new File(fileWithPath);
		return readInOneLine(file);
	}

	public static String readInOneLine(File file) {

		if (file == null) {
			throw new IllegalArgumentException("null file");
		}

		final StringBuilder sb = new StringBuilder();
		BufferedReader bis = null;

		try {
			bis = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));

			for (String line = bis.readLine(); line != null; line = bis
					.readLine()) {
				sb.append(line.trim());
			}
		} catch (IOException e) {
			System.out.println("Error loading content from file:"
					+ file.getAbsolutePath());
			e.printStackTrace();
		} finally {
			close(bis);
		}

		return sb.toString();
	}

	// TODO exception should be thrown on open/operation file failure
	public static long getFileLineNum(final String fileWithPath) {

		LineNumberReader lnr = null;
		int linenumber = 0;
		try {
			lnr = new LineNumberReader(new InputStreamReader(
					new FileInputStream(fileWithPath)));
			while (lnr.readLine() != null) {
				linenumber++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Deal XML file '" + fileWithPath
					+ "' not found.");
		} catch (IOException e) {
			System.out
					.println("Error loading deal XML '" + fileWithPath + "'.");
			e.printStackTrace();
		} finally {
			close(lnr);
		}

		return linenumber;
	}

	public static void close(Closeable ioObj) {
		try {
			if (ioObj != null) {
				ioObj.close();
			}
		} catch (IOException e) {
		}
	}

	public static Properties readPropertiesFromCP(String file)
			throws URISyntaxException, FileNotFoundException, IOException {

		//groovy
		// def props = new Properties()
		// new
		// File(this.getClass().getResource(myAppConf).toURI()).withInputStream
		// { stream ->
		// props.load(stream)
		// }

		URL url = FileReaderUtil.class.getResource(file);

		Properties result = new Properties();
		File f = new File(url.toURI());
		FileInputStream in = new FileInputStream(f);
		result.load(in);
		in.close();

		return result;
	}
	
	public static Properties readProperties(String file)
			throws URISyntaxException, FileNotFoundException, IOException {

		Properties result = new Properties();
		File f = new File(file);
		FileInputStream in = new FileInputStream(f);
		result.load(in);
		in.close();

		return result;
	}	

	public static void main(String[] args) {

		String filename = "D:/x1.txt";
		String x = FileReaderUtil.readInOneLine(filename);
		System.out.println(x);
	}

}
