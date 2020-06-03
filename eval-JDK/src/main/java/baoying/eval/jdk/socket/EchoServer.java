package baoying.eval.jdk.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Echo each line. It requires '\n' as separator.
 *
 */
public class EchoServer {

	ServerSocket serverSocket = null;
	private int port = -1;

	public EchoServer(int listenPort) {
		this.port = listenPort;
	}

	public void execute() throws IOException {

		// int backlog = 100;
		// InetAddress address = InetAddress.getByName("10.35.51.32");
		// serverSocket = new ServerSocket(port, backlog, address);
		serverSocket = new ServerSocket(port);
		serverSocket.setReuseAddress(true); // Enabling SO_REUSEADDR prior to
											// binding the socket using
											// bind(SocketAddress) allows the
											// socket to be bound even though a
											// previous connection is in a
											// timeout state.
		System.out.println("server socket created, listening port:" + port);

		while (true) {

			final Socket socket = serverSocket.accept();
			System.out.println("client socket is accepted - client ip:"
					+ socket.getInetAddress().getHostName()+" client port:"+socket.getPort());

			String threadName = "Socket-" + socket.getPort();
			System.out.println("hatch new thread for this socket: "
					+ threadName);
			new Thread(new Runnable() {

				public void run() {
					try {

						InputStream in = socket.getInputStream();
						OutputStream out = socket.getOutputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(in));
						while (true) {
							String line = reader.readLine();
							if (line == null) {
								System.out.println("read stop because reach the end of stream");
								break;
							}
							//System.out.println("got data: \"" + line + "\"");

							out.write(line.getBytes());
							out.write('\n');
							out.flush();

							//System.out.println("echo data:\"" + line + "\"");
						}

					} catch (Exception e) {
						System.out.println("error" + e.toString());
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
				System.out.println("error" + e.toString());
			}
		}
	}

	public static void main(String[] args) throws IOException {

		// int port = 44444;
		int port = 51235;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}

		final EchoServer simpleSocketServer = new EchoServer(
				port);

		simpleSocketServer.execute();
	}

}
