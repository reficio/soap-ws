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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class AutoResponder extends AbstractResponder {

    private final SoapContext context;

    public AutoResponder(SoapBuilder builder, QName bindingName) {
        super(builder, bindingName);
        context = SoapContext.builder().exampleContent(true).create();
    }

    public AutoResponder(SoapBuilder builder, QName bindingName, SoapContext context) {
        super(builder, bindingName);
        this.context = context;
    }

    @Override
    public Source respond(OperationWrapper invokedOperation, SoapMessage message) {
        try {
            String response = builder.buildSoapMessageFromOutput(invokedOperation, context);
            return XmlUtils.xmlStringToSource(response);
        } catch (Exception e) {
            throw new SoapServerException(e);
        }
    }

}
