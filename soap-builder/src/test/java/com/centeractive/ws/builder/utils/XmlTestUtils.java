package com.centeractive.ws.builder.utils;

import com.centeractive.ws.builder.soap.XmlUtils;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 14/10/11
 * Time: 11:20 AM
 */
public class XmlTestUtils {

    private final static Logger log = Logger.getLogger(XmlTestUtils.class);

    public static String normalizeAndRemoveValues(String xmlContent)  {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setCoalescing(true);
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setIgnoringComments(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new ByteArrayInputStream(xmlContent.getBytes()));
            document.normalizeDocument();
            processNode(document);
            return XmlUtils.serializePretty(document);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void processNode(Node node) throws Exception {
        if(node.hasChildNodes()) {
            for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                processNode(child);
            }
        } else {
            node.setTextContent(" ");
        }
    }

    public static boolean isIdenticalNormalizedWithoutValues(String expected, String current) {
        String expectedProcessed = normalizeAndRemoveValues(expected);
        String currentProcessed = normalizeAndRemoveValues(current);
        try {
            return XMLUnit.compareXML(expectedProcessed, currentProcessed).identical();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
