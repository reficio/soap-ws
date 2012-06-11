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
