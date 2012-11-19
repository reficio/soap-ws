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
package com.centeractive.ws.legacy;

import com.centeractive.ws.SoapBuilderException;
import com.centeractive.ws.SoapContext;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.xml.namespace.QName;
import java.io.File;
import java.net.URL;
import java.util.List;

public class SoapLegacyFacade {

    public static enum Soap {SOAP_1_1, SOAP_1_2}

    private SoapMessageBuilder messageBuilder;

    public SoapLegacyFacade(URL wsdlUrl) throws WSDLException {
        this(wsdlUrl, SoapContext.builder().build());
    }

    public SoapLegacyFacade(URL wsdlUrl, SoapContext context) throws WSDLException {
        this.messageBuilder = new SoapMessageBuilder(context, wsdlUrl);
    }

    public String buildSoapMessageFromInput(Binding binding, BindingOperation bindingOperation, SoapContext context) {
        try {
            return messageBuilder.buildSoapMessageFromInput(binding, bindingOperation, context);
        } catch (Exception e) {
            throw new SoapBuilderException(e);
        }
    }

    public String buildSoapMessageFromOutput(Binding binding, BindingOperation bindingOperation, SoapContext context) {
        try {
            return messageBuilder.buildSoapMessageFromOutput(binding, bindingOperation, context);
        } catch (Exception e) {
            throw new SoapBuilderException(e);
        }
    }

    public String buildFault(String code, String message, Binding binding) {
        return messageBuilder.buildFault(code, message, binding);
    }

    public String buildEmptyFault(Binding binding) {
        return messageBuilder.buildEmptyFault(binding);
    }

    public String buildEmptyMessage(Binding binding) {
        return messageBuilder.buildEmptyMessage(binding);
    }

    public URL saveWsdl(String rootFileName, File folder) {
        return messageBuilder.saveWsdl(rootFileName, folder);
    }

    public static URL saveWsdl(String rootFileName, URL wsdlUrl, File folder) {
        try {
            return SoapMessageBuilder.saveWsdl(rootFileName, wsdlUrl, folder);
        } catch (WSDLException e) {
            throw new SoapBuilderException(e);
        }
    }

    public Binding getBindingByName(QName bindingName) {
        return messageBuilder.getBindingByName(bindingName);
    }

    public List<QName> getBindingNames() {
        return messageBuilder.getBindingNames();
    }

    public static String buildEmptyMessage(SoapVersion soapVersion) {
        return SoapMessageBuilder.buildEmptyMessage(soapVersion);
    }

    public static String buildEmptyFault(SoapVersion soapVersion) {
        return SoapMessageBuilder.buildEmptyFault(soapVersion);
    }

    public static String buildFault(String code, String message, SoapVersion soapVersion) {
        return SoapMessageBuilder.buildFault(code, message, soapVersion);
    }

    private static SoapVersion transformSoapVersion(Soap soapVersion) {
        if (soapVersion.equals(Soap.SOAP_1_1)) {
            return SoapVersion.Soap11;
        } else {
            return SoapVersion.Soap12;
        }
    }

    public static String buildEmptyMessage(Soap version) {
        return SoapLegacyFacade.buildEmptyMessage(transformSoapVersion(version));
    }

    public static String buildEmptyFault(Soap version) {
        return SoapLegacyFacade.buildEmptyFault(transformSoapVersion(version));
    }

    public static String buildFault(Soap version, String code, String message) {
        return SoapLegacyFacade.buildFault(code, message, transformSoapVersion(version));
    }

    public static boolean isRpc(Binding binding) {
        SOAPBinding soapBinding = WsdlUtils
                .getExtensiblityElement(binding.getExtensibilityElements(), SOAPBinding.class);

        if (soapBinding != null)
            return "rpc".equalsIgnoreCase(soapBinding.getStyle());

        SOAP12Binding soap12Binding = WsdlUtils.getExtensiblityElement(binding.getExtensibilityElements(),
                SOAP12Binding.class);

        if (soap12Binding != null)
            return "rpc".equalsIgnoreCase(soap12Binding.getStyle());

        return false;
    }

}
