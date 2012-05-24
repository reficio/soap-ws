package com.centeractive.ws.server.responder;

import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.Source;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 16/11/11
 * Time: 1:17 PM
 */
public interface RequestResponder {

    /**
     * How to get full SOAP message envelope (envelope = header + body):
     * msg.getEnvelope().getSource()
     * <p/>
     * How to get header section:
     * msg.getEnvelope().getHeader().getSource()
     * <p/>
     * How to get body section:
     * msg.getEnvelope().getBody().getSource()
     * <p/>
     * How to convert XML String to XML Source:
     * XmlUtils.xmlStringToSource(string);
     * <p/>
     * How to convert XML Source to XML String:
     * XmlUtils.sourceToXmlString(source);
     */
    Source respond(SoapMessage request);

}
