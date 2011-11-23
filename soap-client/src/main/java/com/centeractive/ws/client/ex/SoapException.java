package com.centeractive.ws.client.ex;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 22/11/11
 * Time: 7:53 PM
 */
public class SoapException extends RuntimeException {

    public SoapException() {
    }

    public SoapException(String message) {
        super(message);
    }

    public SoapException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoapException(Throwable cause) {
        super(cause);
    }
}
