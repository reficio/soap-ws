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

import org.junit.Test;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.common.ResourceUtils;

import javax.wsdl.WSDLException;
import java.net.URL;

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
}
