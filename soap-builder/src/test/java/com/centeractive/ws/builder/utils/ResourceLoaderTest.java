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
package com.centeractive.ws.builder.utils;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class ResourceLoaderTest {

    private final static Logger log = Logger.getLogger(ResourceLoaderTest.class);

    @Test
    public void loadWithSystemClassloaderTest() {

        URL url5 = System.class.getResource("/com/centeractive/soap/test/soapEncoding.xsd");
        assertNotNull(url5);

        InputStream in = getClass().getResourceAsStream("/com/centeractive/soap/test/soapEncoding.xsd");
        assertNotNull(in);

        InputStream in2 = getClass().getClassLoader().getResourceAsStream("com/centeractive/soap/test/soapEncoding.xsd");
        assertNotNull(in2);

        URL url = getClass().getResource("/com/centeractive/soap/test/soapEncoding.xsd");
        assertNotNull(url);

        URL url2 = getClass().getClassLoader().getResource("com/centeractive/soap/test/soapEncoding.xsd");
        assertNotNull(url2);

        InputStream stream = ResourceLoaderTest.class.getResourceAsStream("/com/centeractive/soap/test/soapEncoding.xsd");
        assertNotNull(stream);

        InputStream stream2 = ResourceLoaderTest.class.getClassLoader().getResourceAsStream("com/centeractive/soap/test/soapEncoding.xsd");
        assertNotNull(stream2);

        URL url3 = ResourceLoaderTest.class.getResource("/com/centeractive/soap/test/soapEncoding.xsd");
        assertNotNull(url3);

        URL url4 = ResourceLoaderTest.class.getClassLoader().getResource("com/centeractive/soap/test/soapEncoding.xsd");
        assertNotNull(url4);

    }

}
