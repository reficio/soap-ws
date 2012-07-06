package com.centeractive.ws.builder.core;

import com.centeractive.ws.builder.soap.SoapMessageBuilder;
import com.centeractive.ws.builder.soap.protocol.SoapVersion;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.OperationType;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import java.util.List;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapUtils {

    public static enum Soap { SOAP_1_1, SOAP_1_2 }

    public static SoapOperation getOperation(Binding binding, BindingOperation operation) {
        String soapAction = getSOAPActionUri(operation);
        return getOperation(binding, operation, soapAction);
    }

    public static SoapOperation getOperation(Binding binding, BindingOperation operation, String soapAction) {
        if (operation.getOperation().getStyle().equals(OperationType.REQUEST_RESPONSE)) {
            return new SoapOperation(binding.getQName(), operation.getName(), operation.getBindingInput().getName(),
                    operation.getBindingOutput().getName(), normalizeSoapAction(soapAction));
        } else {
            return new SoapOperation(binding.getQName(), operation.getName(), operation.getBindingInput().getName(),
                    null, normalizeSoapAction(soapAction));
        }
    }

    // removes "" from soap action
    public static String normalizeSoapAction(String soapAction) {
        String normalizedSoapAction = "";
        if (soapAction != null && soapAction.length() > 0) {
            normalizedSoapAction = soapAction;
            if (soapAction.charAt(0) == '"' && soapAction.charAt(soapAction.length() - 1) == '"') {
                normalizedSoapAction = soapAction.substring(1, soapAction.length() - 1).trim();
            }
        }
        return normalizedSoapAction;
    }

    public static String getSOAPActionUri(BindingOperation operation) {
        List extensions = operation.getExtensibilityElements();
        if (extensions != null) {
            for (int i = 0; i < extensions.size(); i++) {
                ExtensibilityElement extElement = (ExtensibilityElement) extensions.get(i);
                if (extElement instanceof SOAPOperation) {
                    SOAPOperation soapOp = (SOAPOperation) extElement;
                    return soapOp.getSoapActionURI();
                } else if (extElement instanceof SOAP12Operation) {
                    SOAP12Operation soapOp = (SOAP12Operation) extElement;
                    return soapOp.getSoapActionURI();
                }
            }
        }
        return null;
    }

    private static SoapVersion transformSoapVersion(Soap soapVersion) {
        if(soapVersion.equals(Soap.SOAP_1_1)) {
            return SoapVersion.Soap11;
        } else {
            return SoapVersion.Soap12;
        }
    }

    public static String buildEmptyMessage(Soap version) {
        return SoapMessageBuilder.buildEmptyMessage(transformSoapVersion(version));
    }

    public static String buildEmptyFault(Soap version) {
        return SoapMessageBuilder.buildEmptyFault(transformSoapVersion(version));
    }

    public static String buildFault(Soap version, String code, String message) {
        return SoapMessageBuilder.buildFault(code, message, transformSoapVersion(version));
    }

}
