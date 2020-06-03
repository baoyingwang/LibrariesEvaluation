package baoying.eval.jdk.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * below programm is only for quick start.
 * for better usage, or performance, pls check the EmailSender
 *
 * @author baoying.wang
 *
 */
//Use Spring + javamail to send email from gmail
//http://www.yuloo.com/news/0904/226033.html

//JAVA Gmail group sending
//http://wenku.baidu.com/view/d8c0d18d680203d8ce2f242e.html

public class SimpleEmailSender
{

    private final String     smtpHost; // = "smtp.gmail.com";
    private final String     user;    // = "wbaoying";
    private final String     pwd;     // = "";
    private final Properties props;

    /**
     *
     * @param smtpHost
     * @param user
     * @param pwd
     * @param additionalProps - not yet support. any value will ge skipped.
     */
    public SimpleEmailSender(String smtpHost, String user, String pwd, Properties additionalProps)
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

    public void send(String toEmail, String subject, String content) throws AddressException,
            MessagingException
    {

        String[] toEmails = { toEmail };
        send(toEmails, subject, content);
    }

    public void send(String[] toEmails, String subject, String content) throws AddressException,
            MessagingException
    {

        String[] to = toEmails;

        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user));

        for (int i = 0; i < to.length; i++)
        {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
        }

        message.setSubject(subject);
        message.setText(content);
        Transport transport = session.getTransport("smtp");
        transport.connect(smtpHost, user, pwd);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    public static void main(String[] args) throws AddressException, MessagingException
    {

        final String smtpHost = "smtp.gmail.com";
        final String user = "wbaoying";
        final String pwd = "";
        Properties additinoalProps = null;

        String toEmail = "winnerbao@163.com";
        String subject = "";
        String content = "";

        new SimpleEmailSender(smtpHost, user, pwd, additinoalProps).send(toEmail, subject, content);
    }
}
