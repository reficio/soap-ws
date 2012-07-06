package com.centeractive.ws.builder.core;

import com.centeractive.ws.builder.soap.SoapContext;

import javax.wsdl.Binding;
import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public interface SoapBuilder {

    List<SoapOperation> getOperations();

    String buildInputMessage(SoapOperation operation);

    String buildInputMessage(SoapOperation operation, SoapContext context);

    String buildOutputMessage(SoapOperation operation);

    String buildOutputMessage(SoapOperation operation, SoapContext context);

    String buildFault(String code, String message);

    String buildEmptyFault();

    String buildEmptyMessage();

    QName getBindingName();

    Binding getBinding();

}
