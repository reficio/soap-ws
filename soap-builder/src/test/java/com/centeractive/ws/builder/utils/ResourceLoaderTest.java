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
