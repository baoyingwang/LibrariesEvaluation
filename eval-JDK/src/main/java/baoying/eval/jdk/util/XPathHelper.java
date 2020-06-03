package baoying.eval.jdk.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XPathHelper
{
    private static final Logger logger = LoggerFactory.getLogger(XPathHelper.class);
    

    private final Document   document;
    private final XPath      xPath;

    /**
     * Constructor.
     * 
     * @param xml the XML
     * 
     * @throws Exception an error occurred
     */
    public XPathHelper(final String xml) throws Exception
    {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();

        // parse the XML string - this should close the StringReader
        this.document = builder.parse(new InputSource(new StringReader(xml)));
        this.xPath = XPathFactory.newInstance().newXPath();
    }

    /**
     * Evaluate the XPath expression
     * 
     * @param xPath the XPath expression
     * @param returnType the XPath return type, see: {@link XPathConstants}.
     * @param type the return type class
     * @return the result of evaluating the expression and converting the result
     *         to <code>returnType</code>
     * @throws Exception an error occurred
     */
    public <T> T evaluate(final String xPath, final QName returnType, final Class<T> type) throws Exception
    {
        final XPathExpression expression = this.xPath.compile(xPath);
        @SuppressWarnings("unchecked")
        final T result = (T) expression.evaluate(this.document, returnType);

        return result;
    }

    public String getStringV(String xpath)
    {
        String v = null;
        try
        {
            v = evaluate(xpath, XPathConstants.STRING, String.class);
        }
        catch (Exception e)
        {
            logger.warn("faile to deal xml from xpath:" + xpath);
        }

        return v;

    }

    public NodeList getNodeList(String xpath)
    {
        org.w3c.dom.NodeList v = null;
        try
        {
            v = evaluate(xpath, XPathConstants.NODESET, org.w3c.dom.NodeList.class);
        }
        catch (Exception e)
        {
            logger.warn("faile to deal xml from xpath:" + xpath);
        }

        return v;

    }

    public List<String> getNodeListV(String xpath)
    {
        List<String> result = new ArrayList<String>();
        org.w3c.dom.NodeList nodeList = null;
        try
        {
            nodeList = evaluate(xpath, XPathConstants.NODESET, org.w3c.dom.NodeList.class);

            if (nodeList != null && nodeList.getLength() > 0)
            {
                for (int i = 0; i < nodeList.getLength(); i++)
                {
                    final Node node = nodeList.item(i);
                    if (node != null)
                    {
                        final String s = node.getTextContent();
                        result.add(s);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.warn("faile getNodeListV on xpath:" + xpath);
        }

        return result;

    }

}
