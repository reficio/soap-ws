package com.centeractive.ws.server.responder;

import com.centeractive.SoapBuilder;
import com.centeractive.soap.WsdlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.OperationType;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 18/11/11
 * Time: 10:19 AM
 */
public abstract class AbstractResponder implements RequestResponder {

    private final static Log log = LogFactory.getLog(AbstractResponder.class);

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

    private QName getRootQName(DOMSource request) {
        return nodeToQName(request.getNode());
    }

    // rpc-style -> operation name is always encoded in the request
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
            names.add(new QName(node.getNamespaceURI(), node.getLocalName()));
        }
        return names;
    }

    // document style -> there is not encoded operation name - matching based on the input style
    private BindingOperation matchToInputNames(Set<Node> rootNodes) {
        for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
            try {
                Set<String> expectedNames = operation.getOperation().getInput().getMessage().getParts().keySet();
                Set<String> receivedNames = getNodeNames(rootNodes);
                if (receivedNames.equals(expectedNames)) {
                    return operation;
                }
            } catch (NullPointerException ex) {
                // double-check in case of malformed WSDL's
            }
        }
        return null;
    }

    private BindingOperation matchToInputTypes(Set<Node> rootNodes) {
        for (BindingOperation operation : (List<BindingOperation>) binding.getBindingOperations()) {
            try {
                Collection<Part> expectedParts = operation.getOperation().getInput().getMessage().getParts().values();
                Set<QName> expectedTypes = new HashSet<QName>();
                for(Part part : expectedParts) {
                    expectedTypes.add(part.getElementName());
                }
                Set<QName> receivedTypes = getNodeTypes(rootNodes);
                if (expectedTypes.equals(receivedTypes)) {
                    return operation;
                } else if(expectedParts.size() == 0 && receivedTypes.size() == 1) {
                    QName receivedType = receivedTypes.toArray(new QName[]{})[0];
                    String namespaceUri = operation.getOperation().getInput().getMessage().getQName().getNamespaceURI();
                    String name = operation.getOperation().getName();
                    QName pseudoInputName = new QName(namespaceUri, name);
                    if(pseudoInputName.equals(receivedType)) {
                        return operation;
                    }
                }
            } catch (NullPointerException ex) {
                // double-check in case of malformed WSDL's
            }
        }
        return null;
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

    private BindingOperation getOperationByRequestElement(DOMSource request) throws OperationNotFoundException {
        Set<Node> rootNodes = getRootNodes(request);
        BindingOperation invokedOperation = null;
        if (isRpc()) {
            if (rootNodes.size() > 1) {
                throw new OperationNotFoundException("No operation node in the rpc request.");
            }
            QName root = getRootQName(request);
            invokedOperation = matchToOperationName(root);
        } else {
            invokedOperation = matchToInputNames(rootNodes);
            if(invokedOperation == null) {
                invokedOperation = matchToInputTypes(rootNodes);
            }
        }
        if (invokedOperation != null) {
            return invokedOperation;
        }
        throw new OperationNotFoundException("Cannot match a SOAP operation to the given SOAP request");
    }

    private boolean isRequestResponseOperation(BindingOperation operation) {
        return operation.getOperation().getStyle().equals(OperationType.REQUEST_RESPONSE);
    }

    @Override
    public Source respond(Source request) {
        try {
            BindingOperation invokedOperation = getOperationByRequestElement((DOMSource) request);
            if (isRequestResponseOperation(invokedOperation)) {
                return respond(invokedOperation, request);
            }
            return null;
        } catch (OperationNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract Source respond(BindingOperation invokedOperation, Source request);

}
