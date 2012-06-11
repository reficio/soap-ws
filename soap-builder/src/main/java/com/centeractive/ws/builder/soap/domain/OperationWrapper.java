/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
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
