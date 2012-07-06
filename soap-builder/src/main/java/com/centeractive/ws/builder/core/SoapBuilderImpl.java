package com.centeractive.ws.builder.core;

import com.centeractive.ws.builder.SoapBuilderException;
import com.centeractive.ws.builder.soap.SoapContext;
import com.centeractive.ws.builder.soap.SoapMessageBuilder;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
class SoapBuilderImpl implements SoapBuilder {

    private final SoapMessageBuilder builder;
    private final Binding binding;
    private final SoapContext context;

    SoapBuilderImpl(SoapMessageBuilder builder, Binding binding, SoapContext context) {
        this.builder = builder;
        this.binding = binding;
        this.context = context;
    }

    public BindingOperation getBindingOperation(SoapOperation op) {
        BindingOperation operation = binding.getBindingOperation(op.getOperationName(),
                op.getOperationInputName(), op.getOperationOutputName());
        if (operation == null) {
            throw new SoapBuilderException("Operation not found");
        }
        return operation;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SoapOperation> getOperations() {
        List<SoapOperation> operationNames = new ArrayList<SoapOperation>();
        for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
            operationNames.add(SoapUtils.getOperation(binding, operation));
        }
        return operationNames;

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
