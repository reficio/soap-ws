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
package com.centeractive.ws.server.endpoint;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.PayloadEndpoint;

import javax.xml.transform.Source;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 14/11/11
 * Time: 3:14 PM
 */
public interface ContextPayloadEndpoint extends PayloadEndpoint {

    /**
     * Invokes the endpoint with the given request payload, and possibly returns a response.
     *
     * @param request the payload of the request message, may be <code>null</code>
     * @param messageContext
     * @return the payload of the response message, may be <code>null</code> to indicate no response
     * @throws Exception if an exception occurs
     */
    Source invoke(Source request, MessageContext messageContext) throws Exception;

}
