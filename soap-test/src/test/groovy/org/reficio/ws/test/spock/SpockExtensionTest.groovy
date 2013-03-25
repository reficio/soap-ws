/**
 * Copyright (c) 2012 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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
package org.reficio.ws.test.spock

import groovy.util.logging.Log4j
import org.reficio.ws.builder.SoapBuilder
import org.reficio.ws.builder.SoapOperation
import org.reficio.ws.builder.core.Wsdl
import org.reficio.ws.client.core.SoapClient
import spock.lang.Specification

@Log4j
@Server(wsdl = "http://www.webservicex.net/CurrencyConvertor.asmx?WSDL", binding = "CurrencyConvertorSoap")
class SpockExtensionTest extends Specification {

    static final String WSDL = "http://www.webservicex.net/CurrencyConvertor.asmx?WSDL";

    @Server(wsdl = "http://www.webservicex.net/CurrencyConvertor.asmx?WSDL", binding = "CurrencyConvertorSoap", port = 41414)
    def "method specific server"() {
        setup:
        SoapClient client = SoapClient.builder().endpointUri("http://localhost:41414/service").build();
        SoapBuilder builder = Wsdl.parse(WSDL).binding().localPart("CurrencyConvertorSoap").find();
        SoapOperation operation = builder.operation().name("ConversionRate").find();
        String request = builder.buildInputMessage(operation);
        log.info("\n" + request);

        when:
        String response = client.post(request);

        then:
        assert response != null
        log.info("\n" + response);
    }

    def "test specific server"() {
        setup:
        SoapClient client = SoapClient.builder().endpointUri("http://localhost:51515/service").build();
        SoapBuilder builder = Wsdl.parse(WSDL).binding().localPart("CurrencyConvertorSoap").find();
        SoapOperation operation = builder.operation().name("ConversionRate").find();
        String request = builder.buildInputMessage(operation);
        log.info("\n" + request);

        when:
        String response = client.post(request);

        then:
        assert response != null
        log.info("\n" + response);
    }

}
