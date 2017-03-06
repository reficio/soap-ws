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

import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.ServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.reficio.ws.builder.core.Wsdl;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertTrue;

public class SoapBuilderNetforumTest {
    Wsdl wsdl;

    @Before
    public void setup() {
        String url = "https://www.cfa.com/xweb/secure/netforumxml.asmx?wsdl";
        wsdl = Wsdl.parse(url);
    }

    @Test
    public void testNetforumOperations() throws WSDLException {

        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        Definition definition = reader.readWSDL("https://www.cfa.com/xweb/secure/netforumxml.asmx?wsdl");
        Map services = definition.getServices();

        Set<Map.Entry<QName, ServiceImpl>> entries = services.entrySet();
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            Map.Entry<QName, ServiceImpl> entry = (Map.Entry<QName, ServiceImpl>) iter.next();
            ServiceImpl service = entry.getValue();

            Map<String, PortImpl> ports = (Map<String, PortImpl>) service.getPorts();

            Set<Map.Entry<String, PortImpl>> portsEntries = ports.entrySet();
            Iterator portIter = portsEntries.iterator();
            while (portIter.hasNext()) {
                Map.Entry<String, PortImpl> portEntry = (Map.Entry<String, PortImpl>) portIter.next();
                PortImpl port = portEntry.getValue();

                SoapBuilder builder = wsdl.binding().name(port.getBinding().getQName()).find();
                for (SoapOperation op : builder.getOperations()) {
                    try {
                        String inputMessage = builder.buildInputMessage(op);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        assertTrue(true);
    }
}
