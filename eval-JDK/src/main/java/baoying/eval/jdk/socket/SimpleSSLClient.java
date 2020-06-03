package baoying.eval.jdk.socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


//-Djavax.net.ssl.trustStore=D:/baoying.wang/UAT.jks
//-Djavax.net.ssl.trustStorePassword=Monday20140623
//
//-Djavax.net.debug=all
//
//for client authentication, e.g.  FIX port 16005
//-Djavax.net.ssl.keyStore=
//-Djavax.net.ssl.keyStorePassword=
//

//from 
//http://stilius.net/java/java_ssl.php
//http://e-blog-java.blogspot.co.uk/2011/02/find-out-what-cipher-suites-are.html
//
public class SimpleSSLClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		//String uatInternetIP="81.201.162.171";

		System.out.println("app log - date:" + new java.util.Date());
		
		
		String serverIP=args[0];
		int serverPort =Integer.parseInt(args[1]);
		System.out.println("app log - target ip  :" + serverIP);
		System.out.println("app log - target port:" + serverPort);

        String[] protocols = null; //new String[]{"TLSv1"};
        String[] suites = null; //new String[]{"SSL_RSA_WITH_RC4_128_MD5"};

		if(args.length >=4){
			
			protocols = args[2].split(",");
			System.out.println("app log - protocols:" + args[2]);
			
			suites = args[3].split(",");
			System.out.println("app log - cipherSuites:" + args[3]);
		}
		
        try {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(serverIP, serverPort);
            
            System.out.println("app log - socket:" + sslsocket.toString());
            System.out.println("app log - socket.getLocalPort:" + sslsocket.getLocalPort());
            System.out.println("app log - socket.getPort:" + sslsocket.getPort());
            
            if(protocols != null)	
            	sslsocket.setEnabledProtocols(protocols);
            
            if(suites != null)
            	sslsocket.setEnabledCipherSuites(suites);


			//The initial handshake on this connection can be initiated in one of three ways:
			//    calling startHandshake which explicitly begins handshakes, or
			//    any attempt to read or write application data on this socket causes an implicit handshake, or
			//    a call to getSession tries to set up a session if there is no currently valid session, and an implicit handshake is done.
            //refer: http://download.java.net/jdk7/archive/b123/docs/api/javax/net/ssl/SSLSocket.html
            sslsocket.startHandshake();  
            
            Thread.sleep(3*1000);
            System.out.println("done handshake. looks good");
                
        } catch (Exception exception) {
        	
            exception.printStackTrace();            
            System.out.println("ERROR. Exception happened : "+exception.toString());
            
        }

	}

}
