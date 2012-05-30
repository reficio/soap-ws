package com.centeractive.ws.client.ex;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 22/11/11
 * Time: 8:11 PM
 */
public class SoapTransmissionException extends SoapException {

    private final String errorResponse;
    private final int errorCode;

    public SoapTransmissionException(Throwable cause) {
        super(cause);
        this.errorResponse = null;
        this.errorCode = -1;
    }

    public SoapTransmissionException(String response, int errorCode, Throwable cause) {
        super(response + " " +cause.getMessage(), cause);
        this.errorResponse = response;
        this.errorCode = errorCode;
    }

    public String getErrorResponse() {
        return errorResponse;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
