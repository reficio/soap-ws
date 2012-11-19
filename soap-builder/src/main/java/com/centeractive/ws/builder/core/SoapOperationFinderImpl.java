/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.centeractive.ws.builder.core;

import com.centeractive.ws.SoapBuilderException;
import com.centeractive.ws.builder.SoapOperation;
import com.centeractive.ws.builder.SoapOperationFinder;
import com.google.common.base.Preconditions;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import java.util.ArrayList;
import java.util.List;

class SoapOperationFinderImpl implements SoapOperationFinder {

    private final Binding binding;

    private String operationName;
    private String operationInputName;
    private String operationOutputName;
    private String soapAction;

    SoapOperationFinderImpl(Binding binding) {
        this.binding = binding;
    }

    @Override
    public SoapOperationFinder name(String operationName) {
        Preconditions.checkNotNull(operationName);
        this.operationName = operationName;
        return this;
    }

    @Override
    public SoapOperationFinder soapAction(String soapAction) {
        Preconditions.checkNotNull(operationName);
        this.soapAction = soapAction;
        return this;
    }

    @Override
    public SoapOperationFinder inputName(String inputName) {
        Preconditions.checkNotNull(inputName);
        this.operationInputName = inputName;
        return this;
    }

    @Override
    public SoapOperationFinder outputName(String outputName) {
        Preconditions.checkNotNull(outputName);
        this.operationOutputName = outputName;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SoapOperation find() {
        validateInput();
        List<SoapOperation> found = new ArrayList<SoapOperation>();
        for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
            boolean condition = true;
            condition &= checkOperationName(operation);
            condition &= checkSoapAction(operation);
            condition &= checkOperationInputName(operation);
            condition &= checkOperationOutputName(operation);
            if(condition) {
                found.add(SoapOperation.create(binding, operation));
                if(found.size() > 1) {
                    throw new SoapBuilderException("Operation not unique - found more than one operation");
                }
            }
        }
        if(found.isEmpty()) {
            throw new SoapBuilderException("Found no operations");
        }
        return found.iterator().next();
    }

    private void validateInput() {
        boolean failed = true;
        failed &= this.operationName == null;
        failed &= this.soapAction == null;
        failed &= this.operationInputName == null;
        failed &= this.operationOutputName == null;
        if(failed) {
            throw new IllegalArgumentException("All finder properties cannot be null");
        }
    }

    private boolean checkOperationName(BindingOperation op) {
        if (this.operationName != null) {
            return this.operationName.equals(op.getOperation().getName());
        }
        return true;
    }

    private boolean checkSoapAction(BindingOperation op) {
        if (this.soapAction != null) {
            return this.soapAction.equals(SoapUtils.getSOAPActionUri(op));
        }
        return true;
    }

    private boolean checkOperationInputName(BindingOperation op) {
        if (this.operationInputName != null) {
            return this.operationInputName.equals(op.getOperation().getInput().getName());
        }
        return true;
    }

    private boolean checkOperationOutputName(BindingOperation op) {
        if (this.operationOutputName != null) {
            return this.operationOutputName.equals(op.getOperation().getOutput().getName());
        }
        return true;
    }

}
