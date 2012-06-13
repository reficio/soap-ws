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
import com.centeractive.ws.builder.soap.domain.OperationWrapper;
import com.centeractive.ws.server.OperationNotFoundException;
import com.centeractive.ws.server.SoapServerException;
import com.centeractive.ws.server.matcher.SoapOperationMatcher;
import org.springframework.ws.soap.SoapMessage;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

/**
 * Convenience class that implements the RequestResponder interface and
 * contains a mechanism that matches the request to an operation from the Binding
 * RequestResponder method is implemented, but a new abstract respond method is added
 * which contains the invoked operation as an argument.
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public abstract class AbstractResponder implements RequestResponder {

    private final SoapBuilder builder;
    private final QName bindingName;
    private final Binding binding;
    private final SoapOperationMatcher soapOperationMatcher;

    public SoapBuilder getBuilder() {
        return builder;
    }

    /**
     * Constructs a responder for the specified binding of the builder
     *
     * @param builder     Soap builder used to construct messages
     * @param bindingName Binding to be used - builders may contain many bindings
     */
    public AbstractResponder(SoapBuilder builder, QName bindingName) {
        this.builder = builder;
        this.bindingName = bindingName;
        this.binding = builder.getDefinition().getBinding(bindingName);
        this.soapOperationMatcher = new SoapOperationMatcher(builder.getDefinition(), bindingName);
    }

    /**
     * Implementation of the RequestResponder bare method.
     * It matches the SoapMessage to the binding operation and invokes the
     * abstract respond method that contains OperationWrapper as an argument.
     *
     * @param message SOAP message passed by the client
     * @return response in the XML source format containing the whole SOAP envelope
     */
    @Override
    public Source respond(SoapMessage message) {
        try {
            BindingOperation invokedOperation = soapOperationMatcher.getInvokedOperation(message);
            if (soapOperationMatcher.isRequestResponseOperation(invokedOperation)) {
                OperationWrapper operation = SoapBuilder.getOperation(binding, invokedOperation, message.getSoapAction());
                return respond(operation, message);
            }
            return null;
        } catch (OperationNotFoundException e) {
            throw new SoapServerException(e);
        }
    }

    /**
     * Abstract method that should be implemented by overriding classes.
     * This method is invoked whenever a request is send by the client.
     * InvokedOperation may be passed to a SoapBuilder to construct the
     * response to the request that was sent by the client.
     *
     * @param invokedOperation operation from the binding that is matched to the SOAP message
     * @param message          SOAP message passed by the client
     * @return response in the XML source format containing the whole SOAP envelope
     */
    public abstract Source respond(OperationWrapper invokedOperation, SoapMessage message);

}
