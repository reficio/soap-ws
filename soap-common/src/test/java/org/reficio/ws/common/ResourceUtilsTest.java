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

/**
 * User: Tom Bujok (tom.bujok@gmail.com)
 * Date: 11/10/11
 * Time: 11:16 AM
 */
public class ResourceUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testResourceLoading_failed() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "org/reficio/ws/common/test", "soapEncoding.xsd123123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResourceLoading_failed_noPackage() {
        URL url = ResourceUtils.getResource(System.class, "asdasdasdasd.txt");
    }

    @Test
    public void testResourceLoading_noLeadingTrailing() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "org/reficio/ws/common/test", "soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_noLeadingTrailing_noPackage() {
        URL url = ResourceUtils.getResource(System.class, "org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_noLeading() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "org/reficio/ws/common/test/", "soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_noLeading_noPackage() {
        URL url = ResourceUtils.getResource(System.class, "org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_multipleLeading() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "//////org/reficio/ws/common/test/", "soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_multipleLeading_noPackage() {
        URL url = ResourceUtils.getResource(System.class, "//////org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoadingNoTrailing() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "/org/reficio/ws/common/test", "soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoadingNoTrailing_noPackage() {
        URL url = ResourceUtils.getResource(System.class, "/org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_whiteSpaces() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "  /org/reficio/ws/common/test  ", "soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_whiteSpaces_noPackage() {
        URL url = ResourceUtils.getResource(System.class, "  /org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_multipleInner() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "/org///reficio////ws/common/////test", "soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_multipleInner_noPackage() {
        URL url = ResourceUtils.getResource(System.class, "/org///reficio////ws/common/////test/////soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_multipleTrailing() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "org/reficio/ws/common/test//////", "soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_multipleTrailing_noPackage() {
        URL url = ResourceUtils.getResource(System.class, "org/reficio/ws/common/test//////soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_notNormalized() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath(System.class, "org/../org/reficio/../reficio/ws/common/test/", "soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_notNormalized_noPackage() {
        URL url = ResourceUtils.getResource(System.class, "org/../org/reficio/../reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoading_notNormalized_asStream() {
        InputStream stream = ResourceUtils.getResourceWithAbsolutePackagePathAsStream(System.class, "/org/../org/reficio/../reficio/ws/common/test/", "soapEncoding.xsd");
        Assert.assertNotNull(stream);
    }

    @Test
    public void testResourceLoading_notNormalized_asStream_noPackage() {
        InputStream stream = ResourceUtils.getResourceAsStream(System.class, "/org/../org/reficio/../reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingObject() {
        InputStream stream = ResourceUtils.getResourceWithAbsolutePackagePathAsStream(Object.class, "/org/reficio/ws/common/test/", "soapEncoding.xsd");
        Assert.assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingObject_noPackage() {
        InputStream stream = ResourceUtils.getResourceAsStream(Object.class, "/org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingThis() {
        InputStream stream = ResourceUtils.getResourceWithAbsolutePackagePathAsStream(ResourceUtilsTest.class, "/org/reficio/ws/common/test/", "soapEncoding.xsd");
        Assert.assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingThis_noPackage() {
        InputStream stream = ResourceUtils.getResourceAsStream(ResourceUtilsTest.class, "/org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingGetClass() {
        InputStream stream = ResourceUtils.getResourceWithAbsolutePackagePathAsStream(getClass(), "/org/reficio/ws/common/test/", "soapEncoding.xsd");
        Assert.assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingGetClass_noPackage() {
        InputStream stream = ResourceUtils.getResourceAsStream(getClass(), "/org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingNoClass() {
        InputStream stream = ResourceUtils.getResourceWithAbsolutePackagePathAsStream("/org/reficio/ws/common/test/", "soapEncoding.xsd");
        Assert.assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingNoClass_noPackage() {
        InputStream stream = ResourceUtils.getResourceAsStream("/org/reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(stream);
    }

    @Test
    public void testResourceLoadingNotNormalizedNoClass() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath("org/../org/reficio/../reficio/ws/common/test/", "soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoadingNotNormalizedNoClass_noPackage() {
        URL url = ResourceUtils.getResource("org/../org/reficio/../reficio/ws/common/test/soapEncoding.xsd");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoadingSpaceInTheResource() {
        URL url = ResourceUtils.getResourceWithAbsolutePackagePath("my folder", "resource.txt");
        Assert.assertNotNull(url);
    }

    @Test
    public void testResourceLoadingSpaceInTheResource_noPackage() {
        URL url = ResourceUtils.getResource("my folder/resource.txt");
        Assert.assertNotNull(url);
    }


}
