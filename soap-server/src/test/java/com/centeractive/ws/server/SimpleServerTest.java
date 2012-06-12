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
package com.centeractive.ws.server;

import com.centeractive.ws.server.core.SoapServer;
import com.centeractive.ws.server.responder.RequestResponder;
import org.junit.Test;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.Source;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SimpleServerTest {

    public static boolean isPortAvailable(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

    public static int getFreePort() {
        for (int portToCheck = 20000; portToCheck < 21000; portToCheck++) {
            if (isPortAvailable(portToCheck)) {
                return portToCheck;
            }
        }
        throw new SoapServerException("Crazy stuff is happening, no free port available");
    }

    public static SoapServer getServer() {
        SoapServer server = SoapServer.builder()
                .httpPort(getFreePort())
                .build();
        return server;
    }

    @Test
    public void startStop() {
        int port = getFreePort();
        assertTrue(isPortAvailable(port));
        SoapServer server = SoapServer.builder()
                .httpPort(port)
                .build();
        server.start();
        assertFalse(isPortAvailable(port));
        server.stop();
        assertTrue(isPortAvailable(port));
    }

    @Test
    public void startStopDestroy() {
        SoapServer server = getServer();
        server.start();
        server.stop();
        server.destroy();
    }

    @Test(expected = RuntimeException.class)
    public void startStopDestroyCannotResurrect() {
        SoapServer server = getServer();
        server.start();
        server.stop();
        server.destroy();
        server.start();
    }

    @Test
    public void registerCheck() {
        String contextPath = "/test";
        SoapServer server = getServer();
        server.registerRequestResponder(contextPath, new RequestResponder() {
            @Override
            public Source respond(SoapMessage message) {
                return null;
            }
        });
        List<String> paths = server.getRegisteredContextPaths();
        assertEquals(paths.size(), 1);
        assertEquals(paths.toArray(new String[]{})[0], contextPath);
    }

    @Test
    public void unregisterCheck() {
        String contextPath = "/test";
        SoapServer server = getServer();
        server.registerRequestResponder(contextPath, new RequestResponder() {
            @Override
            public Source respond(SoapMessage message) {
                return null;
            }
        });
        server.unregisterRequestResponder(contextPath);
        List<String> paths = server.getRegisteredContextPaths();
        assertEquals(paths.size(), 0);
    }

    @Test(expected = ServiceRegistrationException.class)
    public void doubleRegister() {
        String contextPath = "/test";
        SoapServer server = getServer();
        server.registerRequestResponder(contextPath, new RequestResponder() {
            @Override
            public Source respond(SoapMessage message) {
                return null;
            }
        });
        server.registerRequestResponder(contextPath, new RequestResponder() {
            @Override
            public Source respond(SoapMessage message) {
                return null;
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void registerNullResponder() {
        String contextPath = "/test";
        SoapServer server = getServer();
        server.registerRequestResponder(contextPath, null);
    }

    @Test(expected = NullPointerException.class)
    public void registerNullContextPath() {
        SoapServer server = getServer();
        server.registerRequestResponder(null, new RequestResponder() {
            @Override
            public Source respond(SoapMessage message) {
                return null;
            }
        });
    }

    @Test(expected = ServiceRegistrationException.class)
    public void unregisterNotExisting() {
        String contextPath = "/test";
        SoapServer server = getServer();
        server.unregisterRequestResponder(contextPath);
    }

    @Test
    public void urlWithSpace() throws MalformedURLException, URISyntaxException {
        URL url = new File("/Users/tom/Keystore Location/SOAP/ssl/mykeystore").toURI().toURL();
        System.out.println(url.toString());
        System.out.println(url.toURI().getPath());
        System.out.println(url.toURI().toString());
    }

}
