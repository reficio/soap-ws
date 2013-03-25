/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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
package org.reficio.ws.it.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
public class SslTunnel {

    private final static Log log = LogFactory.getLog(SslTunnel.class);

    private final KeyStore trustStore;
    private final KeyStore keyStore;
    private final String keyStorePassword;
    private final int sourcePort;
    private final String targetHost;
    private final int targetPort;
    private final AtomicBoolean run = new AtomicBoolean(true);

    private SSLContext sslContext;
    private ServerSocket socket;
    private CountDownLatch latch = new CountDownLatch(3);

    private List<Socket> clients = new ArrayList<Socket>();

    public SslTunnel(KeyStore keyStore, String keyStorePassword, int sourcePort, String targetHost, int targetPort) {
        this.trustStore = null;
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.sourcePort = sourcePort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    public SslTunnel(KeyStore keyStore, String keyStorePassword, KeyStore trustStore, int sourcePort, String targetHost, int targetPort) {
        this.trustStore = trustStore;
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.sourcePort = sourcePort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    public void start() {
        try {
            sslContext = SSLContext.getInstance("SSLv3");
            KeyManager[] keyManagers = null;
            TrustManager[] trustManagers = null;

            if (keyStore != null) {
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
                X509KeyManager defaultKeyManager = (X509KeyManager) keyManagerFactory.getKeyManagers()[0];
                keyManagers = new KeyManager[]{defaultKeyManager};

            }
            if (trustStore != null) {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);
                X509TrustManager defaultTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
                trustManagers = new TrustManager[]{defaultTrustManager};
            }

            sslContext.init(keyManagers, trustManagers, new SecureRandom());

            SSLServerSocketFactory socketFactory = sslContext.getServerSocketFactory();
            socket = socketFactory.createServerSocket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(sourcePort));
            new ServerThread(socket, run).start();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    class ServerThread extends Thread {
        private final ServerSocket server;
        private final AtomicBoolean run;

        public ServerThread(ServerSocket server, AtomicBoolean run) {
            this.server = server;
            this.run = run;
        }

        public void run() {
            try {
                server.setSoTimeout(100);
            } catch (Exception e) {
            }
            while (run.get()) {
                try {
                    Socket client = server.accept();
                    if (client != null) {
                        clients.add(client);
                        new PipeThread(client, run).start();
                    }
                } catch (Exception e) {
                }
            }
            IOUtils.closeQuietly(server);
            latch.countDown();
        }
    }

    public void stop() {
        try {
            run.set(false);
            IOUtils.closeQuietly(socket);
            for(Socket client : clients) {
                IOUtils.closeQuietly(client);
            }
            latch.await();
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    class PipeThread extends Thread {
        private final Socket client;
        private final AtomicBoolean run;

        public PipeThread(Socket client, AtomicBoolean run) {
            this.client = client;
            this.run = run;
        }

        public void run() {
            try {
                final Socket target = new Socket(targetHost, targetPort);
                final InputStream ti = target.getInputStream();
                final OutputStream to = target.getOutputStream();
                target.setSoTimeout(100);

                final InputStream ci = client.getInputStream();
                final OutputStream co = client.getOutputStream();
                client.setSoTimeout(100);

                new Thread() {
                    public void run() {
                        try {
                            while (run.get()) {
                                try {
                                    int read = 0;
                                    byte[] buffer = new byte[4096];
                                    read = ti.read(buffer);
                                    if (read == -1) {
                                        break;
                                    }
                                    co.write(buffer, 0, read);
                                } catch (Exception ex) {
                                }
                            }
                            IOUtils.closeQuietly(client);
                        } finally {
                            latch.countDown();
                        }
                    }
                }.start();

                new Thread() {
                    public void run() {
                        try {
                            while (run.get()) {
                                try {
                                    int read = 0;
                                    byte[] buffer = new byte[4096];
                                    read = ci.read(buffer);
                                    if (read == -1) {
                                        break;
                                    }
                                    to.write(buffer, 0, read);
                                } catch (Exception ex) {
                                }
                            }
                        } finally {
                            IOUtils.closeQuietly(client);
                        }
                        latch.countDown();
                    }
                }.start();
            } catch (Exception ex) {
                latch.countDown();
                latch.countDown();
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

    }


}
