package com.centeractive.ws.client.ex;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 22/11/11
 * Time: 7:53 PM
 */
public class SoapBuilderException extends SoapException {

    public SoapBuilderException() {
    }

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
