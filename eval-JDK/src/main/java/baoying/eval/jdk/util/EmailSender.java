package baoying.eval.jdk.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


//http://www.yuloo.com/news/0904/226033.html
//http://wenku.baidu.com/view/d8c0d18d680203d8ce2f242e.html

public class EmailSender
{

    private final String     smtpHost;         // = "smtp.gmail.com";
    private final String     user;             // = "wbaoying";
    private final String     pwd;              // = "";
    private final Properties props;

    private Transport        transport = null;
    private Session          session   = null;

    private volatile boolean connected = false;

    /**
     *
     * @param smtpHost
     * @param user
     * @param pwd
     * @param additionalProps - not yet support. any value will ge skipped.
     */
    public EmailSender(String smtpHost, String user, String pwd, Properties additionalProps)
    {
        this.smtpHost = smtpHost;
        this.user = user;
        this.pwd = pwd;

        props = System.getProperties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.password", pwd);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        //TODO copy the additionalProps to props
    }

    public void connect() throws MessagingException
    {

        if (!connected)
        {
            session = Session.getDefaultInstance(props, null);
            transport = session.getTransport("smtp");
            transport.connect(smtpHost, user, pwd);

            connected = true;
        }
        else
        {
            //LOGGER, ignore the request because it is connected status
        }
    }

    public void connect(boolean force) throws MessagingException
    {

        if (force)
        {
            close();
            connect();
        }
        else
        {
            connect();
        }
    }

    public void close() throws MessagingException
    {
        transport.close();
    }

    public boolean isConnected()
    {
        return this.connected;
    }

    public void send(String toEmail, String subject, String content) throws AddressException,
            MessagingException
    {
        String[] toEmails = { toEmail };
        send(toEmails, subject, content);
    }

    public void send(String[] toEmails, String subject, String content) throws AddressException,
            MessagingException
    {
        //Thing about what we do not check the
        //connection status here.
        //. if the connection status is down, exception will be throw.
        //. it makes the logic simple.
        //. otherwise, there will be full of if(connected)
        //. client has to close and re-connect

        try
        {
            String[] to = toEmails;

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));

            for (int i = 0; i < to.length; i++)
            {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
            }

            message.setSubject(subject);
            message.setText(content);

            transport.sendMessage(message, message.getAllRecipients());
        }
        catch (MessagingException e)
        {
            //not required to set connected to false on AddressException
            //because it is not caused by connection issue. it is cause by wrong email address.
            this.connected = false;
            throw e;
        }
    }

    public static void main(String[] args) throws AddressException, MessagingException
    {

        final String smtpHost = "smtp.gmail.com";
        final String user = "wbaoying";
        final String pwd = "";
        final Properties additinoalProps = null;

        String toEmail = "winnerbao@163.com";
        String subject = "";
        String content = "";

        EmailSender s = new EmailSender(smtpHost, user, pwd, additinoalProps);
        s.connect();
        s.send(toEmail, subject, content);
        s.close();

    }
}
