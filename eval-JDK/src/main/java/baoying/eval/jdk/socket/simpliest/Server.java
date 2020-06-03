package baoying.eval.jdk.socket.simpliest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	// port number should be more than 1024

	public static final int PORT = 1025;

	public static void main(String args[]) throws IOException {

		ServerSocket serverSocket = new ServerSocket(PORT);
		System.out.println(new java.util.Date() + "Server Started  :" + serverSocket);

		
		Socket clientSock = serverSocket.accept(); //blocking wait next connection request
		System.out.println(new java.util.Date() + " Connection from :  " + clientSock.getInetAddress());

		BufferedReader ins = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
		PrintStream ios = new PrintStream(clientSock.getOutputStream());
		ios.println(new java.util.Date() + "Hello from server");
		
		ios.close();
		
		clientSock.close();
		serverSocket.close();

	}
}// Server class
