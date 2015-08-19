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
package org.reficio.ws.client.ssl;

import org.apache.http.conn.ssl.*;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.ssl.*;
import org.apache.http.ssl.SSLContexts;
import org.reficio.ws.client.core.Security;

import javax.net.ssl.*;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
public class SSLUtils {

    public static X509TrustManager getTrustManager(KeyStore trustStore) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
    }

    public static X509KeyManager getKeyManager(KeyStore keyStore, char[] keyStorePassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword);
        return (X509KeyManager) keyManagerFactory.getKeyManagers()[0];
    }

    public static X509TrustManager getMultiTrustManager(X509TrustManager... managers) {
        List<X509TrustManager> managersList = new ArrayList<X509TrustManager>();
        for (X509TrustManager manager : managers) {
            managersList.add(manager);
        }
        return new MultiX509TrustManager(managersList);
    }

    public static SSLConnectionSocketFactory getMergedSocketFactory(Security securityOne, Security securityTwo) throws GeneralSecurityException {
        X509KeyManager keyManagerOne = getKeyManager(securityOne.getKeyStore(), securityOne.getKeyStorePassword());
        X509KeyManager keyManagerTwo = getKeyManager(securityTwo.getKeyStore(), securityTwo.getKeyStorePassword());

        X509TrustManager trustManager = getMultiTrustManager(
                getTrustManager(securityOne.getTrustStore()),
                getTrustManager(securityTwo.getTrustStore())
        );

        SSLContext context = SSLContext.getInstance(securityOne.getSslContextProtocol());
        boolean strictHostVerification = securityOne.isStrictHostVerification() && securityTwo.isStrictHostVerification();

        context.init(new KeyManager[]{keyManagerOne, keyManagerTwo}, new TrustManager[]{trustManager}, new SecureRandom());
        HostnameVerifier verifier = strictHostVerification ?
                new DefaultHostnameVerifier() : new NoopHostnameVerifier();
        return new SSLConnectionSocketFactory(context, verifier);
    }

    public static SSLConnectionSocketFactory getFactory(Security security) throws GeneralSecurityException {
        HostnameVerifier verifier = security.isStrictHostVerification() ?
                new DefaultHostnameVerifier() : new NoopHostnameVerifier();

        SSLContext context = SSLContexts.custom()
                        .useProtocol(security.getSslContextProtocol())
                        .setSecureRandom(new SecureRandom())
                        .loadKeyMaterial(security.getKeyStore(),security.getKeyStorePassword() != null ? security.getKeyStorePassword() : null)
                        .loadTrustMaterial(security.getTrustStore(), null)
                        .build();

        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(context, verifier);
        return factory;
    }


}
