package baoying.eval.jdk.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Echo each line. It requires '\n' as separator.
 *
 */
public class DumbServer {

	ServerSocket _serverSocket = null;
	private int port = -1;

	public DumbServer(int listenPort) {
		this.port = listenPort;
	}

	public void start() throws Exception {

		_serverSocket = new ServerSocket(port);
		//https://msdn.microsoft.com/en-us/library/windows/desktop/ms740621(v=vs.85).aspx
		//http://www.unixguide.net/network/socketfaq/4.5.shtml
		_serverSocket.setReuseAddress(true);
		System.out.println("server socket created, listening port:" + port);

		while (true) {

			final Socket socket = _serverSocket.accept();
			System.out.println("client socket is accepted - client ip:" + socket.getInetAddress().getHostName()
			        + " client port:" + socket.getPort());

			String threadName = "Socket-" + socket.getPort();
			System.out.println("hatch new thread for this socket: " + threadName);
			new Thread(() -> {
					try {

						InputStream in = socket.getInputStream();
						// OutputStream out = socket.getOutputStream(); not
			            // required for DumbServer
						while (true) {
							in.read();
							// out.write("<R></RWP_1>".getBytes());
						}

					} catch (Exception e) {
						System.out.println("error" + e.toString());
					}

				}
			, threadName).start();
		}
	}

	public void shutdown() {
		if (_serverSocket == null) {
			return;
		}

		try {
			_serverSocket.close();
		} catch (IOException ignore) {
			System.out.println("error" + ignore.toString());
		}
	}

	public static void main(String[] args) throws Exception {

		// int port = 44444;
		int port = 51235;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}

		final DumbServer simpleSocketServer = new DumbServer(port);

		simpleSocketServer.start();
	}

}
