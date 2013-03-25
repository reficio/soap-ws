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
package org.reficio.ws.arquillian;

import groovy.util.XmlSlurper;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.common.ResourceUtils;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.PrintStream;
import java.net.URL;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
public class Greeter {

    public String getConversionRate(String fromCurrency, String toCurrency) throws Exception {
        URL url = ResourceUtils.getResource("wsdl/currency-convertor.wsdl");

        // generate the message (the quickest way)
        String input = Wsdl.parse(url)
                .binding().name("{http://www.webserviceX.NET/}CurrencyConvertorSoap").find()
                .operation().soapAction("http://www.webserviceX.NET/ConversionRate").find()
                .buildInputMessage();

        SoapClient client = SoapClient.builder()
                .endpointUri("http://localhost:51515/service")
                .build();

        String response = client.post(input);

        XmlSlurper slurper = new XmlSlurper(XMLReaderFactory.createXMLReader());
        return slurper.parseText(response).toString();
    }

    public void greet(PrintStream to, String name) {
        to.println(createGreeting(name));
    }

    public String createGreeting(String name) {
        return "Hello, " + name + "!";
    }

}
