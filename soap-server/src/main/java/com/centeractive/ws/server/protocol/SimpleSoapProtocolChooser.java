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
package com.centeractive.ws.server.protocol;

import org.springframework.ws.transport.TransportInputStream;

import java.io.IOException;
import java.util.Iterator;

/**
 * Chooses which soap version is used in the transmission.
 * It is a simple implementation that verifies the the content-type header.
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SimpleSoapProtocolChooser implements SoapProtocolChooser {

    private static final String CONTENT_TYPE_HEADER_NAME = "content-type";
    private static final String CONTENT_TYPE_HEADER_CONTENT_SOAP_11 = "text/xml";

    /**
     * @param transportInputStream input stream from the SOAP client
     * @return true if SOAP 1.1 is used
     * @throws IOException in case an error occurs
     */
    @Override
    public boolean useSoap11(TransportInputStream transportInputStream) throws IOException {
        for (Iterator headerNames = transportInputStream.getHeaderNames(); headerNames.hasNext(); ) {
            String headerName = (String) headerNames.next();
            for (Iterator headerValues = transportInputStream.getHeaders(headerName); headerValues.hasNext(); ) {
                String headerValue = (String) headerValues.next();
                if (headerName.toLowerCase().contains(CONTENT_TYPE_HEADER_NAME)) {
                    if (headerValue.trim().toLowerCase().contains(CONTENT_TYPE_HEADER_CONTENT_SOAP_11)) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * @param transportInputStream input stream from the SOAP client
     * @return true if SOAP 1.2 is used
     * @throws IOException in case an error occurs
     */
    @Override
    public boolean useSoap12(TransportInputStream transportInputStream) throws IOException {
        return useSoap11(transportInputStream) == false;
    }

}

