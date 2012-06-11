package com.centeractive.ws.server.core;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
class SoapServerConstants {
    public static final int HTTP_PORT = 8080;
    public static final int HTTPS_PORT = 8443;
    public static final int CONNECTION_MAX_IDLE_TIME_IN_SECONDS = 60;
    public static final int ACCEPTOR_THREADS_COUNT = 4;
    public static final int CORE_THREADS_COUNT = 8;
    public static final int MAX_THREADS_COUNT = 16;
    public static final int THREAD_KEEP_ALIVE_TIME_IN_SECONDS = 60;
    public static final String KEYSTORE_TYPE = "JKS";
    public static final boolean REUSE_ADDRESS = true;

    public static final String SPRING_CONTEXT_LOCATION = "classpath:soap-server.xml";
    public static final String SERVER_BEAN_NAME = "jettyServer";
    public static final String CONNECTOR_BEAN_NAME = "connector";
    public static final String SSL_CONNECTOR_BEAN_NAME = "sslConnector";
    public static final String ENDPOINT_BEAN_NAME = "endpoint";

    public static final String CORE_POOL_SIZE_PROP_KEY = "core.pool.size";
    public static final String MAX_POOL_SIZE_PROP_KEY = "max.pool.size";
    public static final String KEEP_ALIVE_PROP_KEY = "keep.alive.time";


}
