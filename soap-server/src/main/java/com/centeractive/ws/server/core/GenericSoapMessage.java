package com.centeractive.ws.server.core;

import com.centeractive.soap.XmlUtils;
import org.springframework.ws.WebServiceMessage;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 21/11/11
 * Time: 9:43 AM
 */
public class GenericSoapMessage implements WebServiceMessage {

    private final Source source;

    public GenericSoapMessage(Source source) {
        this.source = source;
    }

    @Override
    public Source getPayloadSource() {
        return source;
    }

    @Override
    public Result getPayloadResult() {
        throw new RuntimeException("This method is not implemented - it SHOULD NOT be used.");
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        Writer writer = new OutputStreamWriter(outputStream);
        String message = XmlUtils.sourceToXmlString(source);
        writer.write(message);
        writer.flush();
        writer.close();
    }

}
