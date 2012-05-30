package com.centeractive.ws.client.ex;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 * <p/>
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 22/11/11
 * Time: 7:53 PM
 */
public class SoapClientException extends RuntimeException {

    public SoapClientException(String message) {
        super(message);
    }

    public SoapClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoapClientException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
