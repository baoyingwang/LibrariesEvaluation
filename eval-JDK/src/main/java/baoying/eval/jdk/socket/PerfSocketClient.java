package baoying.eval.jdk.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerfSocketClient {

	private static Logger LOGGER = LoggerFactory.getLogger(PerfSocketClient.class);

	private volatile boolean connected = false;

	private final String host;
	private final int port;

	private Socket socket = null;
	private OutputStream out = null;
	private InputStream in = null;

	private SimpleSocketReader reader = null;
	private String readerEndTag = "\n";

	public PerfSocketClient(String targetHost, int targetPort) {
		this.host = targetHost;
		this.port = targetPort;
	}

	public PerfSocketClient(String targetHost, int targetPort, String msgEndTag) {
		this.host = targetHost;
		this.port = targetPort;
		this.readerEndTag = msgEndTag;
	}

	public void connect() throws UnknownHostException, IOException {

		// deliberately without check stopped status, always force re-connect
		disconnect();

		// here, we could specify local ip address via
		// public Socket(InetAddress address, int port, InetAddress localAddr,
		// int localPort)throws IOException
		socket = new Socket(host, port);
		LOGGER.info("socket is setup, host:" + host + " port:" + port);

		in = socket.getInputStream();
		out = socket.getOutputStream();

		reader = new SimpleSocketReader(in, readerEndTag);
		reader.setDaemon(true);
		reader.start();
		LOGGER.info("socket reader is setup, ENDTAG:" + reader.endTag());

		connected = true;
	}

	public synchronized void send(String data) throws IOException {

		try {

			char[] chars = data.toCharArray();
			for (char i : chars) {
				out.write(i);
			}
			LOGGER.info("sent data:" + data);

			out.flush();

		} catch (IOException e) {
			connected = false;
			disconnect();
			throw e;
		}
	}

	public void sendLine(String data) throws IOException {
		send(data + "\n");
	}

	public String take() throws InterruptedException {
		return reader.take();
	}

	public void disconnect() {
		if (this.reader != null) {
			reader.stopIt();
			reader.interrupt();
		}

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

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {

		String host = "localhost";
		int port = 51235;

		if (args.length >= 2) {
			host = args[0];
			port = Integer.parseInt(args[1]);
		}

		final PerfSocketClient socketClient = new PerfSocketClient(host, port);

		socketClient.connect();
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						String received = socketClient.take();
						long send = Long.parseLong(received.trim());
						long recv = System.nanoTime();

						System.out.println("e2e:" + (recv - send));

					}
				} catch (InterruptedException e) {
					LOGGER.error("", e);
				}
			}
		}).start();

		for(int i=0; i<5000; i++){
			socketClient.sendLine(String.valueOf(System.nanoTime()));
			TimeUnit.MILLISECONDS.sleep(2);
		}

	}

}
