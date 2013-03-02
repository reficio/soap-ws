/**
 * Copyright (c) 2012 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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
package org.reficio.ws.test;

import com.google.common.base.Preconditions;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.reficio.ws.SoapContext;
import org.reficio.ws.SoapException;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.core.WsdlParser;
import org.reficio.ws.common.ResourceUtils;
import org.reficio.ws.server.core.SoapServer;
import org.reficio.ws.server.responder.AutoResponder;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapRule implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base, description);
    }

    private Statement statement(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                SoapServer server = initServer(description.getAnnotation(Server.class), description.getTestClass());
                try {
                    base.evaluate();
                    return;
                } finally {
                    stopServer(server);
                }
            }
        };
    }

    private SoapServer initServer(Server annotation, Class testClass) {
        if (annotation == null) {
            return null;
        }
        validate(annotation);
        URL wsdlUrl = getWsdlUrl(annotation, testClass);
        WsdlParser parser = WsdlParser.parse(wsdlUrl);
        SoapBuilder builder = getBuilder(annotation, parser);
        SoapServer server = construct(annotation);
        AutoResponder responder = getAutoResponder(builder);
        registerService(server, annotation, responder);

        server.start();
        return server;
    }

    private SoapServer construct(Server annotation) {
        return SoapServer.builder()
                .httpPort(annotation.port())
                .build();
    }

    private void stopServer(SoapServer server) {
        if (server != null) {
            server.stop();
        }
    }

    private void registerService(SoapServer server, Server annotation, AutoResponder responder) {
        server.registerRequestResponder(annotation.path(), responder);
    }

    private AutoResponder getAutoResponder(SoapBuilder builder) {
        SoapContext context = SoapContext.builder()
                .exampleContent(true)
                .buildOptional(true)
                .alwaysBuildHeaders(true)
                .build();
        return new AutoResponder(builder, context);
    }

    private SoapBuilder getBuilder(Server server, WsdlParser parser) {
        SoapBuilder builder = null;
        try {
            builder = parser.binding(server.binding()).builder();
        } catch (SoapException ex) {
            // ignore
        }
        if (builder == null) {
            builder = parser.binding().localPart(server.binding()).builder();
        }
        Preconditions.checkNotNull(builder, "Binding not found");
        return builder;
    }

    private URL getWsdlUrl(Server server, Class testClass) {
        URL wsdlUrl = null;
        try {
            wsdlUrl = ResourceUtils.getResource(testClass, server.wsdl());
        } catch (IllegalArgumentException ex) {
            // ignore
        }
        if (wsdlUrl == null) {
            try {
                wsdlUrl = new URL(server.wsdl());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Wrong wsdl url", e);
            }
        }
        return wsdlUrl;
    }

    private void validate(Server server) {
        Preconditions.checkNotNull(server.wsdl(), "Wsdl url cannot be null");
        Preconditions.checkNotNull(server.binding(), "Binding name cannot be null");
        Preconditions.checkArgument(server.port() >= 0 && server.port() < 65535, "Port has to be in range [0, 655535]");
    }

}
