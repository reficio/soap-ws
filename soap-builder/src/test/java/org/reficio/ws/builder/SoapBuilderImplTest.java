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
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.junit.Test;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.common.ResourceUtils;

import javax.wsdl.WSDLException;
import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertNotNull;

public class SoapBuilderImplTest {

    @Test
    public void testLoadSnowboard_Bug_851() throws WSDLException {
        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("builder", "snowboard.wsdl");
        SoapBuilder builder = Wsdl.parse(wsdlUrl).binding().name("{http://namespaces.snowboard-info" +
                ".com}EndorsementSearchSoapBinding").find();
        for (SoapOperation op : builder.getOperations()) {
            assertNotNull(op);
        }
    }

    //Playground...more to refine
    @Test
    public void testAutotask() throws WSDLException {
        String url = "https://webservices2.autotask.net/atservices/1.5/atws.wsdl";
        String netsuiteUrl = "https://webservices.netsuite.com/wsdl/v2016_1_0/netsuite.wsdl";
        Wsdl wsdl = Wsdl.parse(url);
        SoapBuilder builder = wsdl.binding().name("{http://autotask.net/ATWS/v1_5/}ATWSSoap").find();
        for (SoapOperation op : builder.getOperations()) {
            assertNotNull(op);
            if (op.getOperationName().equals("create")) {
                boolean isInputAbstract = false;
                if (builder.isInputMessageAbstract(op)) {
                    //Get the abstract Type
                    SchemaType abstractType = builder.getAbstractSchemaTypeFromOperation(op);
                    //Get all the complextypes for the abstract types
                    //loop through the complextypes to construct the soap envelope for each complex type

                    List<SchemaType> children = builder.getChildrenForType(abstractType);
                    for (SchemaType childSchemaType: children) {
                        if(StringUtils.equals("Contact", childSchemaType.getName().getLocalPart())) {
                            System.out.println("-------------"+childSchemaType.getName().getLocalPart()+"-------------");
                            String inputMessage = builder.buildInputMessage(op, abstractType, childSchemaType);
                            System.out.println(inputMessage);
                            System.out.println("*************************************************************");
                        }
                    }

                }

//                String inputMessage = builder.buildInputMessage(op);
//                System.out.println(inputMessage);
            }
        }
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
