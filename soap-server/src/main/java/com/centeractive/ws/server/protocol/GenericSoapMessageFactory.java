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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.TransportInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class GenericSoapMessageFactory implements SoapMessageFactory, InitializingBean {
    private static final String REQUEST_CONTEXT_ATTRIBUTE = "GenericSoapMessageFactory";

    private static final Log log = LogFactory.getLog(GenericSoapMessageFactory.class);

    private SaajSoapMessageFactory soap11 = new SaajSoapMessageFactory();
    private SaajSoapMessageFactory soap12 = new SaajSoapMessageFactory();

    private SoapProtocolChooser soapProtocolChooser = new SimpleSoapProtocolChooser();

    private void setMessageFactoryForRequestContext(SaajSoapMessageFactory factory) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        attributes.setAttribute(REQUEST_CONTEXT_ATTRIBUTE, factory, RequestAttributes.SCOPE_REQUEST);
    }

    private SaajSoapMessageFactory getMessageFactoryForRequestContext() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (SaajSoapMessageFactory) attributes.getAttribute(REQUEST_CONTEXT_ATTRIBUTE,
                RequestAttributes.SCOPE_REQUEST);
    }

    public void setSoapVersion(SoapVersion version) {
        // ignore this, it will be set automatically
    }

    public void setSoapProtocolChooser(SoapProtocolChooser soapProtocolChooser) {
        this.soapProtocolChooser = soapProtocolChooser;
    }

    private void configureFactory(SaajSoapMessageFactory factory, SoapVersion version) {
        factory.setSoapVersion(version);
        factory.afterPropertiesSet();
    }

    public void afterPropertiesSet() throws Exception {
        configureFactory(soap11, SoapVersion.SOAP_11);
        configureFactory(soap12, SoapVersion.SOAP_12);
    }

    public SoapMessage createWebServiceMessage() {
        return getMessageFactoryForRequestContext().createWebServiceMessage();
    }

    public SoapMessage createWebServiceMessage(InputStream inputStream) throws IOException {
        setMessageFactoryForRequestContext(soap11);
        if (inputStream instanceof TransportInputStream) {
            TransportInputStream transportInputStream = (TransportInputStream) inputStream;
            if (soapProtocolChooser.useSoap12(transportInputStream)) {
                setMessageFactoryForRequestContext(soap12);
            }
        }
        SaajSoapMessageFactory mf = getMessageFactoryForRequestContext();
        return mf.createWebServiceMessage(inputStream);
    }

}

