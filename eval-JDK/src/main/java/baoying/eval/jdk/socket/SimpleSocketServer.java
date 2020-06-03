package baoying.eval.jdk.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleSocketServer {

	private static Logger LOGGER = LoggerFactory.getLogger(SimpleSocketServer.class);

	ServerSocket serverSocket = null;
	private int port = -1;
	SocketDataHandler handler = null;

	public static void main(String[] args) throws IOException {

		int port = 44444;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}

		DefaultSocketDataHandler socketDataHandler = new DefaultSocketDataHandler();
		final SimpleSocketServer simpleSocketServer = new SimpleSocketServer(port, socketDataHandler);

		simpleSocketServer.listen();
	}

	public SimpleSocketServer(int listenPort, SocketDataHandler handler) {
		this.port = listenPort;
		this.handler = handler;
	}

	public void listen() throws IOException {

		serverSocket = new ServerSocket(port);

		//TODO TO BE REMOVED, ONLY TEST TIMEOUT OF TOF
		serverSocket.setReuseAddress(true);

		LOGGER.info("server socket created, listening port:" + port);

		while (true) {

			final Socket socket = serverSocket.accept();
	        //TODO TO BE REMOVED, ONLY TEST TIMEOUT OF TOF
			socket.setReuseAddress(true);

			LOGGER.info("client socket is accepted, client port:"
					+ socket.getPort());
			SocketUtil.printSocketInfo(socket);

			String threadName = "Socket-" + socket.getPort();
			LOGGER.info("hatch new thread for this socket: " + threadName);
			new Thread(new Runnable() {

				public void run() {
					try {

						InputStream in = socket.getInputStream();
						handler.handle(in);

					} catch (Exception e) {
						LOGGER.error("error", e);
					}

				}
			}, threadName).start();
		}
	}

	public void shutdown() {

		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				LOGGER.error("error", e);
			}
		}
	}

	static class DefaultSocketDataHandler implements SocketDataHandler {

		@Override
		public void handle(InputStream in) throws IOException {

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while (true) {
				String line = reader.readLine();
                if(line == null){
                    LOGGER.info("read stop because reach the end of stream");
                    break;
                }
                LOGGER.info("got data:" + line);
				LOGGER.info("got data:" + line);
			}
		}
	}

	interface SocketDataHandler {
		public void handle(InputStream in) throws IOException;
	}
}
