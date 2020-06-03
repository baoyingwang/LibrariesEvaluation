package baoying.eval.jdk.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class SimpleEchoSocketServer {


	ServerSocket serverSocket = null;
	private int port = -1;

	public static void main(String[] args) throws IOException {

		int port = 44444;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}

		
		final SimpleEchoSocketServer simpleSocketServer = new SimpleEchoSocketServer(port);

		simpleSocketServer.listen();
	}

	public SimpleEchoSocketServer(int listenPort) {
		this.port = listenPort;
	}

	public void listen() throws IOException {

		serverSocket = new ServerSocket(port);

		//TODO TO BE REMOVED, ONLY TEST TIMEOUT OF TOF
		serverSocket.setReuseAddress(true);

		System.out.println("server socket created, listening port:" + port);

		while (true) {

			final Socket socket = serverSocket.accept();
	        //TODO TO BE REMOVED, ONLY TEST TIMEOUT OF TOF
			socket.setReuseAddress(true);

			System.out.println("client socket is accepted, client port:"
					+ socket.getPort());
			SocketUtil.printSocketInfo(socket);

			String threadName = "Socket-" + socket.getPort();
			System.out.println("hatch new thread for this socket: " + threadName);
			new Thread(new Runnable() {

				public void run() {
					try {

						InputStream in = socket.getInputStream();
						OutputStream out = socket.getOutputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						while (true) {
							String line = reader.readLine();
			                if(line == null){
			                    System.out.println("read stop because reach the end of stream");
			                    break;
			                }
			                System.out.println("got data: \"" + line+"\"");
			                
			                out.write(line.getBytes());
			                out.write('\n');
			                
			                System.out.println("echo data:\"" + line+"\"");
						}
						

					} catch (Exception e) {
						System.out.println("error"+ e.toString());
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
				System.out.println("error"+ e.toString());
			}
		}
	}




}
