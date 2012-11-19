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
package com.centeractive.ws.builder;

import com.centeractive.ws.SoapBuilderException;
import com.centeractive.ws.builder.core.WsdlParser;
import com.centeractive.ws.common.ResourceUtils;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class SoapOperationFinderImplTest {

    public SoapOperationFinder operation() {
        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("wsdl", "TestService.wsdl");
        WsdlParser parser = WsdlParser.parse(wsdlUrl);
        String binding = "{http://schemas.eviware.com/TestService/v1/}TestServiceSoap";
        return parser.binding(binding).builder().operation();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFinderNoArguments() {
        operation().find();
    }

    @Test(expected = NullPointerException.class)
    public void testFinderWrongArgument() {
        operation().name(null).find();
    }

    @Test(expected = SoapBuilderException.class)
    public void testFinderNoOperation() {
        operation().name("asd").find();
    }

    @Test
    public void testFindOk() {
        assertNotNull(operation().name("GetDefaultPageData").find());
    }

    @Test(expected = SoapBuilderException.class)
    public void testFindNameOkWrongAction() {
        operation().name("GetDefaultPageData").soapAction("asdasdasd").find();
    }

}
