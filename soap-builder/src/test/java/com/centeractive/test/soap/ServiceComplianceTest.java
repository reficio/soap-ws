package com.centeractive.test.soap;

import com.centeractive.SoapBuilder;
import com.centeractive.test.utils.XmlTestUtils;
import com.centeractive.utils.ResourceUtils;
import com.ibm.wsdl.xml.WSDLReaderImpl;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 12/10/11
 * Time: 12:23 PM
 */
public class ServiceComplianceTest {

    private final static Logger log = Logger.getLogger(ServiceComplianceTest.class);

    enum MessageType {REQUEST, RESPONSE}

    public static String getTestServiceFolderPath(int testServiceId) {
        String testServiceIdString = (testServiceId < 10) ? "0" + testServiceId : "" + testServiceId;
        return "/services/test" + testServiceIdString;
    }

    public static URL getDefinitionUrl(int testServiceId) {
        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath(
                getTestServiceFolderPath(testServiceId), "TestService.wsdl");
        return wsdlUrl;
    }

    public static Definition getDefinition(URL wsdlUrl) throws WSDLException {
        WSDLReader reader = new WSDLReaderImpl();
        Definition definition = reader.readWSDL(wsdlUrl.getPath());
        return definition;
    }

    public static String getExpectedMessage(int testServiceId, String bindingName, String operationName, MessageType msg) {
        String serviceFolderPath = getTestServiceFolderPath(testServiceId);
        String messageFolderPath = String.format("%s/operations/%s", serviceFolderPath, bindingName);
        String fileName = operationName + "." + (MessageType.REQUEST.equals(msg) ? "request" : "response") + ".xml";
        URL fileUrl = ResourceUtils.getResourceWithAbsolutePackagePath(messageFolderPath, fileName);
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

    public static String getExpectedRequest(int testServiceId, String bindingName, String operationName) {
        return getExpectedMessage(testServiceId, bindingName, operationName, MessageType.REQUEST);
    }

    public static String getExpectedResponse(int testServiceId, String bindingName, String operationName) {
        return getExpectedMessage(testServiceId, bindingName, operationName, MessageType.RESPONSE);
    }


    private static void testService(int testServiceId) throws Exception {
        URL wsdlUrl = getDefinitionUrl(testServiceId);
        SoapBuilder builder = new SoapBuilder(wsdlUrl);

        for (Binding binding : (Collection<Binding>) builder.getDefinition().getAllBindings().values()) {
            String bindingName =  binding.getQName().getLocalPart();
            for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
                String request = builder.buildSoapMessageFromInput(builder.getOperation(binding, operation));
                String expectedRequest = getExpectedRequest(testServiceId, bindingName, operation.getName());
                log.info(String.format("Comparing binding=[%s] operation=[%s]", bindingName, operation.getName()));
                log.info("REQUEST:\n" + request);
                log.info("EXPECTED_REQUEST:\n" + expectedRequest);

                request = XmlTestUtils.normalizeAndRemoveValues(request);
                expectedRequest = XmlTestUtils.normalizeAndRemoveValues(expectedRequest);
                log.info("REQUEST_NO_VALUES:\n" + request);
                log.info("EXPECTED_REQUEST_NO_VALUES:\n" + expectedRequest);
                assertTrue(XMLUnit.compareXML(expectedRequest, request).identical());

                String response = builder.buildSoapMessageFromOutput(builder.getOperation(binding, operation));
                String expectedResponse = getExpectedResponse(testServiceId, bindingName, operation.getName());
                log.info("RESPONSE:\n" + response);
                log.info("EXPECTED_RESPONSE:\n" + expectedResponse);

                response = XmlTestUtils.normalizeAndRemoveValues(response);
                expectedResponse = XmlTestUtils.normalizeAndRemoveValues(expectedResponse);

                log.info("RESPONSE_NO_VALUES:\n" + response);
                log.info("EXPECTED_RESPONSE_NO_VALUES:\n" + expectedResponse);
                assertTrue(XMLUnit.compareXML(expectedResponse, response).identical());
            }
        }
    }

    @Test
    public void testService01() throws Exception {
        testService(1);
    }

    @Test
    public void testService02() throws Exception {
        testService(2);
    }

    @Test
    public void testService03() throws Exception {
        testService(3);
    }

    @Test
    public void testService04() throws Exception {
        testService(4);
    }

    @Test
    public void testService05() throws Exception {
        testService(5);
    }

    @Test
    public void testService06() throws Exception {
        testService(6);
    }

    @Test
    public void testService07() throws Exception {
        testService(7);
    }

    @Test
    public void testService08() throws Exception {
        testService(8);
    }

    @Test
    public void testService09() throws Exception {
        testService(9);
    }

    @Test
    public void testService10() throws Exception {
        testService(10);
    }

    @Test
    public void testService11() throws Exception {
        testService(11);
    }

    @Test
    public void testService12() throws Exception {
        testService(12);
    }

    @Test
    public void testService13() throws Exception {
        testService(13);
    }

    @Test
    public void testService14() throws Exception {
        testService(14);
    }

    @Test
    public void testService15() throws Exception {
        testService(15);
    }

    @Test
    public void testService16() throws Exception {
        testService(16);
    }

    @Test
    public void testService17() throws Exception {
        testService(17);
    }

    @Test
    public void testService18() throws Exception {
        testService(18);
    }

}
