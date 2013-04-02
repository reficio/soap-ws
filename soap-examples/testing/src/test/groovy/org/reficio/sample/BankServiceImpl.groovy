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
package org.reficio.sample

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.reficio.ws.builder.SoapBuilder
import org.reficio.ws.builder.core.Wsdl
import org.reficio.ws.client.core.SoapClient
import org.reficio.ws.common.ResourceUtils

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 *
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
class BankServiceImpl implements BankService {

    String conversionServiceUri
    SoapClient client;

    BankServiceImpl() {
        this.conversionServiceUri = "http://127.0.0.1:9090/service"
    }

    BankServiceImpl(String conversionServiceUri) {
        this.conversionServiceUri = conversionServiceUri
    }

    BankServiceImpl(SoapClient client) {
        this.client = client
    }

    Double getExpenses(String account, String category) {
        if (!account) {
            return -1
        } else if (category == "BEER") {
            return 317
        } else {
            return Random.newInstance().nextInt(1000)
        }
    }

    Double getExpenses(String account, String category, String currency) {
        Double balance = getExpenses(account, category)
        Double conversionRate = getConversionRate(getDefaultCurrency(), currency)
        return balance * conversionRate
    }

    String getCurrency() {
        return "GBP"
    }

    String getDefaultCurrency() {
        return "GBP"
    }

    Double getConversionRate(String fromCurrency, String toCurrency) {
        URL url = ResourceUtils.getResource("wsdl/currency-convertor.wsdl")
        Wsdl wsdl = Wsdl.parse(url)

        SoapBuilder builder = wsdl.binding().localPart("CurrencyConvertorSoap").find()
        String request = builder.operation().name("ConversionRate").find().buildInputMessage()

        def slurper = new XmlSlurper().parseText(request)
        slurper.Body.ConversionRate.FromCurrency = fromCurrency
        slurper.Body.ConversionRate.ToCurrency = toCurrency
        request = toPrettyXml(slurper)

        if (!client) {
            client = SoapClient.builder()
                    .endpointUri(conversionServiceUri)
                    .build()
        }
        String response = client.post(request)
        return parseResponse(response)
    }

    Double parseResponse(String response) {
        def slurper = new XmlSlurper().parseText(response)
        Double conversionRate = slurper.Body.ConversionRateResponse.ConversionRateResult.toString() as Double
        return conversionRate
    }

    String toPrettyXml(def xml) {
        XmlUtil.serialize(new StreamingMarkupBuilder().bind { mkp.yield xml })
    }

}
