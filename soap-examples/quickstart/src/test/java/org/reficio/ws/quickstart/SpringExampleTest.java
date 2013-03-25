/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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
package org.reficio.ws.quickstart;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reficio.ws.SoapContext;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.server.core.SoapServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringExampleTest {

    private final static Logger log = Logger.getLogger(SpringExampleTest.class);

    @Autowired
    private SoapBuilder builder;

    @Autowired
    private SoapClient client;

    @Autowired
    private SoapServer server;

    @Test
    public void testInjection() {
        assertNotNull(builder);
        assertNotNull(server);
    }

    @Test
    public void testServerStarted() {
        assertTrue(server.isRunning());
    }

    @Test
    public void testRequestResponse() {
        SoapOperation operation = builder.operation().name("ConversionRate").find();
        SoapContext context = SoapContext.builder().exampleContent(true).build();
        String request = builder.buildInputMessage(operation, context);

        String response = client.post(request);
        assertNotNull(response);
    }

}
