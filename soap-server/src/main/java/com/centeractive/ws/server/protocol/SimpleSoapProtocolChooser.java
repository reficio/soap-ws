package com.centeractive.ws.server.protocol;

import org.springframework.ws.transport.TransportInputStream;

import java.io.IOException;
import java.util.Iterator;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 21/11/11
 * Time: 2:43 PM
 */
public class SimpleSoapProtocolChooser implements SoapProtocolChooser {

    public boolean useSoap11(TransportInputStream transportInputStream) throws IOException {
        for (Iterator headerNames = transportInputStream.getHeaderNames(); headerNames.hasNext(); ) {
            String headerName = (String) headerNames.next();
            for (Iterator headerValues = transportInputStream.getHeaders(headerName); headerValues.hasNext(); ) {
                String headerValue = (String) headerValues.next();
                if (headerName.toLowerCase().contains("content-type")) {
                    if (headerValue.trim().toLowerCase().contains("text/xml")) {
                        return true;
                    }

                }
            }
        }
        return false;
    }
    public boolean useSoap12(TransportInputStream transportInputStream) throws IOException {
        return useSoap11(transportInputStream) == false;
    }
}

