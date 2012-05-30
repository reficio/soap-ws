package com.centeractive.ws.builder.soap.domain;

import javax.xml.namespace.QName;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 16/11/11
 * Time: 2:49 PM
 */
public class OperationWrapper {

    private final QName bindingName;
    private final String operationName;
    private final String operationInputName;
    private final String operationOutputName;
    private final String soapAction;

    public OperationWrapper(QName bindingName, String operationName, String operationInputName, String operationOutputName, String soapAction) {
        this.bindingName = bindingName;
        this.operationName = operationName;
        this.operationInputName = operationInputName;
        this.operationOutputName = operationOutputName;
        this.soapAction = soapAction;
    }

    public QName getBindingName() {
        return bindingName;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getOperationInputName() {
        return operationInputName;
    }

    public String getOperationOutputName() {
        return operationOutputName;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public String toString() {
        return String.format("bindingName=[%s] operationName=[%s] operationInputName=[%s] operationOutputName=[%s] soapAction=[%s]",
                bindingName.toString(), operationName, operationInputName, operationOutputName, soapAction);
    }

}
