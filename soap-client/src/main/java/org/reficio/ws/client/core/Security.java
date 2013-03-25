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
package org.reficio.ws.client.core;

import org.reficio.ws.client.SoapClientException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.reficio.ws.client.core.SoapConstants.*;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
public class Security {

    private KeyStore trustStore;
    private char[] trustStorePassword;
    private String trustStoreType;

    private KeyStore keyStore;
    private char[] keyStorePassword;
    private String keyStoreType;

    private String authUsername;
    private String authPassword;
    private String authWorkstation;
    private String authDomain;

    private String authMethod;

    private boolean strictHostVerification;
    private String sslContextProtocol;

    // ----------------------------------------------------------------
    // PUBLIC API
    // ----------------------------------------------------------------
    public KeyStore getTrustStore() {
        return trustStore;
    }

    public char[] getTrustStorePassword() {
        return trustStorePassword;
    }

    public String getTrustStorePasswordAsString() {
        return trustStorePassword != null ? new String(trustStorePassword) : null;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public char[] getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getKeyStorePasswordAsString() {
        return keyStorePassword != null ? new String(keyStorePassword) : null;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public String getAuthWorkstation() {
        return authWorkstation;
    }

    public String getAuthDomain() {
        return authDomain;
    }

    public String getAuthMethod() {
        return authMethod;
    }

    public boolean isAuthEnabled() {
        return !authMethod.equals(AuthMethod.NONE.name());
    }

    public boolean isAuthBasic() {
        return authMethod.equals(AuthMethod.BASIC.name());
    }

    public boolean isAuthDigest() {
        return authMethod.equals(AuthMethod.DIGEST.name());
    }

    public boolean isAuthNtlm() {
        return authMethod.equals(AuthMethod.NTLM.name());
    }

    public boolean isAuthSpnego() {
        return authMethod.equals(AuthMethod.SPNEGO.name());
    }

    public boolean isStrictHostVerification() {
        return strictHostVerification;
    }

    public String getSslContextProtocol() {
        return sslContextProtocol;
    }

    // ----------------------------------------------------------------
    // BUILDER API
    // ----------------------------------------------------------------
    public static Builder builder() {
        return new Builder();
    }

    private Security() {
    }

    /**
     * Builder to construct a properly populated SoapClient
     */
    public static class Builder {

        private KeyStore trustStore;
        private URL trustStoreUrl;
        private String trustStoreType = JKS_KEYSTORE;
        private char[] trustStorePassword;

        private KeyStore keyStore;
        private URL keyStoreUrl;
        private String keyStoreType = JKS_KEYSTORE;
        private char[] keyStorePassword;

        private String authUsername;
        private String authPassword;
        private String authWorkstation;
        private String authDomain;

        private String authMethod = AuthMethod.NONE.name();

        private String sslContextProtocol = SSL_CONTEXT_PROTOCOL;
        private Boolean strictHostVerification = false;

        public Builder authBasic(String user, String password) {
            authUsername = checkNotNull(user);
            authPassword = checkNotNull(password);
            authMethod = AuthMethod.BASIC.name();
            return this;
        }

        public Builder authDigest(String user, String password) {
            authUsername = checkNotNull(user);
            authPassword = checkNotNull(password);
            authMethod = AuthMethod.DIGEST.name();
            return this;
        }

        public Builder authNtlm(String user, String password, String workstation, String domain) {
            authUsername = checkNotNull(user);
            authPassword = checkNotNull(password);
            authWorkstation = checkNotNull(workstation);
            authDomain = checkNotNull(domain);
            authMethod = AuthMethod.NTLM.name();
            return this;
        }

        public Builder authSpnego() {
            authMethod = AuthMethod.SPNEGO.name();
            return this;
        }

        /**
         * @param value Specifies the instance of the truststore to use in the SOAP communication. Null is not accepted.
         * @return builder
         */
        public Builder keyStore(KeyStore value) {
            checkNotNull(value);
            keyStore = value;
            return this;
        }

        /**
         * @param value Specifies the URL of the truststore to use in the SOAP communication. Null is not accepted.
         * @return builder
         */
        public Builder keyStoreUrl(URL value) {
            checkNotNull(value);
            keyStoreUrl = value;
            return this;
        }

        /**
         * @param value Specifies the URL of the truststore to use in the SOAP communication. Null is not accepted.
         * @return builder
         */
        public Builder keyStoreUrl(String value) {
            checkNotNull(value);
            try {
                keyStoreUrl = new URL(value);
                return this;
            } catch (MalformedURLException ex) {
                throw new SoapClientException(String.format("URL [%s] is malformed", value), ex);
            }
        }

        /**
         * @param value Specifies the type of the truststore. Null is not accepted.
         * @return builder
         */
        public Builder keyStoreType(String value) {
            checkNotNull(value);
            keyStoreType = value;
            return this;
        }

        /**
         * @param value truststore password. Null is accepted.
         * @return builder
         */
        public Builder keyStorePassword(String value) {
            if (value != null) {
                keyStorePassword = value.toCharArray();
            }
            return this;
        }

        /**
         * @param value Specifies the instance of the truststore to use in the SOAP communication. Null is not accepted.
         * @return builder
         */
        public Builder trustStore(KeyStore value) {
            checkNotNull(value);
            trustStore = value;
            return this;
        }


        /**
         * @param value Specifies the URL of the truststore to use in the SOAP communication. Null is not accepted.
         * @return builder
         */
        public Builder trustStoreUrl(URL value) {
            checkNotNull(value);
            trustStoreUrl = value;
            return this;
        }

        /**
         * @param value Specifies the URL of the truststore to use in the SOAP communication. Null is not accepted.
         * @return builder
         */
        public Builder trustStoreUrl(String value) {
            checkNotNull(value);
            try {
                trustStoreUrl = new URL(value);
                return this;
            } catch (MalformedURLException ex) {
                throw new SoapClientException(String.format("URL [%s] is malformed", value), ex);
            }
        }

        /**
         * @param value Specifies the type of the truststore. Null is not accepted.
         * @return builder
         */
        public Builder trustStoreType(String value) {
            checkNotNull(value);
            trustStoreType = value;
            return this;
        }

        /**
         * @param value truststore password. Null is accepted.
         * @return builder
         */
        public Builder trustStorePassword(String value) {
            if (value != null) {
                trustStorePassword = value.toCharArray();
            }
            return this;
        }

        /**
         * Enables strict host verification
         *
         * @param value strict host verification enables/disabled
         * @return builder
         */
        public Builder strictHostVerification(boolean value) {
            strictHostVerification = value;
            return this;
        }

        /**
         * @param value Specifies the SSL Context Protocol. By default it's SSLv3. Null is not accepted.
         * @return builder
         */
        public Builder sslContextProtocol(String value) {
            checkNotNull(value);
            sslContextProtocol = value;
            return this;
        }

        /**
         * Constructs properly populated soap client
         *
         * @return properly populated soap clients
         */
        public Security build() {
            Security security = new Security();

            security.keyStore = initKeyStore(keyStore, keyStoreUrl, keyStoreType, keyStorePassword);
            security.keyStoreType = keyStoreType;
            security.keyStorePassword = keyStorePassword;

            security.trustStore = initKeyStore(trustStore, trustStoreUrl, trustStoreType, trustStorePassword);
            security.trustStoreType = trustStoreType;
            security.trustStorePassword = trustStorePassword;

            security.sslContextProtocol = sslContextProtocol;
            security.strictHostVerification = strictHostVerification;

            security.authUsername = authUsername;
            security.authPassword = authPassword;
            security.authWorkstation = authWorkstation;
            security.authDomain = authDomain;
            security.authMethod = authMethod;

            return security;
        }

        private KeyStore initKeyStore(KeyStore keyStore, URL keyStoreUrl, String keyStoreType, char[] keyStorePassword) {
            boolean keyStorePropertiesDefined = keyStoreUrl != null || keyStoreType != null || keyStorePassword != null;
            if (keyStore != null && keyStorePropertiesDefined) {
                throw new SoapClientException("Specify either a keyStore | trustStore instance or properties required to load one " +
                        "(url, type, password)");
            }
            if (keyStoreUrl != null) {
                try {
                    InputStream in = keyStoreUrl.openStream();
                    KeyStore ks = KeyStore.getInstance(keyStoreType);
                    ks.load(in, keyStorePassword);
                    in.close();
                    return ks;
                } catch (GeneralSecurityException e) {
                    throw new SoapClientException("KeyStore setup failed", e);
                } catch (IOException e) {
                    throw new SoapClientException("KeyStore setup failed", e);
                }
            }
            return null;
        }
    }

}
