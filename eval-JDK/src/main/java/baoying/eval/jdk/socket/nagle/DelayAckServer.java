package baoying.eval.jdk.socket.nagle;

import baoying.eval.jdk.socket.SocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;



/**
 * 
 * This is a socket server. It would be used to verify the delayed ack. This
 * server would accept connection and print input data only. In another word, it
 * applies read-read-read pattern.
 * 
 */
public class DelayAckServer {

	private static Logger LOGGER = LoggerFactory.getLogger(DelayAckServer.class);

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		int port = 44444;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}

		ServerSocket serverSocket = null;

		serverSocket = new ServerSocket(port);
		LOGGER.info("server socket created, listening port:" + port);

		while (true) {

			final Socket socket = serverSocket.accept();
			LOGGER.info("client socket is accepted, client port:"
					+ socket.getPort());

			SocketUtil.printSocketInfo(socket);

			LOGGER.info("client socket is accepted, client port:"
					+ socket.getPort());
			
			String threadName = "DelayedAckServer-"+socket.getPort();
			new Thread(new Runnable() {

				public void run() {
					try {

						InputStreamReader in = new InputStreamReader(
								socket.getInputStream(), "UTF-8");

						while (true) {
							int x = in.read();

							if (x == -1) {
								LOGGER.info("end of stream. ");
								break;
							}

							LOGGER.info("got data:" + x);

						}
					} catch (Exception e) {
						LOGGER.error("error", e);
					}

				}
			},threadName).start();
		}

	}

}
