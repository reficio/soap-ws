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
package com.centeractive.ws.server.util;

import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class XmlUtils {

    public static Set<String> getNodeNames(Set<Node> nodes) {
        Set<String> names = new HashSet<String>();
        for (Node node : nodes) {
            names.add(node.getLocalName());
        }
        return names;
    }

    public static Set<QName> getNodeTypes(Set<Node> nodes) {
        Set<QName> names = new HashSet<QName>();
        for (Node node : nodes) {
            names.add(nodeToQName(node));
        }
        return names;
    }

    public static Set<Node> getRootNodes(DOMSource request) {
        return populateNodes(request.getNode(), new HashSet<Node>());
    }

    public static Set<Node> populateNodes(Node node, Set<Node> nodes) {
        if (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                nodes.add(node);
            }
            populateNodes(node.getNextSibling(), nodes);
        }
        return nodes;
    }

    public static QName nodeToQName(Node node) {
        return new QName(node.getNamespaceURI(), node.getLocalName());
    }

}
