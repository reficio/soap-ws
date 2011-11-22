package com.centeractive.ws.server.core;

import com.centeractive.ws.server.ServiceRegistrationException;
import com.centeractive.ws.server.responder.RequestResponder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.AbstractDomPayloadEndpoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 18/10/11
 * Time: 11:27 AM
 */
@SuppressWarnings("deprecation")
public class GenericContextDomEndpoint extends AbstractDomPayloadEndpoint implements ContextPayloadEndpoint, InitializingBean {

    private final static Log log = LogFactory.getLog(GenericContextDomEndpoint.class);

    private ConcurrentHashMap<String, RequestResponder> services = new ConcurrentHashMap<String, RequestResponder>();

    @Override
    protected Element invokeInternal(Element requestElement, Document responseDocument) throws Exception {
        throw new RuntimeException("This method is not implemented - it SHOULD NOT be used.");
    }

    @Override
    public Source invoke(Source request, MessageContext messageContext) throws Exception {
        RequestResponder requestResponder = getRequestResponderBySessionRequestContextPath();
        if (noResponderForRequestFound(requestResponder)) {
            handleNoResponderFault(request);
        }
        Source response = requestResponder.respond(request);
        return response;
    }

    private RequestResponder getRequestResponderBySessionRequestContextPath() {
        HttpServletRequest htpServletRequest = getHttpServletRequest();
        return getRequestResponderByRequestContextPath(htpServletRequest.getRequestURI());
    }

    private RequestResponder getRequestResponderByRequestContextPath(String contextPath) {
        return services.get(contextPath);
    }

    private boolean noResponderForRequestFound(RequestResponder responder) {
        if (responder == null) {
            return true;
        }
        return false;
    }

    private Source handleNoResponderFault(Source request) {
        String msg = String.format("There is no service under the requested context path [%s]", getRequestContextPath());
        throw new RuntimeException(msg);
    }

    private HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private String getRequestContextPath() {
        return getHttpServletRequest().getRequestURI();
    }

    public void registerRequestResponder(String contextPath, RequestResponder responder) throws ServiceRegistrationException {
        if (services.putIfAbsent(contextPath, responder) != null) {
            throw new ServiceRegistrationException(String.format("Specified context path [%s] is already taken", contextPath));
        }
    }

    public void unregisterRequestResponder(String contextPath)  throws ServiceRegistrationException {
        if (services.remove(contextPath) == null) {
            throw new ServiceRegistrationException(String.format("There was no service under the specified context path [%s]", contextPath));
        }
    }

    public Enumeration<String> getRegisteredContextPaths() {
        return services.keys();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Generic SOAP endpoint initialized");
    }
}
