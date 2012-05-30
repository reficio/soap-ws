package com.centeractive.ws.server.util;

import com.centeractive.SoapBuilder;
import com.centeractive.SoapContext;
import com.centeractive.utils.ResourceUtils;
import com.centeractive.ws.server.SoapServer;
import com.centeractive.ws.server.responder.AutoResponder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.wsdl.Binding;
import javax.wsdl.WSDLException;
import java.net.URL;
import java.util.Collection;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 19/11/11
 * Time: 6:29 PM
 */
public class TestUtils {

    private final static Log log = LogFactory.getLog(TestUtils.class);

    public static SoapBuilder createBuilderForService(int testServiceId) throws WSDLException {
        String path = getTestServiceFolderPath(testServiceId);
        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath(path, "TestService.wsdl");
        SoapBuilder builder = new SoapBuilder(wsdlUrl);
        return builder;
    }

    public static String formatContextPath(int testServiceId, Binding binding) {
        return "/service" + formatServiceId(testServiceId) + "_" + binding.getQName().getLocalPart();
    }

    public static String getTestServiceFolderPath(int testServiceId) {
        String testServiceIdString = formatServiceId(testServiceId);
        return "/services/test" + testServiceIdString;
    }

    public static String formatServiceId(int testServiceId) {
        return (testServiceId < 10) ? "0" + testServiceId : "" + testServiceId;
    }

    public static void registerService(SoapServer server, int testServiceId) throws WSDLException {
        SoapBuilder builder = TestUtils.createBuilderForService(testServiceId);
        registerAutoResponderForAllServiceBindings(server, testServiceId, builder);
    }

    public static void registerService(SoapServer server, int testServiceId, SoapBuilder builder) throws WSDLException {
        registerAutoResponderForAllServiceBindings(server, testServiceId, builder);
    }

    public static void registerAutoResponderForAllServiceBindings(SoapServer server, int testServiceId, SoapBuilder builder) {
        for (Binding binding : (Collection<Binding>) builder.getDefinition().getAllBindings().values()) {
            String contextPath = TestUtils.formatContextPath(testServiceId, binding);
            log.info(String.format("Registering auto responder for service [%d] undex context path [%s]", testServiceId, contextPath));
            SoapContext context = SoapContext.builder().exampleContent(false).create();
            server.registerRequestResponder(contextPath, new AutoResponder(builder, binding.getQName(), context));
        }
    }

}
