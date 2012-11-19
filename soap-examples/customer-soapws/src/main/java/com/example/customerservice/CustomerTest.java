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
package com.example.customerservice;

import com.centeractive.ws.builder.SoapBuilder;
import com.centeractive.ws.builder.SoapOperation;
import com.centeractive.ws.builder.core.WsdlParser;
import com.centeractive.ws.client.core.SoapClient;
import com.centeractive.ws.common.ResourceUtils;
import com.centeractive.ws.server.core.SoapServer;
import com.centeractive.ws.server.responder.AutoResponder;
import org.apache.commons.lang3.time.StopWatch;

import java.net.URL;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11/11/12
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerTest {

    private static final int PORT = 9091;
    private static final URL WSDL_URL = ResourceUtils.getResourceWithAbsolutePackagePath("", "CustomerService.wsdl");

    public void startServer() {

        WsdlParser parser = WsdlParser.parse(WSDL_URL);
        parser.printBindings();
        SoapBuilder builder = parser.binding("{http://customerservice.example.com/}CustomerServiceServiceSoapBinding").builder();
        AutoResponder responder = new AutoResponder(builder);

        SoapServer server = SoapServer.builder().httpPort(PORT).build();
        server.start();
        server.registerRequestResponder("/jdays", responder);


    }

    public void startClient() {
        SoapServer server = SoapServer.builder().httpPort(PORT).build();
        WsdlParser parser = WsdlParser.parse(WSDL_URL);
        parser.printBindings();
        SoapBuilder builder = parser.binding("{http://customerservice.example.com/}CustomerServiceServiceSoapBinding").builder();
        Iterator<SoapOperation> it = builder.getOperations().iterator();
        it.next();
        String msg = builder.buildInputMessage(it.next());

        System.out.println(msg);


        SoapClient client = SoapClient.builder().endpointUrl("http://127.0.0.1:9091/jdays").build();

        StopWatch watch = new StopWatch();
        watch.start();
//        for (int i = 0; i < 10000; i++) {
            String response = client.post(msg);
//        }
        watch.stop();
        System.out.println(watch.toString());

        System.out.println(response);
    }

    public static void main(String args[]) throws InterruptedException {
        CustomerTest customerTest = new CustomerTest();
        customerTest.startServer();
        customerTest.startClient();
    }


}
