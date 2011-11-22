package com.centeractive.ws.server.responder;

import javax.xml.transform.Source;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 16/11/11
 * Time: 1:17 PM
 */
public interface RequestResponder {

    public Source respond(Source request);

}
