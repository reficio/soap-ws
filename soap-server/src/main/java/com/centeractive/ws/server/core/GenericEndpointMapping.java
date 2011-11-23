package com.centeractive.ws.server.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.mapping.AbstractEndpointMapping;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 18/10/11
 * Time: 11:26 AM
 */
public class GenericEndpointMapping extends AbstractEndpointMapping {

    private final static Log log = LogFactory.getLog(GenericEndpointMapping.class);

    private ContextPayloadEndpoint genericEndpoint;

    @Override
    protected Object getEndpointInternal(MessageContext messageContext) throws Exception {
        return genericEndpoint;
    }

    public ContextPayloadEndpoint getGenericEndpoint() {
        return genericEndpoint;
    }

    public void setGenericEndpoint(ContextPayloadEndpoint genericEndpoint) {
        this.genericEndpoint = genericEndpoint;
    }
}
