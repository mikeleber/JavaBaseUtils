import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

public class SimpleSoap {
    public static void main(String[] args) throws IOException {
        new SimpleSoap().call("http://currencyconverter.kowabunga.net/converter.asmx", null);
    }

    public void call(String soapEndpoint, String strSoapAction) throws IOException {
        //wsdl file :http://currencyconverter.kowabunga.net/converter.asmx?wsdl
        InputStream soapRequestFile = org.basetools.util.io.FileUtils.streamFromClassloader(getClass(), "SoapRequestFile.xml");
        CloseableHttpClient client = getCloseableHttpClient();
        HttpPost request = new HttpPost(soapEndpoint); //Create the request
        request.addHeader("Content-Type", "text/xml"); //adding header
        if (strSoapAction != null) {
            request.addHeader("SOAPAction", strSoapAction);
        }

        request.setEntity(new InputStreamEntity(soapRequestFile));
        CloseableHttpResponse response = client.execute(request);//Execute the command

        int statusCode = response.getStatusLine().getStatusCode();//Get the status code and assert
        System.out.println("Status code: " + statusCode);

        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");//Getting the Response body
        System.out.println(responseString);
    }

    private CloseableHttpClient getCloseableHttpClient() {
        HttpClientBuilder clientbuilder = HttpClientBuilder.create(); //create client
        // Set HTTP proxy, if specified in system properties
        if (System.getProperty("http.proxyHost") != null) {
            int port = 80;
            if (System.getProperty("http.proxyPort") != null) {
                port = Integer.parseInt(System.getProperty("http.proxyPort"));
            }
            HttpHost proxy = new HttpHost(System.getProperty("http.proxyHost"), port, "http");
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            clientbuilder.setRoutePlanner(routePlanner);
        }
        CloseableHttpClient client = clientbuilder.build();
        return client;
    }
}
