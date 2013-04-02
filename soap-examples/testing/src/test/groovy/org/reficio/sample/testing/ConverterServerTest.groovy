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
package org.reficio.sample.testing

import org.reficio.ws.builder.core.Wsdl
import org.reficio.ws.client.core.SoapClient
import org.reficio.ws.common.ResourceUtils
import org.reficio.ws.test.spock.Server
import spock.lang.Specification

import static org.reficio.sample.util.ExampleUtils.toPrettyXml

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 *
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
class ConverterServerTest extends Specification {

    URL url = ResourceUtils.getResource("wsdl/currency-convertor.wsdl")

    String supplement(String message, String from, String to) {
        def slurper = new XmlSlurper().parseText(message)
        slurper.Body.ConversionRate.FromCurrency = from
        slurper.Body.ConversionRate.ToCurrency = to
        return toPrettyXml(slurper)
    }

    Double extractRate(String response) {
        def slurper = new XmlSlurper().parseText(response)
        Double conversionRate = slurper.Body.ConversionRateResponse.ConversionRateResult.toString() as Double
        return conversionRate
    }

    @Server(wsdl = "classpath:wsdl/currency-convertor.wsdl",
            binding = "CurrencyConvertorSoap",
            port = 9090)
    def "conversion rate test"() {

        setup:
        SoapClient client = SoapClient.builder()
                .endpointUri("http://127.0.0.1:9090/service")
                .build()
        String message = Wsdl.parse(url)
                .binding().localPart("CurrencyConvertorSoap").find()
                .operation().name("ConversionRate").find()
                .buildInputMessage()

        message = supplement(message, from, to)
        String response = client.post(message)
        Double rate = extractRate(response)


        expect:
        min < rate && rate < max

        where:
        from  | to    | min   | max
        "EUR" | "CHF" | 1.112 | 1.784
        "EUR" | "GBP" | 0.546 | 2.134
        "GBP" | "CHF" | 1.152 | 3.863


    }

}
