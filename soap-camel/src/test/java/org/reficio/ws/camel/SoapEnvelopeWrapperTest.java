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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.common.ResourceUtils;

public class SoapEnvelopeWrapperTest {

    private SoapEnvelopeWrapper wrapper;

    @Before
    public void setup() {
        Wsdl wsdl = Wsdl.parse(ResourceUtils.getResource("wsdl/currency-convertor.wsdl").toString());
        SoapBuilder builder = wsdl.binding()
           .localPart("CurrencyConvertorSoap")
           .find();
        wrapper = new SoapEnvelopeWrapper(builder);
    }

    @Test
    public void shouldWrapRequest() {
        CamelContext ctx = new DefaultCamelContext();
        String payload = "<web:ConversionRate xmlns:web=\"http://www.webserviceX.NET/\">\n" +
            "     <web:FromCurrency>KHR</web:FromCurrency>\n" +
            "    <web:ToCurrency>MDL</web:ToCurrency>\n" +
            "</web:ConversionRate>";

        String request = wrapper.wrap(payload, ctx);

        assertThat(request, startsWith("<soapenv:Envelope"));
        assertThat(request, endsWith("</soapenv:Envelope>"));
        assertThat(request, containsString("soapenv:Body"));
        assertThat(request, containsString(payload));
    }

    @Test
    public void shouldUnwrapResponse() {
        CamelContext ctx = new DefaultCamelContext();
        String payload = 
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                  xmlns:web=\"http://www.webserviceX.NET/\">\n" +
            "   <soapenv:Header/>\n" +
            "   <soapenv:Body>\n" +
            "      <web:ConversionRateResponse>\n" +
            "         <web:ConversionRateResult>1.30</web:ConversionRateResult>\n" +
            "      </web:ConversionRateResponse>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

        String response = wrapper.unwrap(payload, ctx);

        assertThat(response, not(containsString("soapenv:Envelope")));
        assertThat(response, not(containsString("soapenv:Body")));
        assertThat(response, containsString("web:ConversionRateResponse"));
        assertThat(response, containsString("<web:ConversionRateResult>1.30</web:ConversionRateResult>"));
    }

}
