package com.centeractive.ws.server.core;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.PayloadEndpoint;

import javax.xml.transform.Source;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 14/11/11
 * Time: 3:14 PM
 */
public interface ContextPayloadEndpoint extends PayloadEndpoint {

    /**
     * Invokes the endpoint with the given request payload, and possibly returns a response.
     *
     * @param request the payload of the request message, may be <code>null</code>
     * @param messageContext
     * @return the payload of the response message, may be <code>null</code> to indicate no response
     * @throws Exception if an exception occurs
     */
    Source invoke(Source request, MessageContext messageContext) throws Exception;

}
