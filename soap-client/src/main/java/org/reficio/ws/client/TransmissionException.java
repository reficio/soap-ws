/**
 * Copyright (c) 2012 Reficio (TM) - Reestablish your software!. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.reficio.ws.client;

/**
 * Exception thrown when an transmission error occurs. Returns HTTP error code if returned by the server.
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class TransmissionException extends SoapClientException {

    private final String errorResponse;
    private final Integer errorCode;

    public TransmissionException(String message) {
        super(message);
        this.errorResponse = null;
        this.errorCode = null;
    }

    public TransmissionException(String message, Throwable ex) {
        super(message, ex);
        this.errorResponse = null;
        this.errorCode = null;
    }

    public TransmissionException(String response, Integer errorCode) {
        super(String.format("HTTP response=[%s] code=[%d]", response, errorCode));
        this.errorResponse = response;
        this.errorCode = errorCode;
    }

    public String getErrorResponse() {
        return errorResponse;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
