package com.centeractive.ws.server;

import com.centeractive.ws.server.core.SoapServer;
import com.centeractive.ws.server.responder.RequestResponder;
import org.junit.Test;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.Source;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Copyright (c) centeractive ag, Inc. All Rights Reserved.
 *
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 22/11/11
 * Time: 8:47 PM
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
        throw new RuntimeException("Crazy stuff is happening, no free port available");
    }

    public static SoapServer getServer() {
        SoapServer server = SoapServer.builder()
                .httpPort(getFreePort())
                .create();
        return server;
    }

    @Test
    public void startStop() {
        int port = getFreePort();
        assertTrue(isPortAvailable(port));
        SoapServer server = SoapServer.builder()
                .httpPort(port)
                .create();
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


}
