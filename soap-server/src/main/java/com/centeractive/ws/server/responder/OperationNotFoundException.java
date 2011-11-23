package com.centeractive.ws.server.responder;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 18/11/11
 * Time: 10:38 AM
 */
class OperationNotFoundException extends Exception {
    OperationNotFoundException(String message) {
        super(message);
    }

    OperationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
