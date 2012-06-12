/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
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
package com.centeractive.ws.client.core;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
final class SoapClientConstants {

    private SoapClientConstants() {
    }

    public final static String
            HTTP = "HTTP",
            HTTPS = "HTTPS";

    public final static String
            GET = "GET",
            POST = "POST",
            HEAD = "HEAD",
            PUT = "PUT",
            OPTIONS = "OPTIONS",
            DELETE = "DELETE";

    public final static String
            MIMETYPE_TEXT_HTML = "text/html",
            MIMETYPE_TEXT_PLAIN = "text/plain",
            MIMETYPE_TEXT_XML = "text/xml",
            MIMETYPE_APPLICATION_XML = "application/soap+xml";

    public final static String
            PROP_SOAP_ACTION_11 = "SOAPAction",
            PROP_SOAP_ACTION_12 = "action=",
            PROP_CONTENT_TYPE = "Content-Type",
            PROP_CONTENT_LENGTH = "Content-Length",
            PROP_AUTH = "Authorization",
            PROP_PROXY_AUTH = "Proxy-Authorization",
            PROP_BASIC_AUTH = "Basic",
            PROP_DELIMITER = "; ";


    public final static String
            SOAP_1_1_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/",
            SOAP_1_2_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";

    public final static String
            SSL_CONTEXT_PROTOCOL = "SSLv3",
            TLS_CONTEXT = "TLS",
            JKS_KEYSTORE = "JKS";

    public final static int
            INFINITE_TIMEOUT = 0;


}
