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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.StringContains.containsString;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.reficio.ws.client.TransmissionException;
import org.reficio.ws.test.junit.Server;
import org.reficio.ws.test.junit.SoapRule;

@Server(wsdl = "classpath:wsdl/currency-convertor.wsdl", binding = "CurrencyConvertorSoap")
public class LocalProducerTest extends CamelTestSupport {

    @ClassRule
    public static SoapRule classRule = new SoapRule();

    @EndpointInject(uri = "direct:successful")
    private ProducerTemplate successful;

    @EndpointInject(uri = "direct:catching")
    private ProducerTemplate catching;

    @EndpointInject(uri = "mock:exception")
    private MockEndpoint exceptionEndpoint;

    @Test
    public void shouldSendMessage() throws InterruptedException {
        String request = "<web:ConversionRate xmlns:web=\"http://www.webserviceX.NET/\">\n" +
            "     <web:FromCurrency>KHR</web:FromCurrency>\n" +
            "    <web:ToCurrency>MDL</web:ToCurrency>\n" +
            "</web:ConversionRate>";

        Object body = successful.requestBody(request);

        Assert.assertNotNull(body);
        String response = String.class.cast(body);
        Assert.assertNotNull(response);
        Assert.assertThat(response, not(containsString("soapenv:Envelope")));
        Assert.assertThat(response, not(containsString("soapenv:Body")));
        Assert.assertThat(response, containsString("web:ConversionRateResponse"));
        Assert.assertThat(response, containsString("<web:ConversionRateResult>1.30</web:ConversionRateResult>"));
    }

    @Test
    public void shouldThrowWhenProducerFails() throws InterruptedException {
        String request = "<a/>"; // unknown request

        catching.requestBody(request);

        exceptionEndpoint.expectedMessageCount(1);
        exceptionEndpoint.setResultWaitTime(2000);
        exceptionEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder[] createRouteBuilders() throws Exception {
        return new RouteBuilder[] {
            new SoapWsExampleRouteBuilder(), new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:successful").to("direct:send");

                    from("direct:catching")
                        .doTry()
                            .to("direct:send")
                        .doCatch(TransmissionException.class)
                            .to("mock:exception");
                }
            }
        };
    }

}
