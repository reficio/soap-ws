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
package com.centeractive.ws.server.responder;

import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.Source;

/**
 * Interface describing the functionality of a SOAP Request Responder
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public interface RequestResponder {

    /**
     * Returns a response to a SOAP message.<br/>
     * <p/>
     * How to get full SOAP message envelope (envelope = header + body):
     * msg.getEnvelope().getSource()
     * <p/>
     * How to get header section:
     * msg.getEnvelope().getHeader().getSource()
     * <p/>
     * How to get body section:
     * msg.getEnvelope().getBody().getSource()
     * <p/>
     * How to convert XML String to XML Source:
     * XmlUtils.xmlStringToSource(string);
     * <p/>
     * How to convert XML Source to XML String:
     * XmlUtils.sourceToXmlString(source);
     *
     * @param request SOAP message to handle
     * @return response in the XML source format containing the whole SOAP envelope
     */
    Source respond(SoapMessage request);

}
