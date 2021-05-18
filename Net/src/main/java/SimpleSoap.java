import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

public class SimpleSoap {
    public static void main(String[] args) throws IOException {
        new SimpleSoap().call("http://currencyconverter.kowabunga.net/converter.asmx",null);
    }

    public void call(String soapEndpoint, String strSoapAction) throws IOException {
        //wsdl file :http://currencyconverter.kowabunga.net/converter.asmx?wsdl
        InputStream soapRequestFile = org.basetools.util.io.FileUtils.streamFromClassloader(getClass(), "SoapRequestFile.xml");

        CloseableHttpClient client = HttpClients.createDefault(); //create client
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
}
