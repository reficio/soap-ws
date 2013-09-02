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

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.common.ResourceUtils;

/**
 * @author piotr.jagielski
 */
public class SoapWsProducer extends DefaultProducer {

    protected final SoapClient client;
    protected final SoapBuilder builder;
    protected final SoapEnvelopeWrapper envelopeWrapper;

    public SoapWsProducer(SoapWsEndpoint endpoint, SoapWsConfiguration configuration) {
        super(endpoint);
        client = SoapClient.builder()
            .endpointUri(configuration.getUri())
            .build();
        builder = Wsdl.parse(ResourceUtils.getResource(configuration.getWsdl()).toString())
            .binding().localPart(configuration.getLocalPart()).find();
        envelopeWrapper = new SoapEnvelopeWrapper(builder);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String payload = exchange.getIn().getMandatoryBody(String.class);
        CamelContext context = exchange.getContext();

        String request = envelopeWrapper.wrap(payload, context);
        String response = client.post(request);

        exchange.getOut().setBody(envelopeWrapper.unwrap(response, context));
    }

}
