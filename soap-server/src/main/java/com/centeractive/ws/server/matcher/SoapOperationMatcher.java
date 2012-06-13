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
package com.centeractive.ws.server.matcher;

import com.centeractive.ws.builder.core.SoapBuilder;
import com.centeractive.ws.builder.soap.WsdlUtils;
import com.centeractive.ws.server.OperationNotFoundException;
import com.centeractive.ws.server.util.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Node;

import javax.wsdl.*;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Matches SOAP message to the binding operation.
 * Tries to match a SOAP message to a binding operation using the following mechanisms:
 * - SOAP Action mapping
 * - RCP bindings are matched using single top-level tag with the name of the invoked operation
 * - Document bindings are matched by input types and then by input names
 * <p/>
 * Thanks to Spring SOAPAction in both SOAP versions is treated transparently.
 * <p/>
 * Resources about SOAP-Action mystery in SOAP 1.1:
 * http://ws-rx.blogspot.com/2006/01/web-services-design-tips-soapaction.html
 * http://www.w3.org/TR/2000/NOTE-SOAP-20000508/#_Toc478383528
 * http://www.oreillynet.com/xml/blog/2002/11/unraveling_the_mystery_of_soap.html
 * http://damithakumarage.wordpress.com/2008/02/12/soap-action-and-addressing-action/
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapOperationMatcher {

    protected final QName bindingName;
    protected final Binding binding;
    protected final boolean rpc;

    public SoapOperationMatcher(Definition definition, QName bindingName) {
        this.bindingName = bindingName;
        this.binding = definition.getBinding(bindingName);
        this.rpc = WsdlUtils.isRpc(binding);
    }

    /**
     * @return returns true if the binding is an RPC binding
     */
    protected boolean isRpc() {
        return rpc;
    }

    /**
     * @return returns true if the binding is an Document binding
     */
    protected boolean isDocument() {
        return isRpc() == false;
    }

    /**
     * Matches the SoapMessage to an binding operation
     * <p/>
     * Tries to match using the following mechanisms:
     * - SOAP Action mapping
     * - RCP bindings are matched using single top-level tag with the name of the invoked operation
     * - Document bindings are matched by input types and then by input names
     * <p/>
     *
     * @param message message passed by the SOAP client
     * @return the BindingOperation matched to the message
     * @throws com.centeractive.ws.server.OperationNotFoundException
     *          if operation not found in the binding
     */
    public BindingOperation getInvokedOperation(SoapMessage message) throws OperationNotFoundException {
        // SOAP action mapping - cheapest and fastest as no request analysis is required
        BindingOperation invokedOperation = getOperationBySoapAction(message);
        if (invokedOperation != null) {
            return invokedOperation;
        }
        Set<Node> rootNodes = XmlUtils.getRootNodes((DOMSource) message.getPayloadSource());
        if (isRpc()) {
            // rpc-type requests always contain single top-level tag with invoked operation
            invokedOperation = getOperationByRootQName(rootNodes);
            if (invokedOperation != null) {
                return invokedOperation;
            }
        } else {
            // match by types of input arguments - if two operation defined with the same argument types
            //  unable to distinguish
            invokedOperation = getOperationByInputTypes(rootNodes);
            if (invokedOperation != null) {
                return invokedOperation;
            }
            // malformed services - rare but possible - if two operation defined with the same argument names
            //  unable to distinguish
            invokedOperation = getOperationByInputNames(rootNodes);
            if (invokedOperation != null) {
                return invokedOperation;
            }
        }
        throw new OperationNotFoundException("Cannot match a SOAP operation to the given SOAP request");
    }


    private BindingOperation getOperationBySoapAction(SoapMessage message) {
        final String soapActionToMatch = SoapBuilder.normalizeSoapAction(message.getSoapAction());
        // optimization - if no soap action skip the visitor
        if (StringUtils.isBlank(soapActionToMatch)) {
            return null;
        }
        AggregatingVisitor<BindingOperation> visitor = new AggregatingVisitor<BindingOperation>() {
            @Override
            public void visit(BindingOperation operation) {
                String soapAction = SoapBuilder.normalizeSoapAction(SoapBuilder.getSOAPActionUri(operation));
                if (soapAction.equals(soapActionToMatch)) {
                    addResult(operation);
                }
            }
        };
        visitOperation(visitor);
        return visitor.getUniqueResult();
    }

    private BindingOperation getOperationByRootQName(Set<Node> rootNodes) throws OperationNotFoundException {
        // check if only one root node exists
        if (rootNodes.isEmpty() || rootNodes.size() > 1) {
            throw new OperationNotFoundException("No unique top-level node containing the operation name in the rpc request.");
        }
        QName root = XmlUtils.nodeToQName(rootNodes.iterator().next());
        return matchElementNameToOperationName(root);
    }

    /**
     * rpc-style -> operation name is always encoded in the request
     */
    private BindingOperation matchElementNameToOperationName(final QName elementName) {
        AggregatingVisitor<BindingOperation> visitor = new AggregatingVisitor<BindingOperation>() {
            @Override
            public void visit(BindingOperation operation) {
                if (operation.getOperation().getName().equals(elementName.getLocalPart())) {
                    addResult(operation);
                }
            }
        };
        visitOperation(visitor);
        return visitor.getUniqueResult();
    }

    /**
     * Last matching mechanism ->
     * When a non ws-compliant document-literal service specifies wsdl:part using the type instead of the element tag
     * Resources:
     * http://stackoverflow.com/questions/1172118/what-is-the-difference-between-type-and-element-in-wsdl
     * http://www.xfront.com/ElementVersusType.html
     * http://www.xfront.com/GlobalVersusLocal.html !!!
     *
     * @param rootNodes root nodes of the request
     * @return operation matched
     */
    private BindingOperation getOperationByInputTypes(final Set<Node> rootNodes) {
        AggregatingVisitor<BindingOperation> visitor = new AggregatingVisitor<BindingOperation>() {
            @Override
            public void visit(BindingOperation operation) {
                Collection<Part> expectedParts = operation.getOperation().getInput().getMessage().getParts().values();
                Set<QName> expectedTypes = new HashSet<QName>();
                for (Part part : expectedParts) {
                    expectedTypes.add(part.getElementName());
                }
                Set<QName> receivedTypes = XmlUtils.getNodeTypes(rootNodes);
                if (expectedTypes.equals(receivedTypes)) {
                    addResult(operation);
                } else if (expectedParts.isEmpty() && receivedTypes.size() == 1) {
                    // check the case when pseudo input name was sent when no input was expected and got one element
                    QName receivedType = receivedTypes.toArray(new QName[receivedTypes.size()])[0];
                    String namespaceUri = operation.getOperation().getInput().getMessage().getQName().getNamespaceURI();
                    String name = operation.getOperation().getName();
                    QName pseudoInputName = new QName(namespaceUri, name);
                    if (pseudoInputName.equals(receivedType)) {
                        addResult(operation);
                    }
                }
            }
        };
        visitOperation(visitor);
        return visitor.getUniqueResult();
    }

    /**
     * document style service -> there is not encoded operation name - matching based on the input style
     *
     * @param rootNodes root nodes of the request
     * @return operation matched
     */
    private BindingOperation getOperationByInputNames(final Set<Node> rootNodes) {
        AggregatingVisitor<BindingOperation> visitor = new AggregatingVisitor<BindingOperation>() {
            @Override
            public void visit(BindingOperation operation) {
                Set<String> expectedNames = operation.getOperation().getInput().getMessage().getParts().keySet();
                Set<String> receivedNames = XmlUtils.getNodeNames(rootNodes);
                if (receivedNames.equals(expectedNames)) {
                    addResult(operation);
                }
            }
        };
        visitOperation(visitor);
        return visitor.getUniqueResult();
    }

    @SuppressWarnings("unchecked")
    private <T extends BindingOperationVisitor> T visitOperation(T visitor) {
        for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
            try {
                visitor.visit(operation);
            } catch (NullPointerException ex) {
                // double-check in case of malformed WSDL's
            }
        }
        return visitor;
    }

    public boolean isRequestResponseOperation(BindingOperation operation) {
        return operation.getOperation().getStyle().equals(OperationType.REQUEST_RESPONSE);
    }

}
