package baoying.eval.jdk.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
// For write operation

/**
 * refer
 * http://docs.oracle.com/javase/tutorial/jaxp/xslt/intro.html
 * 
 * 
 *
 */
public class XSLTProc {

    // assume Unicode UTF-8 encoding
    private static String charsetName = "UTF-8";

    // the scanner object
    private static Scanner scanner = new Scanner(new BufferedInputStream(System.in), charsetName);

    
	/**
	 * argument1: xslt
	 * argument2: xml 
	 * output: translated result
	 * @throws FileNotFoundException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws Exception{

		
		String xsltfile = null;
		String xmlcontent = null;

		if (args.length == 1) {
			xsltfile = args[0];
			xmlcontent = getXMLContent(System.in);
		} else if (args.length == 2) {
			xsltfile = args[0];
			xmlcontent = FileReaderUtil.read(args[1]);
		}else{
			usage();
			System.exit(-1);
		}
		
		new XSLTProc().translate(xsltfile,xmlcontent );
		System.out.println();
	}



	private static String getXMLContent(InputStream in) {
		return scanner.nextLine();
	}
	
	



	private static void usage() {
		System.out.println("java XSLTProc xsltfile xmlfile");
		System.out.println("OR");
		System.out.println("xml file as standard input: cat xmlfile| java XSLTProc xsltfile");
		
	}
	
	public void translate(String xsltfile, String xmlcontent) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
        File stylesheet = new File(xsltfile);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xmlcontent.getBytes()));

        // Use a Transformer for output
        TransformerFactory tFactory = TransformerFactory.newInstance();
        StreamSource stylesource = new StreamSource(stylesheet);
        Transformer transformer = tFactory.newTransformer(stylesource);

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result);
        
        
	}	

}
