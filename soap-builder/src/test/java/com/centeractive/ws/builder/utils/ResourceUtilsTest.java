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

import com.centeractive.ws.common.ResourceUtils;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 11/10/11
 * Time: 11:16 AM
 */
public class ResourceUtilsTest {

    @Test
    public void testResourceLoadingNoLeadingTrailing() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "com/centeractive/soap/test", "soapEncoding.xsd");
        assertNotNull(url);
    }

    @Test
    public void testResourceLoadingNoLeading() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "com/centeractive/soap/test/", "soapEncoding.xsd");
        assertNotNull(url);
    }

    @Test
    public void testResourceLoadingMultipleLeading() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "//////com/centeractive/soap/test/", "soapEncoding.xsd");
        assertNotNull(url);
    }

    @Test
    public void testResourceLoadingNoTrailing() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "/com/centeractive/soap/test", "soapEncoding.xsd");
        assertNotNull(url);
    }

    @Test
    public void testResourceLoadingWhiteSpaces() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "  /com/centeractive/soap/test  ", "soapEncoding.xsd");
        assertNotNull(url);
    }

    @Test
    public void testResourceLoadingMultipleInner() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "/com///centeractive////soap/////test", "soapEncoding.xsd");
        assertNotNull(url);
    }

    @Test
    public void testResourceLoadingMultipleTrailing() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "com/centeractive/soap/test//////", "soapEncoding.xsd");
        assertNotNull(url);
    }

    @Test
    public void testResourceLoadingNotNormalized() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "com/../com/centeractive/../centeractive/soap/test/", "soapEncoding.xsd");
        assertNotNull(url);
    }

    @Test
    public void testResourceLoadingNotNormalizedAsStream() {
        InputStream stream = ResourceUtils.getResourceWithAbsolutePackagePathAsStream(System.class, "/com/../com/centeractive/../centeractive/soap/test/", "soapEncoding.xsd");
        assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingObject() {
        InputStream stream = ResourceUtils.getResourceWithAbsolutePackagePathAsStream(Object.class, "/com/centeractive/soap/test/", "soapEncoding.xsd");
        assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingThis() {
        InputStream stream = ResourceUtils.getResourceWithAbsolutePackagePathAsStream(ResourceUtilsTest.class, "/com/centeractive/soap/test/", "soapEncoding.xsd");
        assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingGetClass() {
        InputStream stream = ResourceUtils.getResourceWithAbsolutePackagePathAsStream(getClass(), "/com/centeractive/soap/test/", "soapEncoding.xsd");
        assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingNoClass() {
        InputStream stream = ResourceUtils.getResourceWithAbsolutePackagePathAsStream("/com/centeractive/soap/test/", "soapEncoding.xsd");
        assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingNotNormalizedNoClass() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath("com/../com/centeractive/../centeractive/soap/test/", "soapEncoding.xsd");
        assertNotNull(url);
    }

    @Test
    public void testResourceLoadingSpaceInTheResource() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath("my folder", "resource.txt");
        assertNotNull(url);
    }


}
