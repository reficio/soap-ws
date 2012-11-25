package com.centeractive.ws.examples

import com.centeractive.ws.builder.core.WsdlParser
import com.centeractive.ws.client.core.SoapClient
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.junit.Test

class QuickStart {

    @Test
    void invokeConversionRate() {
        // generate the message (the quickest way)
        String input = WsdlParser.parse("http://www.webservicex.net/CurrencyConvertor.asmx?WSDL")
            .binding("{http://www.webserviceX.NET/}CurrencyConvertorSoap")
            .operation().soapAction("http://www.webserviceX.NET/ConversionRate")
            .find().buildInputMessage()

        // modify the request providing real data
        def slurper = new XmlSlurper().parseText(input)
        slurper.Body.ConversionRate.FromCurrency = "CHF"
        slurper.Body.ConversionRate.ToCurrency = "PLN"
        input = toPrettyXml(slurper)

        // construct the soap client and post the message
        SoapClient client = SoapClient.builder().endpointUrl("http://www.webservicex.net/CurrencyConvertor.asmx").build();
        String output = client.post("http://www.webserviceX.NET/ConversionRate", input);
        def response = new XmlSlurper().parseText(output)

        // print whole response and the conversion rate only
        println(toPrettyXml(response))
        println "\n" + response.Body.ConversionRateResponse.ConversionRateResult.text()
    }

    def static toPrettyXml(xml) {
        XmlUtil.serialize(new StreamingMarkupBuilder().bind { mkp.yield xml })
    }

}
