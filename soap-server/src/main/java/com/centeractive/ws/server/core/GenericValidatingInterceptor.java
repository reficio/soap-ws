package com.centeractive.ws.server.core;

import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 14/11/11
 * Time: 8:23 PM
 */
public class GenericValidatingInterceptor extends PayloadValidatingInterceptor {

//    private static class MessageHoldingFakeContext implements MessageContext {
//        private WebServiceMessage request;
//        private WebServiceMessage response;
//        // private MessageContext context;
//
//        public MessageHoldingFakeContext(WebServiceMessage request, WebServiceMessage response) {
//            this.request = request;
//            this.response = response;
//            // this.context = context;
//        }
//
//        @Override
//        public WebServiceMessage getRequest() {
//            return this.request;
//        }
//
//        @Override
//        public boolean hasResponse() {
//            return response != null;//return context.hasResponse();
//        }
//
//        @Override
//        public WebServiceMessage getResponse() {
//            return this.response;
//        }
//
//        @Override
//        public void setResponse(WebServiceMessage response) {
//            throw new NotImplementedException();
//        }
//
//        @Override
//        public void clearResponse() {
//            throw new NotImplementedException();
//        }
//
//        @Override
//        public void readResponse(InputStream inputStream) throws IOException {
//            throw new NotImplementedException();
//        }
//
//        @Override
//        public void setProperty(String name, Object value) {
//            throw new NotImplementedException();
//        }
//
//        @Override
//        public Object getProperty(String name) {
//            throw new NotImplementedException();
//        }
//
//        @Override
//        public void removeProperty(String name) {
//            throw new NotImplementedException();
//        }
//
//        @Override
//        public boolean containsProperty(String name) {
//            throw new NotImplementedException();
//        }
//
//        @Override
//        public String[] getPropertyNames() {
//            throw new NotImplementedException();
//        }
//    }
//
//    @Override
//    public boolean handleRequest(MessageContext messageContext, Object endpoint)
//            throws IOException, SAXException, TransformerException {
//
//        MessageHoldingFakeContext fakeContext = new MessageHoldingFakeContext(messageContext.getRequest(),
//                messageContext.getResponse());
//        boolean result = super.handleRequest(messageContext, endpoint);
//
//
//        if(result == false) {
//
//        }
//        return result;
//    }
//
//    /**
//     * Validates the response message in the given message context. Validation only occurs if
//     * <code>validateResponse</code> is set to <code>true</code>, which is <strong>not</strong> the default.
//     * <p/>
//     * Returns <code>true</code> if the request is valid, or <code>false</code> if it isn't.
//     *
//     * @param messageContext the message context.
//     * @return <code>true</code> if the response is valid; <code>false</code> otherwise
//     * @see #setValidateResponse(boolean)
//     */
//    @Override
//    public boolean handleResponse(MessageContext messageContext, Object endpoint) throws IOException, SAXException {
//        Source responseSource = getValidationResponseSource(messageContext.getResponse());
//
//    }
//

}
