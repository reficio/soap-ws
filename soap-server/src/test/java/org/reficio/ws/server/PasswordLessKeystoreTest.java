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
package org.reficio.ws.server; /**
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

import org.junit.Ignore;
import org.junit.Test;
import org.reficio.ws.common.ResourceUtils;
import org.reficio.ws.server.core.SoapServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;

import static org.junit.Assert.assertNotNull;

/**
 * @author Tom Bujok
 * @since 1.0.0
 */
@Ignore // not supported yet
public class PasswordLessKeystoreTest {

    // helper method
    public void generateKeyLessKeystore() throws Exception {
        URL keyStoreUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/keystores", ".keystore");
        InputStream in = keyStoreUrl.openStream();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, "changeit".toCharArray());
        in.close();

        String path = keyStoreUrl.getFile().replace(".keystore", "keyless2.keystore");
        File file = new File(path);
        FileOutputStream out = new FileOutputStream(file);

        Certificate certificate = ks.getCertificate("john");
        assertNotNull(certificate);
        Key key = ks.getKey("john", "changeit".toCharArray());
        assertNotNull(key);

        KeyStore keyLess = KeyStore.getInstance(KeyStore.getDefaultType());
        keyLess.load(null);
        keyLess.setKeyEntry("tom", key, "".toCharArray(), new Certificate[] {certificate});
        keyLess.store(out, "".toCharArray());
    }

    @Test
    public void trustStoreUrl() throws Exception {
        URL keyStoreUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/keystores", "keyless.keystore");
        InputStream in = keyStoreUrl.openStream();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, null);
        in.close();

        Certificate certificate = ks.getCertificate("tom");
        assertNotNull(certificate);
        Key key = ks.getKey("tom", "".toCharArray());
        assertNotNull(key);
    }

    @Test
    public void testServerWithKeyLessKeystore_EmptyPwd() {
        URL keyStoreUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/keystores", "keyless.keystore");
        SoapServer server = SoapServer.builder()
                .httpsPort(9696)
                .keyStoreUrl(keyStoreUrl)
                .keyStorePassword("")
                .build();
        server.start();
        server.stop();
    }

    @Test(expected = SoapServerException.class)
    public void testServerWithKeyLessKeystore_NullPwd() {
        URL keyStoreUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/keystores", "keyless.keystore");
        SoapServer server = SoapServer.builder()
                .httpsPort(9696)
                .keyStoreUrl(keyStoreUrl)
                .keyStorePassword(null)
                .build();
        server.start();
        server.stop();
    }

    @Test(expected = SoapServerException.class)
    public void testServerWithKeyLessKeystore_WrongPwd() {
        URL keyStoreUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/keystores", "keyless.keystore");
        SoapServer server = SoapServer.builder()
                .httpsPort(9696)
                .keyStoreUrl(keyStoreUrl)
                .keyStorePassword("wrong_password")
                .build();
        server.start();
        server.stop();
    }

}
