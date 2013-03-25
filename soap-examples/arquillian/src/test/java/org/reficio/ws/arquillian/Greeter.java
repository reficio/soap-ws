package org.reficio.ws.arquillian;

import groovy.util.XmlSlurper;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.common.ResourceUtils;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.PrintStream;
import java.net.URL;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
public class Greeter {

    public String getConversionRate(String fromCurrency, String toCurrency) throws Exception {
        URL url = ResourceUtils.getResource("wsdl/currency-convertor.wsdl");

        // generate the message (the quickest way)
        String input = Wsdl.parse(url)
                .binding().name("{http://www.webserviceX.NET/}CurrencyConvertorSoap").find()
                .operation().soapAction("http://www.webserviceX.NET/ConversionRate").find()
                .buildInputMessage();

        SoapClient client = SoapClient.builder()
                .endpointUri("http://localhost:51515/service")
                .build();

        String response = client.post(input);

        XmlSlurper slurper = new XmlSlurper(XMLReaderFactory.createXMLReader());
        return slurper.parseText(response).toString();
    }

    public void greet(PrintStream to, String name) {
        to.println(createGreeting(name));
    }

    public String createGreeting(String name) {
        return "Hello, " + name + "!";
    }

}
