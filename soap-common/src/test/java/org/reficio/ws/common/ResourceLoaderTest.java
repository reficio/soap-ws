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
package org.reficio.ws.common;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

public class ResourceLoaderTest {

    @Test
    public void loadWithSystemClassloaderTest() {

        URL url5 = System.class.getResource("/org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url5);

        InputStream in = getClass().getResourceAsStream("/org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(in);

        InputStream in2 = getClass().getClassLoader().getResourceAsStream("org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(in2);

        URL url = getClass().getResource("/org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url);

        URL url2 = getClass().getClassLoader().getResource("org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url2);

        InputStream stream = ResourceLoaderTest.class.getResourceAsStream("/org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(stream);

        InputStream stream2 = ResourceLoaderTest.class.getClassLoader().getResourceAsStream("org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(stream2);

        URL url3 = ResourceLoaderTest.class.getResource("/org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url3);

        URL url4 = ResourceLoaderTest.class.getClassLoader().getResource("org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url4);

    }

}
