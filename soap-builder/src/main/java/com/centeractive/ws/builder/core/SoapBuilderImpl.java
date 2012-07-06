package com.centeractive.ws.builder.core;

import com.centeractive.ws.builder.SoapBuilderException;
import com.centeractive.ws.builder.soap.SoapBuilderLegacy;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
class SoapBuilderImpl implements SoapBuilder {

    private final SoapBuilderLegacy builder;
    private final Binding binding;
    private final SoapContext context;

    SoapBuilderImpl(SoapBuilderLegacy builder, Binding binding, SoapContext context) {
        this.builder = builder;
        this.binding = binding;
        this.context = context;
    }

    private BindingOperation getBindingOperation(SoapOperation operation) {
        return builder.getBindingOperation(binding, operation);
    }

    @Override
    public List<SoapOperation> getOperations() {
        return builder.getOperationNames(binding);
    }

    @Override
    public String buildInputMessage(SoapOperation operation) {
        return buildInputMessage(operation, context);
    }

    @Override
    public String buildInputMessage(SoapOperation operation, SoapContext context) {
        try {
            return builder.buildSoapMessageFromInput(binding, getBindingOperation(operation), context);
        } catch (Exception e) {
            throw new SoapBuilderException(e);
        }
    }

    @Override
    public String buildOutputMessage(SoapOperation operation) {
        return buildOutputMessage(operation, context);
    }

    @Override
    public String buildOutputMessage(SoapOperation operation, SoapContext context) {
        try {
            return builder.buildSoapMessageFromOutput(binding, getBindingOperation(operation), context);
        } catch (Exception e) {
            throw new SoapBuilderException(e);
        }
    }

    @Override
    public String buildFault(String code, String message) {
        return builder.buildFault(code, message, binding);
    }

    @Override
    public String buildEmptyFault() {
        return builder.buildEmptyFault(binding);
    }

    @Override
    public String buildEmptyMessage() {
        return builder.buildEmptyMessage(binding);
    }

    @Override
    public QName getBindingName() {
        return binding.getQName();
    }

    @Override
    public Binding getBinding() {
        return binding;
    }
}
