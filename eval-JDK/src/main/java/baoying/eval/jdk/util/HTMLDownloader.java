package baoying.eval.jdk.util;

import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLDownloader
{
    public static final Logger LOGGER     = LoggerFactory.getLogger(HTMLDownloader.class);

    private DefaultHttpClient  httpclient = new DefaultHttpClient();

    private final String SCANNER_CHARSET ;
    public HTMLDownloader(final String charset){
    	this.SCANNER_CHARSET = charset;
    }
    public String download(String url, int retry) throws Exception
    {

        int realRetry = retry;
        if (retry < 0)
        {
            realRetry = Integer.MAX_VALUE;
        }

        String result = "";
        for (int i = 0; i < realRetry; i++)
        {
            if(i>0){
                Thread.sleep(5 *1000);
                LOGGER.info("sleep 5s before retry");
            }
            try
            {
                result = download(url);
                break;

            }
            catch (Exception e)
            {
                LOGGER.error("",e);
            }
        }

        return result;

    }

    private String download(String pageUrl) throws Exception
    {

        String result = null;
        InputStream in = null;

        try
        {

            HttpResponse response = null;
            HttpEntity entity = null;

            HttpGet request = new HttpGet();
            request.setURI(new URI(pageUrl));

            response = httpclient.execute(request);
            LOGGER.debug("status line : {} ", response.getStatusLine());

            entity = response.getEntity();

            logHeader(response.getAllHeaders());

            if (entity != null)
            {
                LOGGER.debug("entity type, isStreaming {} ", entity.isStreaming());
                LOGGER.debug("entity type, isChunked   {} ", entity.isChunked());
                LOGGER.debug("entity type, isRepeatable{} ", entity.isRepeatable());
                LOGGER.debug("" + entity.getContentType());
                LOGGER.debug("" + entity.getContentLength());

                in = entity.getContent();

                // \A the beginning of input
                // http://download.java.net/jdk7/archive/b123/docs/api/java/util/regex/Pattern.html?is-external=true
                final String SCANNER_DEL = "\\A";
                
                result = new Scanner(in, SCANNER_CHARSET).useDelimiter(SCANNER_DEL).next();

            }

        }
        finally
        {

            if (in != null)
            {
                in.close();
            }
        }

        return result;
    }

    private void logHeader(Header[] headers)
    {

        for (int i = 0; i < headers.length; i++)
        {
            LOGGER.debug(headers[i].toString());
        }
    }
}
