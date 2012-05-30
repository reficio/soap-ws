package com.centeractive.ws.server.responder;

import com.centeractive.ws.builder.core.SoapBuilder;
import com.centeractive.ws.builder.core.SoapContext;
import com.centeractive.ws.builder.soap.XmlUtils;
import com.centeractive.ws.builder.soap.domain.OperationWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 * <p/>
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
    public Source respond(OperationWrapper invokedOperation, SoapMessage message) {
        try {
            String response = builder.buildSoapMessageFromOutput(invokedOperation, context);
            Source responseSource = XmlUtils.xmlStringToSource(response);
            return responseSource;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
