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
package org.reficio.ws.camel;

import javax.xml.transform.Source;

import org.apache.camel.CamelContext;
import org.apache.camel.TypeConverter;
import org.apache.camel.builder.xml.XPathBuilder;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.legacy.SoapVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author piotr.jagielski
 */
public class SoapEnvelopeWrapper {

    private final SoapBuilder builder;

    public SoapEnvelopeWrapper(SoapBuilder builder) {
        this.builder = builder;
    }

    public String wrap(Object payload, CamelContext context) {
        TypeConverter converter = context.getTypeConverter();
        Document soapDocument = wrapToDocument(payload, context);
        return converter.convertTo(String.class, soapDocument);
    }

    public Source wrapToSource(Object payload, CamelContext context) {
        TypeConverter converter = context.getTypeConverter();
        Document soapDocument = wrapToDocument(payload, context);
        return converter.convertTo(Source.class, soapDocument);
    }

    private Document wrapToDocument(Object payload, CamelContext context) {
        TypeConverter converter = context.getTypeConverter();
        String generated = builder.buildEmptyMessage();

        Document generatedDocument = converter.convertTo(Document.class, generated);
        Document payloadDocument = converter.convertTo(Document.class, payload);

        SoapVersion soapVersion = builder.getSoapVersion();
        Node soapBody = XPathBuilder.xpath("//env:Envelope//env:Body")
            .namespace("env", soapVersion.getEnvelopeNamespace())
            .nodeResult()
            .evaluate(context, generatedDocument, Node.class);

        soapBody.appendChild(
            generatedDocument.importNode(payloadDocument.getDocumentElement(), true));

        return generatedDocument;
    }

    public String unwrap(Object payload, CamelContext context) {
        TypeConverter converter = context.getTypeConverter();
        SoapVersion soapVersion = builder.getSoapVersion();
        Node responseBody = XPathBuilder.xpath("//env:Envelope//env:Body//*")
            .namespace("env", soapVersion.getEnvelopeNamespace())
            .nodeResult()
            .evaluate(context, payload, Node.class);
        return converter.convertTo(String.class, responseBody);
    }

}
