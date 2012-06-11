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
package com.centeractive.ws.server.responder;

import com.centeractive.ws.builder.core.SoapBuilder;
import com.centeractive.ws.builder.soap.WsdlUtils;
import com.centeractive.ws.builder.soap.domain.OperationWrapper;
import com.centeractive.ws.server.SoapServerException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Node;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.OperationType;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.util.*;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public abstract class AbstractResponder implements RequestResponder {

    protected final SoapBuilder builder;
    protected final QName bindingName;
    protected final Binding binding;
    protected final boolean rpc;

    public AbstractResponder(SoapBuilder builder, QName bindingName) {
        this.builder = builder;
        this.bindingName = bindingName;
        this.binding = builder.getDefinition().getBinding(bindingName);
        this.rpc = WsdlUtils.isRpc(binding);
    }

    protected boolean isRpc() {
        return rpc;
    }

    protected boolean isDocument() {
        return isRpc() == false;
    }

    private QName nodeToQName(Node node) {
        return new QName(node.getNamespaceURI(), node.getLocalName());
    }

    // rpc-style -> operation name is always encoded in the request
    @SuppressWarnings("unchecked")
    private BindingOperation matchToOperationName(QName elementName) {
        for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
            if (operation.getOperation().getName().equals(elementName.getLocalPart())) {
                return operation;
            }
        }
        return null;
    }

    private Set<String> getNodeNames(Set<Node> nodes) {
        Set<String> names = new HashSet<String>();
        for (Node node : nodes) {
            names.add(node.getLocalName());
        }
        return names;
    }

    private Set<QName> getNodeTypes(Set<Node> nodes) {
        Set<QName> names = new HashSet<QName>();
        for (Node node : nodes) {
            names.add(nodeToQName(node));
        }
        return names;
    }

    private Set<Node> getRootNodes(DOMSource request) {
        return populateNodes(request.getNode(), new HashSet<Node>());
    }

    private Set<Node> populateNodes(Node node, Set<Node> nodes) {
        if (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                nodes.add(node);
            }
            populateNodes(node.getNextSibling(), nodes);
        }
        return nodes;
    }

    private BindingOperation getInvokedOperation(SoapMessage message) throws OperationNotFoundException {
        // SOAP action mapping - cheapest and fastest as no request analysis is required
        BindingOperation invokedOperation = null;
        invokedOperation = getOperationBySoapAction(message);
        if (invokedOperation != null) {
            return invokedOperation;
        }
        Set<Node> rootNodes = getRootNodes((DOMSource) message.getPayloadSource());
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

    @SuppressWarnings("unchecked")
    private BindingOperation getOperationBySoapAction(SoapMessage message) {
        String soapActionToMatch = SoapBuilder.normalizeSoapAction(message.getSoapAction());
        if (StringUtils.isBlank(soapActionToMatch)) {
            return null;
        }
        for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
            try {
                String soapAction = SoapBuilder.normalizeSoapAction(SoapBuilder.getSOAPActionUri(operation));
                if (StringUtils.isBlank(soapAction)) {
                    continue;
                }
                if (soapAction.equals(soapActionToMatch)) {
                    return operation;
                }
            } catch (NullPointerException ex) {
                // double-check in case of malformed WSDL's
            }
        }
        return null;
    }

    private BindingOperation getOperationByRootQName(Set<Node> rootNodes) throws OperationNotFoundException {
        if (rootNodes.isEmpty() || rootNodes.size() > 1) {
            throw new OperationNotFoundException("No unique top-level node containing the operation name in the rpc request.");
        }
        QName root = nodeToQName(rootNodes.iterator().next());
        return matchToOperationName(root);
    }

    // last chance matching -> for example when a non ws-compliant document-literal service specifies
    //   wsdl:part using type instead of element tag
    // Resources:
    //   http://stackoverflow.com/questions/1172118/what-is-the-difference-between-type-and-element-in-wsdl
    //   http://www.xfront.com/ElementVersusType.html
    //   http://www.xfront.com/GlobalVersusLocal.html !!!
    @SuppressWarnings("unchecked")
    private BindingOperation getOperationByInputTypes(Set<Node> rootNodes) {
        for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
            try {
                Collection<Part> expectedParts = operation.getOperation().getInput().getMessage().getParts().values();
                Set<QName> expectedTypes = new HashSet<QName>();
                for (Part part : expectedParts) {
                    expectedTypes.add(part.getElementName());
                }
                Set<QName> receivedTypes = getNodeTypes(rootNodes);
                if (expectedTypes.equals(receivedTypes)) {
                    return operation;
                } else if (expectedParts.size() == 0 && receivedTypes.size() == 1) {
                    QName receivedType = receivedTypes.toArray(new QName[]{})[0];
                    String namespaceUri = operation.getOperation().getInput().getMessage().getQName().getNamespaceURI();
                    String name = operation.getOperation().getName();
                    QName pseudoInputName = new QName(namespaceUri, name);
                    if (pseudoInputName.equals(receivedType)) {
                        return operation;
                    }
                }
            } catch (NullPointerException ex) {
                // double-check in case of malformed WSDL's
            }
        }
        return null;
    }

    // document style -> there is not encoded operation name - matching based on the input style
    @SuppressWarnings("unchecked")
    private BindingOperation getOperationByInputNames(Set<Node> rootNodes) {
        Stack<BindingOperation> matchedOperations = new Stack<BindingOperation>();
        for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
            try {
                Set<String> expectedNames = operation.getOperation().getInput().getMessage().getParts().keySet();
                Set<String> receivedNames = getNodeNames(rootNodes);
                if (receivedNames.equals(expectedNames)) {
                    matchedOperations.push(operation);
                }
            } catch (NullPointerException ex) {
                // double-check in case of malformed WSDL's
            }
        }
        if (matchedOperations.size() == 1) {
            return matchedOperations.pop();
        }
        return null;
    }

    private boolean isRequestResponseOperation(BindingOperation operation) {
        return operation.getOperation().getStyle().equals(OperationType.REQUEST_RESPONSE);
    }

    /**
     * SOAP-Action mystery (1.1):
     * http://ws-rx.blogspot.com/2006/01/web-services-design-tips-soapaction.html
     * http://www.w3.org/TR/2000/NOTE-SOAP-20000508/#_Toc478383528
     * http://www.oreillynet.com/xml/blog/2002/11/unraveling_the_mystery_of_soap.html
     * http://damithakumarage.wordpress.com/2008/02/12/soap-action-and-addressing-action/
     */
    @Override
    public Source respond(SoapMessage message) {
        try {
            BindingOperation invokedOperation = getInvokedOperation(message);
            if (isRequestResponseOperation(invokedOperation)) {
                OperationWrapper operation = builder.getOperation(binding, invokedOperation, message.getSoapAction());
                return respond(operation, message);
            }
            return null;
        } catch (OperationNotFoundException e) {
            throw new SoapServerException(e);
        }
    }

    public abstract Source respond(OperationWrapper invokedOperation, SoapMessage message);

}
