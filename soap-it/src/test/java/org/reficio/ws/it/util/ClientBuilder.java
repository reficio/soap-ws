package org.reficio.ws.it.util;

import org.reficio.ws.client.core.SoapClient;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
public interface ClientBuilder {
    SoapClient buildClient(String endpointUrl);
}
