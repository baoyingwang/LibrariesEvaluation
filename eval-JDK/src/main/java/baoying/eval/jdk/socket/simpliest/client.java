package baoying.eval.jdk.socket.simpliest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

public class client {
	
	public static void main(String args[]) throws Exception {
		
		System.out.println(new java.util.Date() + " Trying to connect");
		InetAddress ip = InetAddress.getByName("localhost");
		Socket sock = new Socket(ip, 1025);
		
		PrintStream ps = new PrintStream(sock.getOutputStream());
		ps.println(new java.util.Date() + " Hi from client");
		
		BufferedReader is = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		Thread.sleep(1000 * 3);
		System.out.println(is.readLine());

		sock.close();
		System.out.println(new java.util.Date() + "sock.close();");

	} // main
} // Class Client
