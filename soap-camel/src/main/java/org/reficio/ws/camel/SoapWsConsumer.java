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

import javax.xml.transform.Source;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultExchange;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.common.ResourceUtils;
import org.reficio.ws.server.SoapServerException;
import org.reficio.ws.server.core.SoapServer;
import org.reficio.ws.server.responder.AbstractResponder;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;

/**
 * @author piotr.jagielski
 */
public class SoapWsConsumer extends DefaultConsumer {

    protected static final String OPERATION = "operation";
    protected final SoapBuilder builder;
    protected final SoapWsConfiguration configuration;
    protected SoapServer server;

    public SoapWsConsumer(Endpoint endpoint, SoapWsConfiguration configuration, Processor processor) {
        super(endpoint, processor);
        this.configuration = configuration;
        builder = Wsdl.parse(ResourceUtils.getResource(configuration.getWsdl()).toString())
                    .binding().localPart(configuration.getLocalPart()).find();
    }

    @Override
    protected void doStart() throws Exception {
        server = SoapServer.builder()
          .httpPort(configuration.getPort())
          .build();
        server.start();
        server.registerRequestResponder(configuration.getContext(), new CamelResponder(builder));
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        server.stop();
        super.doStop();
    }

    private final class CamelResponder extends AbstractResponder {

        private final SoapEnvelopeWrapper envelopeWrapper;

        private CamelResponder(SoapBuilder builder) {
            super(builder);
            this.envelopeWrapper = new SoapEnvelopeWrapper(builder);
        }

        @Override
        public Source respond(SoapOperation invokedOperation, SoapMessage message) {
            Exchange exchange = new DefaultExchange(getCamelContext(), ExchangePattern.InOut);

            Document messageDocument = message.getDocument();
            String requestBody = envelopeWrapper.unwrap(messageDocument, getCamelContext());

            exchange.getIn().setBody(requestBody);
            exchange.getIn().setHeader(OPERATION, invokedOperation.getOperationName());

            try {
                getProcessor().process(exchange);
                if (exchange.getException() != null) {
                    throw exchange.getException();
                }
                Message responseMessage = exchange.getOut(Message.class);
                if (responseMessage != null) {
                    String responseBody = responseMessage.getBody(String.class);
                    return envelopeWrapper.wrapToSource(responseBody, getCamelContext());
                } else {
                    throw new IllegalArgumentException("Could not find output message, exchange: " + exchange);
                }
            } catch (Exception ex) {
                throw new SoapServerException(ex);
            }

        }

        private CamelContext getCamelContext() {
            return getEndpoint().getCamelContext();
        }

    }

}
