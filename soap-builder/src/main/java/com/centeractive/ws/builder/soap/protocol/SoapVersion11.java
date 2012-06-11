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
package com.centeractive.ws.builder.soap.protocol;

import com.centeractive.ws.builder.SoapBuilderException;
import com.centeractive.ws.builder.soap.Constants;
import com.centeractive.ws.builder.utils.ResourceUtils;
import com.centeractive.ws.builder.soap.XmlUtils;
import org.apache.xmlbeans.*;
import org.xmlsoap.schemas.soap.envelope.EnvelopeDocument;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.URL;

/**
 * SoapVersion for SOAP 1.1
 *
 * @author ole.matzura
 */

public class SoapVersion11 extends AbstractSoapVersion {
    private final static QName envelopeQName = new QName(Constants.SOAP11_ENVELOPE_NS, "Envelope");
    private final static QName bodyQName = new QName(Constants.SOAP11_ENVELOPE_NS, "Body");
    private final static QName faultQName = new QName(Constants.SOAP11_ENVELOPE_NS, "Fault");
    private final static QName headerQName = new QName(Constants.SOAP11_ENVELOPE_NS, "Header");

    SchemaTypeLoader soapSchema;
    SchemaType soapEnvelopeType;
    private XmlObject soapSchemaXml;
    private XmlObject soapEncodingXml;
    private SchemaType soapFaultType;

    public final static SoapVersion11 instance = new SoapVersion11();

    private SoapVersion11() {
        try {
            XmlOptions options = new XmlOptions();
            options.setCompileNoValidation();
            options.setCompileNoPvrRule();
            options.setCompileDownloadUrls();
            options.setCompileNoUpaRule();
            options.setValidateTreatLaxAsSkip();

            URL soapSchemaXmlResource = ResourceUtils.getResourceWithAbsolutePackagePath(getClass(),
                    "/xsds/", "soapEnvelope.xsd");
            soapSchemaXml = XmlUtils.createXmlObject(soapSchemaXmlResource, options);
            soapSchema = XmlBeans.loadXsd(new XmlObject[]{soapSchemaXml});

            soapEnvelopeType = soapSchema.findDocumentType(envelopeQName);
            soapFaultType = soapSchema.findDocumentType(faultQName);

            URL soapEncodingXmlResource = ResourceUtils.getResourceWithAbsolutePackagePath(getClass(),
                    "/xsds/", "soapEncoding.xsd");
            soapEncodingXml = XmlUtils.createXmlObject(soapEncodingXmlResource, options);

        }catch(XmlException ex) {
            throw new SoapBuilderException(ex);
        }
    }

    public SchemaType getEnvelopeType() {
        return EnvelopeDocument.type;
    }

    public String getEnvelopeNamespace() {
        return Constants.SOAP11_ENVELOPE_NS;
    }

    public String getEncodingNamespace() {
        return Constants.SOAP_ENCODING_NS;
    }

    public XmlObject getSoapEncodingSchema() throws XmlException, IOException {
        return soapEncodingXml;
    }

    public XmlObject getSoapEnvelopeSchema() throws XmlException, IOException {
        return soapSchemaXml;
    }

    public String toString() {
        return "SOAP 1.1";
    }

    public String getContentTypeHttpHeader(String encoding, String soapAction) {
        if (encoding == null || encoding.trim().length() == 0)
            return getContentType();
        else
            return getContentType() + ";charset=" + encoding;
    }

    public String getSoapActionHeader(String soapAction) {
        if (soapAction == null || soapAction.length() == 0) {
            soapAction = "\"\"";
        } else {
            soapAction = "\"" + soapAction + "\"";
        }

        return soapAction;
    }

    public String getContentType() {
        return "text/xml";
    }

    public QName getBodyQName() {
        return bodyQName;
    }

    public QName getEnvelopeQName() {
        return envelopeQName;
    }

    public QName getHeaderQName() {
        return headerQName;
    }

    protected SchemaTypeLoader getSoapEnvelopeSchemaLoader() {
        return soapSchema;
    }

    public SchemaType getFaultType() {
        return soapFaultType;
    }

    public String getName() {
        return "SOAP 1.1";
    }

    public String getFaultDetailNamespace() {
        return "";
    }
}
