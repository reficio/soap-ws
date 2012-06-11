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
package com.centeractive.ws.builder.utils;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 14/10/11
 * Time: 10:31 AM
 */

import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.centeractive.ws.builder.soap.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XmlComparator {

    private final static Logger log = Logger.getLogger(XmlComparator.class);

    private boolean nodeTypeDiff = true;
    private boolean nodeValueDiff = true;

    public boolean diff(String xml1, String xml2, List<String> diffs) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);
        DocumentBuilder db = dbf.newDocumentBuilder();


        Document doc1 = db.parse(new ByteArrayInputStream(xml1.getBytes()));
        Document doc2 = db.parse(new ByteArrayInputStream(xml2.getBytes()));

        doc1.normalizeDocument();
        doc2.normalizeDocument();

        log.info("\n" + XmlUtils.serializePretty(doc1));
        log.info("\n" + XmlUtils.serializePretty(doc2));

        return diff(doc1, doc2, diffs);

    }

    /**
     * Diff 2 nodes and put the diffs in the list
     */
    public boolean diff(Node node1, Node node2, List<String> diffs) throws Exception {
        if (diffNodeExists(node1, node2, diffs)) {
            return true;
        }

        if (nodeTypeDiff) {
            diffNodeType(node1, node2, diffs);
        }

        if (nodeValueDiff) {
            diffNodeValue(node1, node2, diffs);
        }


        log.info(node1.getNodeName() + "/" + node2.getNodeName());

        diffAttributes(node1, node2, diffs);
        diffNodes(node1, node2, diffs);

        return diffs.size() > 0;
    }

    /**
     * Diff the nodes
     */
    public boolean diffNodes(Node node1, Node node2, List<String> diffs) throws Exception {
        //Sort by Name
        Map<String, Node> children1 = new LinkedHashMap<String, Node>();
        for (Node child1 = node1.getFirstChild(); child1 != null; child1 = child1.getNextSibling()) {
            children1.put(child1.getNodeName(), child1);
        }

        //Sort by Name
        Map<String, Node> children2 = new LinkedHashMap<String, Node>();
        for (Node child2 = node1.getFirstChild(); child2 != null; child2 = child2.getNextSibling()) {
            children2.put(child2.getNodeName(), child2);
        }

        //Diff all the children1
        for (Node child1 : children1.values()) {
            Node child2 = children2.remove(child1.getNodeName());
            diff(child1, child2, diffs);
        }

        //Diff all the children2 left over
        for (Node child2 : children2.values()) {
            Node child1 = children1.get(child2.getNodeName());
            diff(child1, child2, diffs);
        }

        return diffs.size() > 0;
    }


    /**
     * Diff the nodes
     */
    public boolean diffAttributes(Node node1, Node node2, List<String> diffs) throws Exception {
        //Sort by Name
        NamedNodeMap nodeMap1 = node1.getAttributes();
        Map<String, Node> attributes1 = new LinkedHashMap<String, Node>();
        for (int index = 0; nodeMap1 != null && index < nodeMap1.getLength(); index++) {
            attributes1.put(nodeMap1.item(index).getNodeName(), nodeMap1.item(index));
        }

        //Sort by Name
        NamedNodeMap nodeMap2 = node2.getAttributes();
        Map<String, Node> attributes2 = new LinkedHashMap<String, Node>();
        for (int index = 0; nodeMap2 != null && index < nodeMap2.getLength(); index++) {
            attributes2.put(nodeMap2.item(index).getNodeName(), nodeMap2.item(index));

        }

        //Diff all the attributes1
        for (Node attribute1 : attributes1.values()) {
            Node attribute2 = attributes2.remove(attribute1.getNodeName());
            diff(attribute1, attribute2, diffs);
        }

        //Diff all the attributes2 left over
        for (Node attribute2 : attributes2.values()) {
            Node attribute1 = attributes1.get(attribute2.getNodeName());
            diff(attribute1, attribute2, diffs);
        }

        return diffs.size() > 0;
    }

    /**
     * Check that the nodes exist
     */
    public boolean diffNodeExists(Node node1, Node node2, List<String> diffs) throws Exception {
        if (node1 == null && node2 == null) {
            diffs.add(getPath(node2) + ":node " + node1 + "!=" + node2 + "\n");
            return true;
        }

        if (node1 == null && node2 != null) {
            diffs.add(getPath(node2) + ":node " + node1 + "!=" + node2.getNodeName());
            return true;
        }

        if (node1 != null && node2 == null) {
            diffs.add(getPath(node1) + ":node " + node1.getNodeName() + "!=" + node2);
            return true;
        }

        return false;
    }

    /**
     * Diff the Node Type
     */
    public boolean diffNodeType(Node node1, Node node2, List<String> diffs) throws Exception {
        if (node1.getNodeType() != node2.getNodeType()) {
            diffs.add(getPath(node1) + ":type " + node1.getNodeType() + "!=" + node2.getNodeType());
            return true;
        }

        return false;
    }

    /**
     * Diff the Node Value
     */
    public boolean diffNodeValue(Node node1, Node node2, List<String> diffs) throws Exception {
        if (node1.getNodeValue() == null && node2.getNodeValue() == null) {
            return false;
        }

        if (node1.getNodeValue() == null && node2.getNodeValue() != null) {
            diffs.add(getPath(node1) + ":type " + node1 + "!=" + node2.getNodeValue());
            return true;
        }

        if (node1.getNodeValue() != null && node2.getNodeValue() == null) {
            diffs.add(getPath(node1) + ":type " + node1.getNodeValue() + "!=" + node2);
            return true;
        }

        if (!node1.getNodeValue().equals(node2.getNodeValue())) {
            diffs.add(getPath(node1) + ":type " + node1.getNodeValue() + "!=" + node2.getNodeValue());
            return true;
        }

        return false;
    }


    /**
     * Get the node path
     */
    public String getPath(Node node) {
        StringBuilder path = new StringBuilder();

        do {
            path.insert(0, node.getNodeName());
            path.insert(0, "/");
        }
        while ((node = node.getParentNode()) != null);

        return path.toString();
    }
}
