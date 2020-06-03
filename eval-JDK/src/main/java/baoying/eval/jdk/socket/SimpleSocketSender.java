package baoying.eval.jdk.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSocketSender {

	private static Logger LOGGER = LoggerFactory.getLogger(SimpleSocketSender.class);

	private volatile boolean connected = false;

	private final String host;
	private final int port;

	private Socket socket = null;
	private OutputStream out = null;


	public SimpleSocketSender(String targetHost, int targetPort) {
		this.host = targetHost;
		this.port = targetPort;
	}

	public void connect() throws UnknownHostException, IOException {

		//deliberately without check stopped status, always force re-connect 
		disconnect();

		socket = new Socket(host, port);
		LOGGER.info("socket is setup, host:"
		+ host + " port:" + port);

		out = socket.getOutputStream();

		connected = true;
	}

	public void send(String data) throws IOException {

		try {
			
			char[] chars = data.toCharArray();
			for (char i : chars) {
				out.write(i);
			}
			LOGGER.debug("sent data:" + data);

			out.flush();
			
		} catch (IOException e) {
			connected = false;
			throw e;
		}
	}

	public void sendLine(String data) throws IOException {
		send(data + "\n");
	}


	public void disconnect() {

		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				LOGGER.info("error when close", e);
			}
		}

		connected = false;
		LOGGER.info("disconnected host:" + host + " port:" + port);
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {

		String host = "localhost";
		int port = 5001;

		if (args.length >= 2) {
			host = args[0];
			port = Integer.parseInt(args[1]);
		}

		final SimpleSocketSender socketSender = new SimpleSocketSender(host,
				port);

		try {

			socketSender.connect();

			BufferedReader m_in = new BufferedReader(new InputStreamReader(
					System.in));
			while (true) {
				String input = null;
				while (input == null) {
					input = m_in.readLine();
				}

				if (input.startsWith("s")) {
					// send
					socketSender.send(input);
				} else if (input.startsWith("e")) {
					break;
				}
			}

		} catch (final IOException e) {
			System.out.println("" + e.toString());
		}
		socketSender.disconnect();

	}

}
