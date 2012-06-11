/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.centeractive.ws.builder;

import com.centeractive.ws.builder.core.SoapBuilder;
import com.centeractive.ws.builder.soap.protocol.SoapVersion;
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
        String emptyFaultSoap11 = SoapBuilder.buildEmptyFault(SoapVersion.Soap11);
        log.info("\n"+emptyFaultSoap11);
        String expectedMsg = getContent("messages", "EmptyFault11.xml");
        assertEquals(expectedMsg, emptyFaultSoap11);
    }

    @Test
    public void testEmptyFaultSoap12() {
        String emptyFaultSoap12 = SoapBuilder.buildEmptyFault(SoapVersion.Soap12);
        log.info("\n"+emptyFaultSoap12);
        String expectedMsg = getContent("messages", "EmptyFault12.xml");
        assertEquals(expectedMsg, emptyFaultSoap12);
    }

    @Test
    public void testFaultSoap11() {
        String faultSoap11 = SoapBuilder.buildFault("VersionMismatch", "Fault Message", SoapVersion.Soap11);
        log.info("\n"+faultSoap11);
        String expectedMsg = getContent("messages", "FaultVersionMismatch11.xml");
        assertEquals(expectedMsg, faultSoap11);
    }

    @Test
    public void testFaultSoap12() {
        String faultSoap12 = SoapBuilder.buildFault("VersionMismatch", "Fault Message", SoapVersion.Soap12);
        log.info("\n"+faultSoap12);
        String expectedMsg = getContent("messages", "FaultVersionMismatch12.xml");
        assertEquals(expectedMsg, faultSoap12);
    }

    @Test
    public void testEmptyMessageSoap11() {
        String emptyMessageSoap11 = SoapBuilder.buildEmptyMessage(SoapVersion.Soap11);
        log.info("\n"+emptyMessageSoap11);
        String expectedMsg = getContent("messages", "EmptyMessage11.xml");
        assertEquals(expectedMsg, emptyMessageSoap11);
    }

    @Test
    public void testEmptyMessageSoap12() {
        String emptyMessageSoap12 = SoapBuilder.buildEmptyMessage(SoapVersion.Soap12);
        log.info("\n"+emptyMessageSoap12);
        String expectedMsg = getContent("messages", "EmptyMessage12.xml");
        assertEquals(expectedMsg, emptyMessageSoap12);
    }

}
