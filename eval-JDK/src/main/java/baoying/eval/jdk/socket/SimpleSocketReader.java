package baoying.eval.jdk.socket;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO use pattern , instead of hard coded end tag
 * 
 * note: cached messages will be cleared on I/O error, or disconnect.
 * 
 * PLAN: remove thread; provide a util to read with specific end tag.
 *
 */
public class SimpleSocketReader extends Thread {

	private static Logger LOGGER = LoggerFactory.getLogger(SimpleSocketReader.class);
	
	
	private volatile boolean stopped = false;
	
	private final String END_TAG;
	private InputStream in = null;
	
	
	private BlockingQueue<String> msgs = new LinkedBlockingQueue<String>();

	private static String DEFAULT_END_TAG = "\n";
	
	public SimpleSocketReader(InputStream in) {
		
		this(in, DEFAULT_END_TAG);
	}



	public SimpleSocketReader(InputStream in, String endTag) {

		super("Socket Reader Thread");

		END_TAG = endTag;
		this.in = in;
	}

	public void run() {
		
		while (!Thread.currentThread().isInterrupted() && !stopped) {
			
			try {
				
				String msg = readNextMsg();
				LOGGER.debug("recv:"+msg);
				msgs.add(msg);
				
			} catch (IOException e) {
				
				LOGGER.info("IOEception in run()", e);
				
				break;

			}

		}
		
		LOGGER.info("msgs.clear()");
		msgs.clear();
	}
	
	public String take() throws InterruptedException{
		return msgs.take();
	}
	
	public void stopIt(){
		stopped= true;
		msgs.clear();
	}
	public String endTag() {
		return this.END_TAG;
	}

	private String readNextMsg() throws IOException {

		StringBuffer sb = new StringBuffer();
		sb.setLength(0);
		
		boolean msgEnd = false;
		while (!msgEnd) {
			
			int i = in.read();
			if (i == -1) {
				throw new IOException("Unexpected end of stream. Read: "
						+ sb.toString());
			}

			sb.append((char) i);

			msgEnd = isEndOfMassge(sb);
		}
		
		String s = sb.toString();
		return s;
	}

	private boolean isEndOfMassge(StringBuffer sb) {
		
		if (sb.length() < END_TAG.length()) {
			return false;
		}
		
		String last = sb.substring(sb.length() - END_TAG.length());
		return (END_TAG.equals(last));

	}
	

}
