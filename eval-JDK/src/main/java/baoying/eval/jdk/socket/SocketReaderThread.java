package baoying.eval.jdk.socket;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketReaderThread extends Thread {
	private int seq = 0;
	private final String _endTag;

	public interface MessageProcessor {
		public void process(long seq, String message);
	}

	private static Logger LOGGER = LoggerFactory.getLogger(SocketReaderThread.class);

	private final InputStream _input;
	private final MessageProcessor _processor;

	private final SocketInputMessageReader _messageReader;

	public SocketReaderThread(InputStream in, String endOfMsg, MessageProcessor processor) {

		super("Socket Reader Thread");
		this._input = in;

		_messageReader = new SocketInputMessageReader(endOfMsg);
		_endTag = endOfMsg;
		_processor = processor;
	}

	public void run() {
		while (!this.isInterrupted()) {
			try {
				String msg = _messageReader.readNextMsg(_input);
				seq++;
				process(seq, msg);
			} catch (IOException e) {
				LOGGER.error("", e);
				break;

			}
		}
	}

	private void process(long seq, String msg) {
		if (_processor == null)
			LOGGER.warn("received msg {}, but related processor is NOT setup", msg);
		else
			_processor.process(seq, msg);

	}

	public String endTag() {
		return _endTag;
	}

}
