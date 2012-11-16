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

import com.centeractive.ws.SoapBuilderException;
import com.centeractive.ws.SoapContext;
import com.centeractive.ws.builder.SoapBuilder;
import com.google.common.base.Preconditions;

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
public final class WsdlParser {

    private final URL wsdlUrl;
    private final SoapMessageBuilder messageBuilder;

    private WsdlParser(URL wsdlUrl) {
        try {
            this.wsdlUrl = wsdlUrl;
            messageBuilder = new SoapMessageBuilder(wsdlUrl);
        } catch (WSDLException e) {
            throw new SoapBuilderException(e);
        }
    }

    public static WsdlParser parse(URL wsdlUrl) {
        Preconditions.checkNotNull(wsdlUrl, "URL of the WSDL cannot be null");
        return new WsdlParser(wsdlUrl);
    }

    public List<QName> getBindings() {
        return messageBuilder.getBindingNames();
    }

    public void printBindings() {
        System.out.println(wsdlUrl);
        for (QName bindingName : messageBuilder.getBindingNames()) {
            System.out.println("\t" + bindingName.toString());
        }
    }

    public SoapBuilder getBuilder(String bindingName) {
        return getBuilder(QName.valueOf(bindingName));
    }

    public SoapBuilder getBuilder(QName bindingName) {
        return getBuilder(bindingName, SoapContext.builder().build());
    }

    public SoapBuilder getBuilder(String bindingName, SoapContext context) {
        return getBuilder(QName.valueOf(bindingName), context);
    }

    public SoapBuilder getBuilder(QName bindingName, SoapContext context) {
        Preconditions.checkNotNull(context, "SoapContext cannot be null");
        Binding binding = messageBuilder.getBindingByName(bindingName);
        return new SoapBuilderImpl(messageBuilder, binding, context);
    }

    public URL saveWsdl(String rootFileName, File folder) {
        return messageBuilder.saveWsdl(rootFileName, folder);
    }

    public static URL saveWsdl(URL wsdlUrl, String rootFileName, File folder) {
        try {
            return SoapMessageBuilder.saveWsdl(rootFileName, wsdlUrl, folder);
        } catch (WSDLException e) {
            throw new SoapBuilderException(e);
        }
    }

}
