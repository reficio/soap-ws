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

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
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

    public static SSLSocketFactory getMergedSocketFactory(org.reficio.ws.client.core.Security securityOne, Security securityTwo) throws GeneralSecurityException {
        X509KeyManager keyManagerOne = getKeyManager(securityOne.getKeyStore(), securityOne.getKeyStorePassword());
        X509KeyManager keyManagerTwo = getKeyManager(securityTwo.getKeyStore(), securityTwo.getKeyStorePassword());

        X509TrustManager trustManager = getMultiTrustManager(
                getTrustManager(securityOne.getTrustStore()),
                getTrustManager(securityTwo.getTrustStore())
        );

        SSLContext context = SSLContext.getInstance(securityOne.getSslContextProtocol());
        boolean strictHostVerification = securityOne.isStrictHostVerification() && securityTwo.isStrictHostVerification();

        context.init(new KeyManager[]{keyManagerOne, keyManagerTwo}, new TrustManager[]{trustManager}, new SecureRandom());
        X509HostnameVerifier verifier = strictHostVerification ?
                SSLSocketFactory.STRICT_HOSTNAME_VERIFIER : SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        return new SSLSocketFactory(context, verifier);
    }

    public static SSLSocketFactory getFactory(Security security) throws GeneralSecurityException {
        X509HostnameVerifier verifier = security.isStrictHostVerification() ?
                SSLSocketFactory.STRICT_HOSTNAME_VERIFIER : SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        SSLSocketFactory socketFactory = new SSLSocketFactory(security.getSslContextProtocol(),
                security.getKeyStore(), security.getKeyStorePasswordAsString(),
                security.getTrustStore(), new SecureRandom(), null, verifier);
        return socketFactory;
    }


}
