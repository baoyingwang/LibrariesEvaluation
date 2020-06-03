package baoying.eval.jdk.util;

import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JNDIUtil {

	private Context context;

	public JNDIUtil(String icf, String url) throws JMSException,
			NamingException {

		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, icf);
		environment.put(Context.PROVIDER_URL, url);
		context = new InitialContext(environment);
	}

	private Object getObjectByName(String ObjName) throws NamingException {

		return context.lookup(ObjName);
	}

	public ConnectionFactory getConnectionFactory(String factoryName)
			throws NamingException {
		return (ConnectionFactory) getObjectByName(factoryName);
	}

	public Queue getQueue(String queueName) throws NamingException {
		return (Queue) getObjectByName(queueName);
	}

	public Topic getTopic(String topicName) throws NamingException {
		return (Topic) getObjectByName(topicName);
	}

}
