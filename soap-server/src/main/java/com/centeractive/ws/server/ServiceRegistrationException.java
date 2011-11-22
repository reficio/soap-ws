package com.centeractive.ws.server;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 18/11/11
 * Time: 10:06 AM
 */
public class ServiceRegistrationException extends RuntimeException {
    public ServiceRegistrationException(String message) {
        super(message);
    }

    public ServiceRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
