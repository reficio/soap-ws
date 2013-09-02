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
package org.reficio.ws.camel;

import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.common.ResourceUtils;

public class LocalConsumerTest extends CamelTestSupport {

    private SoapClient client;
    private SoapBuilder builder;

    @EndpointInject(uri = "mock:extracted")
    private MockEndpoint extracedMock;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        client = SoapClient.builder()
            .endpointUri("http://localhost:9090/service")
            .build();

        Wsdl wsdl = Wsdl.parse(ResourceUtils.getResource("wsdl/currency-convertor.wsdl").toString());
        builder = wsdl.binding()
           .localPart("CurrencyConvertorSoap")
           .find();
    }

    @Test
    public void shouldProcessMessage() throws InterruptedException {
        SoapOperation operation = builder.operation()
           .soapAction("http://www.webserviceX.NET/ConversionRate")
           .find();
        String request = builder.buildInputMessage(operation);

        String response = client.post(request);

        extracedMock.expectedMessageCount(1);
        extracedMock.setResultWaitTime(2000);
        extracedMock.assertIsSatisfied();
        Assert.assertNotNull(response);
        Assert.assertThat(response, containsString("<soapenv:Envelope"));
        Assert.assertThat(response, containsString("<a>bc</a>"));
        Assert.assertThat(response, endsWith("</soapenv:Envelope>"));

        Exchange extracted = extracedMock.getExchanges().get(0);
        String extractedBody = context().getTypeConverter().convertTo(String.class, extracted.getIn().getBody());
        Assert.assertThat(extractedBody, startsWith("<web:ConversionRate"));
        Assert.assertThat(extractedBody, endsWith("</web:ConversionRate>"));
    }

    @Override
    protected RouteBuilder[] createRouteBuilders() throws Exception {
        return new RouteBuilder[] {
            new SoapWsExampleRouteBuilder(), new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:ConversionRate")
                        .to("mock:extracted");
                }
            }
        };
    }

}
