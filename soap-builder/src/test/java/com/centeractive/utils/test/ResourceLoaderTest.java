package com.centeractive.utils.test;

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
