package com.centeractive.soap.domain;

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

    public OperationWrapper(QName bindingName, String operationName, String operationInputName, String operationOutputName) {
        this.bindingName = bindingName;
        this.operationName = operationName;
        this.operationInputName = operationInputName;
        this.operationOutputName = operationOutputName;
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

}
