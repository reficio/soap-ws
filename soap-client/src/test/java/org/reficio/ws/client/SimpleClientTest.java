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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reficio.ws.client.core.SoapClient;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SimpleClientTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 5000)
    public void connectTimeout() {

        exception.expect(TransmissionException.class);
        exception.expectMessage("Connection timed out");

        SoapClient client = SoapClient.builder()
                .endpointUri("http://test.ch:9999")
                .connectTimeoutInMillis(1000)
                .build();
        client.post("<xml/>");
    }

}
