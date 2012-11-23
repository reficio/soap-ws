/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
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
package com.centeractive.ws.test;

import com.centeractive.ws.builder.SoapBuilder;
import com.centeractive.ws.builder.SoapOperation;
import com.centeractive.ws.builder.core.WsdlParser;
import com.centeractive.ws.client.core.SoapClient;
import org.apache.log4j.Logger;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

@Server(wsdl = "http://www.webservicex.net/CurrencyConvertor.asmx?WSDL", binding = "CurrencyConvertorSoap")
public class SoapRuleTest {

    private final static Logger log = Logger.getLogger(SoapRuleTest.class);
    private static final String WSDL = "http://www.webservicex.net/CurrencyConvertor.asmx?WSDL";

    @Rule
    public SoapRule rule = new SoapRule();

    @ClassRule
    public static SoapRule classRule = new SoapRule();

    @Test
    @Server(wsdl = WSDL, binding = "CurrencyConvertorSoap", port = 41414)
    public void testSoapMock_perMethodServer() {
        SoapClient client = SoapClient.builder().endpointUrl("http://localhost:41414/service").build();
        SoapBuilder builder = WsdlParser.parse(WSDL).binding().localPart("CurrencyConvertorSoap").builder();
        SoapOperation operation = builder.operation().name("ConversionRate").find();
        String request = builder.buildInputMessage(operation);

        log.info("\n" + request);

        String response = client.post(request);
        log.info("\n" + response);

    }

    @Test
    public void testSoapMock_perClassServer() {
        SoapClient client = SoapClient.builder().endpointUrl("http://localhost:51515/service").build();
        SoapBuilder builder = WsdlParser.parse(WSDL).binding().localPart("CurrencyConvertorSoap").builder();
        SoapOperation operation = builder.operation().name("ConversionRate").find();
        String request = builder.buildInputMessage(operation);

        log.info("\n" + request);

        String response = client.post(request);
        log.info("\n" + response);

    }

}
