package com.centeractive.ws.server.core;

import com.centeractive.soap.XmlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.adapter.PayloadEndpointAdapter;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.Source;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 18/10/11
 * Time: 11:27 AM
 */
public class ContextEndpointAdapter extends PayloadEndpointAdapter {

    private final static Log log = LogFactory.getLog(ContextEndpointAdapter.class);

    public void invoke(MessageContext messageContext, Object endpoint) throws Exception {
        ContextPayloadEndpoint payloadEndpoint = (ContextPayloadEndpoint) endpoint;
        Source requestSource = messageContext.getRequest().getPayloadSource();
        Source responseSource = payloadEndpoint.invoke(requestSource, messageContext);
        GenericSoapMessage message = new GenericSoapMessage(responseSource);
        messageContext.setResponse(message);
    }

}
