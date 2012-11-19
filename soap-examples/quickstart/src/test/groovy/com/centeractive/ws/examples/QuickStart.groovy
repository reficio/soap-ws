package com.centeractive.ws.examples

import com.centeractive.ws.builder.SoapBuilder
import com.centeractive.ws.builder.SoapOperation
import com.centeractive.ws.builder.core.WsdlParser
import com.centeractive.ws.client.core.SoapClient
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.junit.Test

class QuickStart {

    @Test
    void invoke() {
        WsdlParser parser = WsdlParser.parse("http://www.webservicex.net/CurrencyConvertor.asmx?WSDL");
        SoapBuilder builder = parser.binding().localPart("CurrencyConvertorSoap").builder();
        SoapOperation operation = builder.operation().soapAction("http://www.webserviceX.NET/ConversionRate").find();

//        SoapBuilder builder = parser.binding().localPart("CurrencyConvertorHttpPost").builder();
//        SoapOperation operation = builder.operation().name("ConversionRate").find()

        String input = builder.buildInputMessage(operation)
        println input

        def slurper = new XmlSlurper().parseText(input)
        slurper.Body.ConversionRate.FromCurrency = "CHF"
        slurper.Body.ConversionRate.ToCurrency = "PLN"

        input = toPrettyXml(slurper)

        SoapClient client = SoapClient.builder().endpointUrl(builder.getServiceUrls().iterator().next()).build();
        String output = client.post("http://www.webserviceX.NET/ConversionRate", input);

        def response = new XmlSlurper().parseText(output)
        println(toPrettyXml(response))

        println "\n" + response.Body.ConversionRateResponse.ConversionRateResult.text()

    }

    def static toPrettyXml(xml) {
        XmlUtil.serialize(new StreamingMarkupBuilder().bind { mkp.yield xml })
    }

}
