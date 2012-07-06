package com.centeractive.ws.builder.core;

import com.centeractive.ws.builder.SoapBuilderException;
import com.centeractive.ws.builder.soap.SoapBuilderLegacy;

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

    private final SoapBuilderLegacy builder;

    public SoapParser(URL wsdlUrl) {
        try {
            builder = new SoapBuilderLegacy(wsdlUrl);
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
            SoapBuilderLegacy.saveWsdl(rootFileName, wsdlUrl, folder);
        } catch (WSDLException e) {
            throw new SoapBuilderException(e);
        }
    }

}
