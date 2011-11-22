package com.centeractive.ws.client;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 14/11/11
 * Time: 11:38 AM
 */
class HttpConstants {

    public final static String HTTP = "HTTP",
            HTTPS = "HTTPS";

    public final static String GET = "GET",
            POST = "POST",
            HEAD = "HEAD",
            PUT = "PUT",
            OPTIONS = "OPTIONS",
            DELETE = "DELETE";

    // These are copy-pasted from the Jetty codebase
    public final static int
            _100_CONTINUE = 100,
            _101_SWITCHING_PROTOCOLS = 101,
            _102_PROCESSING = 102,
            _200_OK = 200,
            _201_CREATED = 201,
            _202_ACCEPTED = 202,
            _203_NON_AUTHORITATIVE_INFORMATION = 203,
            _204_NO_CONTENT = 204,
            _205_RESET_CONTENT = 205,
            _206_PARTIAL_CONTENT = 206,
            _207_MULTI_STATUS = 207,
            _300_MULTIPLE_CHOICES = 300,
            _301_MOVED_PERMANENTLY = 301,
            _302_MOVED_TEMPORARILY = 302,
            _302_FOUND = 302,
            _303_SEE_OTHER = 303,
            _304_NOT_MODIFIED = 304,
            _305_USE_PROXY = 305,
            _400_BAD_REQUEST = 400,
            _401_UNAUTHORIZED = 401,
            _402_PAYMENT_REQUIRED = 402,
            _403_FORBIDDEN = 403,
            _404_NOT_FOUND = 404,
            _405_METHOD_NOT_ALLOWED = 405,
            _406_NOT_ACCEPTABLE = 406,
            _407_PROXY_AUTHENTICATION_REQUIRED = 407,
            _408_REQUEST_TIMEOUT = 408,
            _409_CONFLICT = 409,
            _410_GONE = 410,
            _411_LENGTH_REQUIRED = 411,
            _412_PRECONDITION_FAILED = 412,
            _413_REQUEST_ENTITY_TOO_LARGE = 413,
            _414_REQUEST_URI_TOO_LARGE = 414,
            _415_UNSUPPORTED_MEDIA_TYPE = 415,
            _416_REQUESTED_RANGE_NOT_SATISFIABLE = 416,
            _417_EXPECTATION_FAILED = 417,
            _422_UNPROCESSABLE_ENTITY = 422,
            _423_LOCKED = 423,
            _424_FAILED_DEPENDENCY = 424,
            _500_INTERNAL_SERVER_ERROR = 500,
            _501_NOT_IMPLEMENTED = 501,
            _502_BAD_GATEWAY = 502,
            _503_SERVICE_UNAVAILABLE = 503,
            _504_GATEWAY_TIMEOUT = 504,
            _505_HTTP_VERSION_NOT_SUPPORTED = 505,
            _507_INSUFFICIENT_STORAGE = 507,
            _999_UNKNOWN = 999;

    public final static String
            MIMETYPE_TEXT_HTML = "text/html",
            MIMETYPE_TEXT_PLAIN = "text/plain",
            MIMETYPE_TEXT_XML = "text/xml",
            MIMETYPE_TEXT_HTML_8859_1 = "text/html; charset=iso-8859-1",
            MIMETYPE_TEXT_PLAIN_8859_1 = "text/plain; charset=iso-8859-1",
            MIMETYPE_TEXT_XML_8859_1 = "text/xml; charset=iso-8859-1",
            MIMETYPE_TEXT_HTML_UTF_8 = "text/html; charset=utf-8",
            MIMETYPE_TEXT_PLAIN_UTF_8 = "text/plain; charset=utf-8",
            MIMETYPE_TEXT_XML_UTF_8 = "text/xml; charset=utf-8";
}
