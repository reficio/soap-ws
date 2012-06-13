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

import com.centeractive.ws.builder.core.SoapBuilder;
import com.centeractive.ws.builder.core.SoapContext;
import com.centeractive.ws.builder.soap.XmlUtils;
import com.centeractive.ws.builder.soap.domain.OperationWrapper;
import com.centeractive.ws.server.SoapServerException;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

/**
 * Convenience class to create SOAP mock services.
 * It replies with an sample response to all requests send to this responder.
 * SoapContext passed in the constructor may be used to fine-tune the generation
 * of the sample responses.
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class AutoResponder extends AbstractResponder {

    private final SoapContext context;

    /**
     * Constructs an auto responder for the specified binding of the builder
     *
     * @param builder     Soap builder used to construct messages
     * @param bindingName Binding to be used - builders may contain many bindings
     */
    public AutoResponder(SoapBuilder builder, QName bindingName) {
        super(builder, bindingName);
        context = SoapContext.builder().exampleContent(true).build();
    }

    /**
     * Constructs an auto responder for the specified binding of the builder, fine-tuning the content of the generated messages
     * by passing the SoapContext
     *
     * @param builder     Soap builder used to construct messages
     * @param bindingName Binding to be used - builders may contain many bindings
     * @param context     Contect that is passed to the builder to fine-tune the content of the generated responses
     */
    public AutoResponder(SoapBuilder builder, QName bindingName, SoapContext context) {
        super(builder, bindingName);
        this.context = context;
    }

    @Override
    public Source respond(OperationWrapper invokedOperation, SoapMessage message) {
        try {
            String response = getBuilder().buildSoapMessageFromOutput(invokedOperation, context);
            return XmlUtils.xmlStringToSource(response);
        } catch (Exception e) {
            throw new SoapServerException(e);
        }
    }

}
