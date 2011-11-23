package com.centeractive;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 23/11/11
 * Time: 9:39 AM
 */
public class SoapBuilderException extends RuntimeException {

    public SoapBuilderException(String message) {
        super(message);
    }

    public SoapBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoapBuilderException(Throwable cause) {
        super(cause);
    }
}
