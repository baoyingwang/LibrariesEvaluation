package baoying.eval.jdk.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ComplexSocketClient
{

    private static Logger LOGGER = LoggerFactory.getLogger(ComplexSocketClient.class);

    private final String  host;
    private final int     port;

    private Socket        socket = null;
    private OutputStream  out    = null;
    private InputStream   in     = null;

    private SocketReaderThread  reader = null;

    public ComplexSocketClient(String targetHost, int targetPort)
    {
        this.host = targetHost;
        this.port = targetPort;
    }

    public void connect() throws UnknownHostException, IOException
    {
        socket = new Socket(host, port);
        out = socket.getOutputStream();
        LOGGER.info("socket is setup, host:" + host + " port:" + port);
        SocketUtil.printSocketInfo(socket);

        String endTagOfIncomingMsg = "\n";
        initReader(endTagOfIncomingMsg);
    }

    public void connect(String endTagOfIncomingMsg) throws UnknownHostException, IOException
    {
        socket = new Socket(host, port);
        out = socket.getOutputStream();
        in = socket.getInputStream();
        LOGGER.info("socket is setup, host:" + host + " port:" + port);
        SocketUtil.printSocketInfo(socket);

        initReader(endTagOfIncomingMsg);
    }

    private void initReader(String endTagOfIncomingMsg)
    {
        reader = new SocketReaderThread(in, endTagOfIncomingMsg, new SocketReaderThread.MessageProcessor(){
        	public void process(long seq, String message){
        		
        	}
        });
        reader.start();
        LOGGER.info("socket reader is setup, ENDTAG:" + reader.endTag());

    }

    public void send(String data) throws IOException
    {

        char[] chars = data.toCharArray();
        for (char i : chars)
        {
            out.write(i);
        }
        LOGGER.debug("sent data:" + data);

        // after test, no affection found on flush here or not
        out.flush();
        LOGGER.debug("explicitly flushed");

    }

    public void disconnect()
    {
        if (this.reader != null)
        {
            reader.interrupt();
        }

        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                LOGGER.error("error", e);
            }
        }

        LOGGER.info("disconnected host:" + host + " port:" + port);
    }

    public static void main(String[] args) throws UnknownHostException,
            IOException,
            InterruptedException
    {

        String host = "localhost";
        int port = 44444;

        if (args.length >= 2)
        {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        ComplexSocketClient socketClient = new ComplexSocketClient(host, port);

        try
        {
            String INCOMING_END_TAG = "Z";
            socketClient.connect(INCOMING_END_TAG);

            String m_strPrompt = "input>";
            String exitMark = "q";
            boolean bExit = false;
            BufferedReader m_in = new BufferedReader(new InputStreamReader(System.in));
            while (!bExit)
            {
                System.out.print(m_strPrompt);

                String strCmd = null;
                while (strCmd == null)
                {
                    strCmd = m_in.readLine();
                }

                if (strCmd.equalsIgnoreCase(exitMark))
                {
                    bExit = true;
                }
                if (strCmd.length() > 0)
                {
                    socketClient.send(strCmd);
                }
            }
        }
        catch (final IOException e)
        {
            LOGGER.error("", e);
        }
        socketClient.disconnect();

    }



}
