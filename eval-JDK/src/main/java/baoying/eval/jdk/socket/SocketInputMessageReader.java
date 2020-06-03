package baoying.eval.jdk.socket;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketInputMessageReader 
{
	private static Logger LOGGER = LoggerFactory.getLogger(SocketInputMessageReader.class);

	
	private String _endTag;

	//if end tag is not there after this size, exception would be thrown
	private static int DEFAULT_MAX_INPUT_BYTES = 1024 * 1024 * 32;
	
	/**
	 * normally, a protocol will have same endTag for any message.
	 * @param endTag
	 */
	public SocketInputMessageReader(String endTag){
		this._endTag = endTag;
	}
    
    public String readNextMsg(InputStream input) throws IOException
    {

        StringBuffer sb = new StringBuffer();
        sb.setLength(0);
        boolean eol = false;
        while (!eol)
        {
            int i = input.read();
            if (i == -1)
            {
                throw new RuntimeException("Unexpected end of stream. Read: " + sb.toString());
            }

            sb.append((char) i);

            eol = isEndOfPackage(sb);
        }
        String s = sb.toString();
        LOGGER.debug("Read: " + s);
        return s;
    }

    private boolean isEndOfPackage(StringBuffer sb)
    {
        if (sb.length() < _endTag.length())
        {
            return false;
        }
        String last = sb.substring(sb.length() - _endTag.length());
        return (_endTag.equals(last));

    }
}
