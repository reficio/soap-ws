package com.centeractive.ws.builder.core;

import com.centeractive.ws.builder.soap.SoapMessageBuilder;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.OperationType;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapUtils {

    public static SoapOperation getOperation(Binding binding, BindingOperation operation) {
        String soapAction = SoapMessageBuilder.getSOAPActionUri(operation);
        return getOperation(binding, operation, soapAction);
    }

    public static SoapOperation getOperation(Binding binding, BindingOperation operation, String soapAction) {
        if (operation.getOperation().getStyle().equals(OperationType.REQUEST_RESPONSE)) {
            return new SoapOperation(binding.getQName(), operation.getName(), operation.getBindingInput().getName(),
                    operation.getBindingOutput().getName(), SoapMessageBuilder.normalizeSoapAction(soapAction));
        } else {
            return new SoapOperation(binding.getQName(), operation.getName(), operation.getBindingInput().getName(),
                    null, SoapMessageBuilder.normalizeSoapAction(soapAction));
        }
    }

}
