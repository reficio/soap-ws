package com.centeractive.ws.client;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 23/11/11
 * Time: 9:13 AM
 */
class SoapConstants {

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
            SSL_CONTEXT = "SSLv3",
            TLS_CONTEXT = "TLS",
            JKS_KEYSTORE = "JKS";


}
