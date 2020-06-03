package baoying.eval.jdk.socket.nagle;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


import baoying.eval.jdk.socket.SocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * NagleEnabled 
 * 
 * Nagle would be enabled/disable by
 * java.net.Socket.setTcpNoDelay(true/false) true: disable Nagle false: enable
 * 
 * Nagle - by default
 * 
 * @author baoying.wang
 * 
 */
public class NagleClient {

	private static Logger LOGGER = LoggerFactory.getLogger(NagleClient.class);

	/**
	 * @param args
	 * @throws IOException
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		//alan
		//String host="10.35.36.113";
		
		//huhuzhu gid server
		//String host="10.15.26.84";
		String host = "localhost";
		int port = 44444;

		if (args.length >= 1) {
			host = args[0];
		}

		if (args.length >= 2) {
			port = Integer.parseInt(args[1]);
		}

		boolean isNagleEnabled = false;
		Socket socket = new Socket(host, port);
		socket.setTcpNoDelay(!isNagleEnabled);
		
		LOGGER.info("socket is setup, host:" + host + " port:" + port);
		SocketUtil.printSocketInfo(socket);

		OutputStream out = socket.getOutputStream();

		int maxPackageNum = 3;
		for (int i = 0; i < maxPackageNum; i++) {
			out.write(i);
			LOGGER.info("sent data:"+i);
			
			//after test, no affection found on flush here or not 
			out.flush();
			LOGGER.info("explicitly flushed");
		}

		Thread.sleep(5 * 1000);
		socket.close();

	}
	
	public static void usage(String[] args) {
		
		System.out.println("java "+NagleClient.class.getName() + args[0] + " host [port]");

	}

}
