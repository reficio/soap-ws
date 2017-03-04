/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.reficio.ws.builder;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.SchemaType;
import org.junit.Before;
import org.junit.Test;
import org.reficio.ws.builder.core.Wsdl;

import javax.wsdl.WSDLException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class SoapBuilderAutoTaskTest {
    SoapBuilder autoTaskSoapBuilder;

    @Before
    public void setup() {
        String url = "https://webservices2.autotask.net/atservices/1.5/atws.wsdl";
        Wsdl wsdl = Wsdl.parse(url);
        autoTaskSoapBuilder = wsdl.binding().name("{http://autotask.net/ATWS/v1_5/}ATWSSoap").find();
    }

    @Test
    public void testAutotaskOperations() throws WSDLException {
        assertNotNull(autoTaskSoapBuilder.getOperations());
        assertTrue(autoTaskSoapBuilder.getOperations().size() > 0);
    }

    @Test
    public void testOperationInputIsAbstract() throws WSDLException {
        SoapOperation createOperation = null;
        for (SoapOperation op : autoTaskSoapBuilder.getOperations()) {
            if (op.getOperationName().equals("create")) {
                createOperation = op;
                break;
            }
        }

        assertNotNull(createOperation);
        boolean isAbstract = autoTaskSoapBuilder.isInputMessageAbstract(createOperation);
        assertTrue(isAbstract);
    }

    @Test
    public void testOperationInputAbstractSchemaType() throws WSDLException {
        SoapOperation createOperation = null;
        for (SoapOperation op : autoTaskSoapBuilder.getOperations()) {
            if (op.getOperationName().equals("create")) {
                createOperation = op;
                break;
            }
        }

        assertNotNull(createOperation);
        SchemaType abstractType = autoTaskSoapBuilder.getAbstractSchemaTypeFromOperation(createOperation);
        assertNotNull(abstractType);
    }

    @Test
    public void testOperationInputAbstractSchemaChildTypes() throws WSDLException {
        SoapOperation createOperation = null;
        for (SoapOperation op : autoTaskSoapBuilder.getOperations()) {
            if (op.getOperationName().equals("create")) {
                createOperation = op;
                break;
            }
        }

        assertNotNull(createOperation);
        SchemaType abstractType = autoTaskSoapBuilder.getAbstractSchemaTypeFromOperation(createOperation);
        assertNotNull(abstractType);

        List<SchemaType> children = autoTaskSoapBuilder.getChildrenForType(abstractType);
        assertNotNull(children);
        assertTrue(children.size() > 0);
    }

    // Test the ability to get all the child schema types for an abstract schematype
    @Test
    public void testAutotaskChildSchemaTypeInputMessage() throws WSDLException {
        SoapOperation createOperation = null;
        for (SoapOperation op : autoTaskSoapBuilder.getOperations()) {
            if (op.getOperationName().equals("create")) {
                createOperation = op;
                break;
            }
        }

        assertNotNull(createOperation);
        SchemaType abstractType = autoTaskSoapBuilder.getAbstractSchemaTypeFromOperation(createOperation);
        assertNotNull(abstractType);

        List<SchemaType> children = autoTaskSoapBuilder.getChildrenForType(abstractType);
        assertNotNull(children);
        assertTrue(children.size() > 0);

        SchemaType contactSchemaType = null;
        for (SchemaType childSchemaType : children) {
            if (StringUtils.equals("Contact", childSchemaType.getName().getLocalPart())) {
                contactSchemaType = childSchemaType;
            }
        }

        String inputMessage = autoTaskSoapBuilder.buildInputMessage(createOperation, abstractType, contactSchemaType);
        assertNotNull(inputMessage);
        assertTrue(StringUtils.contains(inputMessage, "xsi:type=\"v1:Contact\""));
    }

//
//    @Test
//    public void testNetsuite() throws WSDLException {
//        String netsuiteUrl = "https://webservices.netsuite.com/wsdl/v2016_1_0/netsuite.wsdl";
//        Wsdl wsdl = Wsdl.parse(netsuiteUrl);
//
//        WSDLFactory factory = WSDLFactory.newInstance();
//        WSDLReader reader = factory.newWSDLReader();
//        Definition definition = reader.readWSDL(netsuiteUrl);
//        Map services = definition.getServices();
//
//        Set<Map.Entry<QName, ServiceImpl>> entries = services.entrySet();
//        Iterator iter = entries.iterator();
//        while (iter.hasNext()) {
//            Map.Entry<QName, ServiceImpl> entry = (Map.Entry<QName, ServiceImpl>) iter.next();
//            ServiceImpl service = entry.getValue();
//
//            Map<String, PortImpl> ports = (Map<String, PortImpl>) service.getPorts();
//
//            Set<Map.Entry<String, PortImpl>> portsEntries = ports.entrySet();
//            Iterator portIter = portsEntries.iterator();
//            while (portIter.hasNext()) {
//                Map.Entry<String, PortImpl> portEntry = (Map.Entry<String, PortImpl>) portIter.next();
//                PortImpl port = portEntry.getValue();
//
//                SoapBuilder builder = wsdl.binding().name(port.getBinding().getQName()).find();
//                for (SoapOperation op : builder.getOperations()) {
//                    if (op.getOperationName().equals("add")) {
//                        boolean isInputAbstract = false;
//                        if (!builder.isInputSoapEncoded(op) && builder.isInputMessageAbstract(op)) {
//                            isInputAbstract = true;
//                        }
//
//                        String inputMessage = builder.buildInputMessage(op);
//                        System.out.println(inputMessage);
//                    }
//                }
//            }
//        }
//
//        assertTrue(true);
//    }
}
