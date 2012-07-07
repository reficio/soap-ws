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

import com.centeractive.ws.builder.core.SoapUtils;
import com.centeractive.ws.builder.utils.ResourceUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 17/10/11
 * Time: Soap11:06 AM
 */
public class MessageComplianceTest {

    private final static Logger log = Logger.getLogger(MessageComplianceTest.class);

    public static String getContent(String folderPath, String fileName) {
        URL fileUrl = ResourceUtils.getResourceWithAbsolutePackagePath(folderPath, fileName);
        File file = null;
        try {
            file = new File(fileUrl.toURI());
        } catch (URISyntaxException e) {
            file = new File(fileUrl.getPath());
        }
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEmptyFaultSoap11() {
        String emptyFaultSoap11 = SoapUtils.buildEmptyFault(SoapUtils.Soap.SOAP_1_1);
        log.info("\n" + emptyFaultSoap11);
        String expectedMsg = getContent("messages", "EmptyFault11.xml");
        assertEquals(expectedMsg, emptyFaultSoap11);
    }

    @Test
    public void testEmptyFaultSoap12() {
        String emptyFaultSoap12 = SoapUtils.buildEmptyFault(SoapUtils.Soap.SOAP_1_2);
        log.info("\n" + emptyFaultSoap12);
        String expectedMsg = getContent("messages", "EmptyFault12.xml");
        assertEquals(expectedMsg, emptyFaultSoap12);
    }

    @Test
    public void testFaultSoap11() {
        String faultSoap11 = SoapUtils.buildFault(SoapUtils.Soap.SOAP_1_1, "VersionMismatch", "Fault Message");
        log.info("\n" + faultSoap11);
        String expectedMsg = getContent("messages", "FaultVersionMismatch11.xml");
        assertEquals(expectedMsg, faultSoap11);
    }

    @Test
    public void testFaultSoap12() {
        String faultSoap12 = SoapUtils.buildFault(SoapUtils.Soap.SOAP_1_2, "VersionMismatch", "Fault Message");
        log.info("\n" + faultSoap12);
        String expectedMsg = getContent("messages", "FaultVersionMismatch12.xml");
        assertEquals(expectedMsg, faultSoap12);
    }

    @Test
    public void testEmptyMessageSoap11() {
        String emptyMessageSoap11 = SoapUtils.buildEmptyMessage(SoapUtils.Soap.SOAP_1_1);
        log.info("\n" + emptyMessageSoap11);
        String expectedMsg = getContent("messages", "EmptyMessage11.xml");
        assertEquals(expectedMsg, emptyMessageSoap11);
    }

    @Test
    public void testEmptyMessageSoap12() {
        String emptyMessageSoap12 = SoapUtils.buildEmptyMessage(SoapUtils.Soap.SOAP_1_2);
        log.info("\n" + emptyMessageSoap12);
        String expectedMsg = getContent("messages", "EmptyMessage12.xml");
        assertEquals(expectedMsg, emptyMessageSoap12);
    }

}
