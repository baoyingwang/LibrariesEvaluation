package baoying.eval.jdk.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLUtil {

	private static final ThreadLocal<DocumentBuilderFactory> dbfLocal = new ThreadLocal<DocumentBuilderFactory>() {
		@Override
		protected DocumentBuilderFactory initialValue() {
			return DocumentBuilderFactory.newInstance();
		}
	};

	private static final ThreadLocal<DocumentBuilder> dbLocal = new ThreadLocal<DocumentBuilder>() {
		@Override
		protected DocumentBuilder initialValue() {
			try {
				return dbfLocal.get().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new RuntimeException(e);
			}
		}
	};

	public static Document parseXmlContent(String content) {
		try {

			return dbLocal.get().parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String toFormattedXML(Document document) throws Exception {

		// initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());

		// http://stackoverflow.com/questions/4850901/formatting-xml-file-indentation
		// NOTE: we have to clean the '\r', '\n' to make the xml as a string in
		// single line. Otherwise, it will not work.
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		DOMSource source = new DOMSource(document.getDocumentElement());
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();

		return xmlString;
	}
}
