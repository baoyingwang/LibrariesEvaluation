package baoying.eval.jdk.util;

import org.junit.Test;

public class XSLTProcText {

    @Test
    public void testGetDummyString1() throws  Exception{

        String xsltfile ="src/test/resources/baoying/util/XSLTProc.xsl";
        String xmlfile="src/test/resources/baoying/util/XSLTProc.xml";
        
        
        new XSLTProc().translate(xsltfile, FileReaderUtil.read(xmlfile));


    }

}
