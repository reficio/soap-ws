package com.centeractive.ws.builder.utils;

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
