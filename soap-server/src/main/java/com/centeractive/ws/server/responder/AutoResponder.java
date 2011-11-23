package com.centeractive.ws.server.responder;

import com.centeractive.SoapBuilder;
import com.centeractive.SoapContext;
import com.centeractive.soap.XmlUtils;
import com.centeractive.soap.domain.OperationWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 16/11/11
 * Time: 3:38 PM
 */
public class AutoResponder extends AbstractResponder {

    private final static Log log = LogFactory.getLog(AutoResponder.class);

    private final SoapContext context;

    public AutoResponder(SoapBuilder builder, QName bindingName) {
        super(builder, bindingName);
        context = SoapContext.builder().exampleContent(true).create();
    }

    public AutoResponder(SoapBuilder builder, QName bindingName, SoapContext context) {
        super(builder, bindingName);
        this.context = context;
    }

    @Override
    public Source respond(BindingOperation invokedOperation, Source request) {
        BindingOutput output = invokedOperation.getBindingOutput();
        if (output == null) {
            return null;
        }
        OperationWrapper wrapper = builder.getOperation(binding, invokedOperation);
        try {
            String response = builder.buildSoapMessageFromOutput(wrapper, context);
            Source responseSource = XmlUtils.xmlToSource(response);
            return responseSource;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
