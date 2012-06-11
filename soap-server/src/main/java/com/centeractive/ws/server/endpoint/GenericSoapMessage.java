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
package com.centeractive.ws.server.endpoint;

import com.centeractive.ws.builder.soap.XmlUtils;
import org.springframework.ws.WebServiceMessage;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @author Tom Bujok
 * @since 1.0.0
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
