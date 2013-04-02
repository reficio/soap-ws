/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.reficio.ws.quickstart

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.junit.Test
import org.reficio.ws.builder.core.Wsdl
import org.reficio.ws.client.core.SoapClient

class QuickStart {

    @Test
    void invokeConversionRate() {
        // generate the message (the quickest way)
        String input = Wsdl.parse("http://www.webservicex.net/CurrencyConvertor.asmx?WSDL")
            .binding().name("{http://www.webserviceX.NET/}CurrencyConvertorSoap").find()
            .operation().soapAction("http://www.webserviceX.NET/ConversionRate").find()
            .buildInputMessage()

        // modify the request providing real data
        def slurper = new XmlSlurper().parseText(input)
        slurper.Body.ConversionRate.FromCurrency = "CHF"
        slurper.Body.ConversionRate.ToCurrency = "PLN"
        input = toPrettyXml(slurper)

        // construct the soap client and post the message
        SoapClient client = SoapClient.builder()
                .endpointUri("http://www.webservicex.net/CurrencyConvertor.asmx")
                .build();

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
