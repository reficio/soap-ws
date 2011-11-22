package com.centeractive.ws.server.protocol;


import org.springframework.ws.transport.TransportInputStream;

import java.io.IOException;


/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 21/11/11
 * Time: 2:42 PM
 */
public interface SoapProtocolChooser {

    boolean useSoap11(TransportInputStream transportInputStream) throws IOException;
    boolean useSoap12(TransportInputStream transportInputStream) throws IOException;

}

