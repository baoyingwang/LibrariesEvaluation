package baoying.eval.jdk.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @deprecated it will be replaced by XPathHelper
 * 
 *
 */
public class XPathUtil {
	
	//TODO is it thread-safe?
	private static DocumentBuilderFactory domFactory = DocumentBuilderFactory
			.newInstance();
	
	//TODO is it thread-safe?
	private static DocumentBuilder builder = null;

	static {
		domFactory.setNamespaceAware(true);
		try {
			builder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
		    System.out.println("fail to initialize xpath builder");
		    e.printStackTrace();
		    System.exit(-1);
		}

	}

	public static String[] getValue(String xmlData, String xpathString)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {

		//TODO replace the ISO-8859-1
		// http://stackoverflow.com/questions/340787/parsing-xml-with-xpath-in-java
		Document doc = builder.parse( new ByteArrayInputStream(xmlData.getBytes("UTF-8")));
		XPath xpath = XPathFactory.newInstance().newXPath();
		// XPath Query for showing all nodes value
		XPathExpression expr = xpath.compile(xpathString);

		Object resultObj = expr.evaluate(doc, XPathConstants.NODESET);
		if(resultObj == null){
			return new String[0];
		}
		
		NodeList nodes = (NodeList) resultObj;
		String[] result = new String[nodes.getLength()];
		for (int i = 0; i < nodes.getLength(); i++) {
			result[i] = nodes.item(i).getTextContent();
		}
		
		return result;
	}
	
	public static void main(String[] args) throws Exception{
		
		String xml="<a> <b>abcd</b><b>abcd-2</b><c>efgh</c></a>";
		//String xpath="/a/b/text()";
		String xpath="/a/b";
		String[] result = getValue(xml,xpath);
		
		for(String x : result){
			System.out.println(x);
		}
		
	}
}
