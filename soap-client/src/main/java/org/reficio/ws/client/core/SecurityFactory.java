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
package org.reficio.ws.client.core;

import java.security.KeyStore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.reficio.ws.client.core.SoapConstants.AuthMethod;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
public class SecurityFactory {

    private KeyStore trustStore;
    private String trustStoreUrl;
    private String trustStorePassword;
    private String trustStoreType;

    private KeyStore keyStore;
    private String keyStoreUrl;
    private String keyStorePassword;
    private String keyStoreType;

    private String authUsername;
    private String authPassword;
    private String authWorkstation;
    private String authDomain;
    private String authMethod;

    private Boolean strictHostVerification;
    private String sslContextProtocol;

    public KeyStore getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(KeyStore trustStore) {
        this.trustStore = checkNotNull(trustStore);
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = checkNotNull(trustStorePassword);
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = checkNotNull(trustStoreType);
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = checkNotNull(keyStore);
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = checkNotNull(keyStorePassword);
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = checkNotNull(keyStoreType);
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = checkNotNull(authUsername);
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = checkNotNull(authPassword);
    }

    public String getAuthWorkstation() {
        return authWorkstation;
    }

    public void setAuthWorkstation(String authWorkstation) {
        this.authWorkstation = checkNotNull(authWorkstation);
    }

    public String getAuthDomain() {
        return authDomain;
    }

    public void setAuthDomain(String authDomain) {
        this.authDomain = checkNotNull(authDomain);
    }

    public String getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = checkNotNull(authMethod).toUpperCase().trim();
    }

    public Boolean getStrictHostVerification() {
        return strictHostVerification;
    }

    public void setStrictHostVerification(Boolean strictHostVerification) {
        this.strictHostVerification = checkNotNull(strictHostVerification);
    }

    public String getSslContextProtocol() {
        return sslContextProtocol;
    }

    public void setSslContextProtocol(String sslContextProtocol) {
        this.sslContextProtocol = checkNotNull(sslContextProtocol);
    }

    public Security create() {
        Security.Builder builder = Security.builder();

        configureKeyStore(builder);
        configureTrustStore(builder);
        configureAuthentication(builder);
        configureTransport(builder);

        return builder.build();
    }

    private void configureKeyStore(Security.Builder builder) {
        if (keyStore != null) {
            builder.keyStore(keyStore);
        }
        if (keyStoreUrl != null) {
            builder.keyStoreUrl(keyStoreUrl);
        }
        if (keyStorePassword != null) {
            builder.keyStorePassword(keyStorePassword);
        }
        if (keyStoreType != null) {
            builder.keyStoreType(keyStoreType);
        }
    }

    private void configureTrustStore(Security.Builder builder) {
        if (trustStore != null) {
            builder.trustStore(trustStore);
        }
        if (trustStoreUrl != null) {
            builder.trustStoreUrl(trustStoreUrl);
        }
        if (trustStorePassword != null) {
            builder.trustStorePassword(trustStorePassword);
        }
        if (trustStoreType != null) {
            builder.trustStoreType(trustStoreType);
        }
    }

    private void configureAuthentication(Security.Builder builder) {
        if(authMethod != null) {
            AuthMethod method = AuthMethod.valueOf(authMethod);
            if(method.equals(AuthMethod.BASIC)) {
                builder.authBasic(authUsername, authPassword);
            } else if(method.equals(AuthMethod.DIGEST)) {
                builder.authDigest(authUsername, authPassword);
            } else if(method.equals(AuthMethod.NTLM)) {
                builder.authNtlm(authUsername, authPassword, authWorkstation, authDomain);
            } else if(method.equals(AuthMethod.SPNEGO)) {
                builder.authSpnego();
            }
        }
    }

    private void configureTransport(Security.Builder builder) {
        if (strictHostVerification != null) {
            builder.strictHostVerification(strictHostVerification);
        }
        if (sslContextProtocol != null) {
            builder.sslContextProtocol(sslContextProtocol);
        }
    }

}
