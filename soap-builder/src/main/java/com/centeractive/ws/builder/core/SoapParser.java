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
package com.centeractive.ws.builder.core;

import com.centeractive.ws.builder.SoapBuilderException;
import com.centeractive.ws.builder.soap.SoapContext;
import com.centeractive.ws.builder.soap.SoapMessageBuilder;

import javax.wsdl.Binding;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapParser {

    private final SoapMessageBuilder builder;

    public SoapParser(URL wsdlUrl) {
        try {
            builder = new SoapMessageBuilder(wsdlUrl);
        } catch (WSDLException e) {
            throw new SoapBuilderException(e);
        }
    }

    public List<QName> getBindings() {
        return builder.getBindingNames();
    }

    public SoapBuilder getBuilder(QName bindingName) {
        return getBuilder(bindingName, SoapContext.builder().build());
    }

    public SoapBuilder getBuilder(QName bindingName, SoapContext context) {
        Binding binding = builder.getBindingByName(bindingName);
        return new SoapBuilderImpl(builder, binding, context);
    }

    public static void saveWsdl(URL wsdlUrl, String rootFileName, File folder) {
        try {
            SoapMessageBuilder.saveWsdl(rootFileName, wsdlUrl, folder);
        } catch (WSDLException e) {
            throw new SoapBuilderException(e);
        }
    }

}
