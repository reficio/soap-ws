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
package com.centeractive.ws.server.endpoint;

import com.centeractive.ws.server.ServiceRegistrationException;
import com.centeractive.ws.server.SoapServerException;
import com.centeractive.ws.server.responder.RequestResponder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.AbstractDomPayloadEndpoint;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
public class GenericContextDomEndpoint extends AbstractDomPayloadEndpoint implements ContextPayloadEndpoint, InitializingBean {

    private final static Log log = LogFactory.getLog(GenericContextDomEndpoint.class);

    private ConcurrentHashMap<String, RequestResponder> services = new ConcurrentHashMap<String, RequestResponder>();

    @Override
    protected Element invokeInternal(Element requestElement, Document responseDocument) throws Exception {
        throw new SoapServerException("This method is not implemented - it SHOULD NOT be used.");
    }

    @Override
    public Source invoke(Source request, MessageContext messageContext) throws Exception {
        RequestResponder requestResponder = getRequestResponderBySessionRequestContextPath();
        if (noResponderForRequestFound(requestResponder)) {
            handleNoResponderFault(request);
        }
        SoapMessage msg = (SoapMessage) messageContext.getRequest();
        Source response = requestResponder.respond(msg);
        return response;
    }

    private RequestResponder getRequestResponderBySessionRequestContextPath() {
        HttpServletRequest htpServletRequest = getHttpServletRequest();
        return getRequestResponderByRequestContextPath(htpServletRequest.getRequestURI());
    }

    private RequestResponder getRequestResponderByRequestContextPath(String contextPath) {
        return services.get(contextPath);
    }

    private boolean noResponderForRequestFound(RequestResponder responder) {
        if (responder == null) {
            return true;
        }
        return false;
    }

    private Source handleNoResponderFault(Source request) {
        String msg = String.format("There is no service under the requested context path [%s]", getRequestContextPath());
        throw new SoapServerException(msg);
    }

    private HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private String getRequestContextPath() {
        return getHttpServletRequest().getRequestURI();
    }

    public void registerRequestResponder(String contextPath, RequestResponder responder) throws ServiceRegistrationException {
        if (services.putIfAbsent(contextPath, responder) != null) {
            throw new ServiceRegistrationException(String.format("Specified context path [%s] is already taken", contextPath));
        }
    }

    public void unregisterRequestResponder(String contextPath)  throws ServiceRegistrationException {
        if (services.remove(contextPath) == null) {
            throw new ServiceRegistrationException(String.format("There was no service under the specified context path [%s]", contextPath));
        }
    }

    public Enumeration<String> getRegisteredContextPaths() {
        return services.keys();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Generic SOAP endpoint initialized");
    }
}
