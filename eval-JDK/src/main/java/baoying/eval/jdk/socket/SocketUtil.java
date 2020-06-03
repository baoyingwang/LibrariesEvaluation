package baoying.eval.jdk.socket;

import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketUtil {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SocketUtil.class);
	
	public static void printSocketInfo(Socket socket) {
		try {
			LOGGER.info("socket:" + socket.toString());
			LOGGER.info("socket.getLocalPort:" + socket.getLocalPort());
			LOGGER.info("socket.getPort:" + socket.getPort());

			LOGGER.info("socket.getReceiveBufferSize:"
					+ socket.getReceiveBufferSize());

			LOGGER.info("socket.getSendBufferSize:"
					+ socket.getSendBufferSize());
			LOGGER.info("socket.getSoLinger:" + socket.getSoLinger());
			LOGGER.info("socket.getSoTimeout:" + socket.getSoTimeout());
			LOGGER.info("socket.getTrafficClass:" + socket.getTrafficClass());
			LOGGER.info("socket.getKeepAlive:" + socket.getKeepAlive());
			LOGGER.info("socket.getOOBInline:" + socket.getOOBInline());
			LOGGER.info("socket.getReuseAddress:" + socket.getReuseAddress());
			LOGGER.info("socket.getTcpNoDelay:" + socket.getTcpNoDelay());
		} catch (SocketException e) {
			LOGGER.error("failed to print socket info", e);
		}
	}
}
