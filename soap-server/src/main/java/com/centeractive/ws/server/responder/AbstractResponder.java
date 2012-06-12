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
import com.centeractive.ws.server.OperationNotFoundException;
import com.centeractive.ws.server.SoapServerException;
import org.apache.commons.lang3.StringUtils;
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
 * Convenience class that implements the RequestResponder interface and
 * contains a mechanism that matches the request to an operation from the Binding
 * RequestResponder method is implemented, but a new abstract respond method is added
 * which contains the invoked operation as an argument.
 * <p/>
 * Tries to match using the following mechanisms:
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
public abstract class AbstractResponder implements RequestResponder {

    protected final SoapBuilder builder;
    protected final QName bindingName;
    protected final Binding binding;
    protected final boolean rpc;

    /**
     * Constructs a responder for the specified binding of the builder
     *
     * @param builder     Soap builder used to construct messages
     * @param bindingName Binding to be used - builders may contain many bindings
     */
    public AbstractResponder(SoapBuilder builder, QName bindingName) {
        this.builder = builder;
        this.bindingName = bindingName;
        this.binding = builder.getDefinition().getBinding(bindingName);
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
     * @throws OperationNotFoundException if operation not found in the binding
     */
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

    /**
     * Last matching mechanism ->
     * When a non ws-compliant document-literal service specifies wsdl:part using the type instead of the element tag
     * Resources:
     * http://stackoverflow.com/questions/1172118/what-is-the-difference-between-type-and-element-in-wsdl
     * http://www.xfront.com/ElementVersusType.html
     * http://www.xfront.com/GlobalVersusLocal.html !!!
     */
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


    /**
     * document style service -> there is not encoded operation name - matching based on the input style
     */
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
     * Implementation of the RequestResponder bare method.
     * It matches the SoapMessage to the binding operation and invokes the
     * abstract respond method that contains OperationWrapper as an argument.
     *
     * @param message SOAP message passed by the client
     * @return response in the XML source format containing the whole SOAP envelope
     */
    @Override
    public Source respond(SoapMessage message) {
        try {
            BindingOperation invokedOperation = getInvokedOperation(message);
            if (isRequestResponseOperation(invokedOperation)) {
                OperationWrapper operation = SoapBuilder.getOperation(binding, invokedOperation, message.getSoapAction());
                return respond(operation, message);
            }
            return null;
        } catch (OperationNotFoundException e) {
            throw new SoapServerException(e);
        }
    }

    /**
     * Abstract method that should be implemented by overriding classes.
     * This method is invoked whenever a request is send by the client.
     * InvokedOperation may be passed to a SoapBuilder to construct the
     * response to the request that was sent by the client.
     *
     * @param invokedOperation operation from the binding that is matched to the SOAP message
     * @param message          SOAP message passed by the client
     * @return response in the XML source format containing the whole SOAP envelope
     */
    public abstract Source respond(OperationWrapper invokedOperation, SoapMessage message);

}
