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
package com.centeractive.ws;

/**
 * Specifies the context of the SOAP message generation.
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapContext {

    public final static SoapContext DEFAULT = SoapContext.builder().build();

    private final boolean typeComments;
    private final boolean valueComments;
    private final boolean exampleContent;
    private final boolean buildOptional;
    private final boolean alwaysBuildHeaders;

    /**
     * Constructor mainly for SpringFramework purposes, in any other case use the fluent builder interface;
     * #see builder() method
     *
     * @param exampleContent
     * @param typeComments
     * @param valueComments
     * @param buildOptional
     * @param alwaysBuildHeaders
     */
    public SoapContext(boolean exampleContent, boolean typeComments, boolean valueComments,
                       boolean buildOptional, boolean alwaysBuildHeaders) {
        this.exampleContent = exampleContent;
        this.typeComments = typeComments;
        this.valueComments = valueComments;
        this.buildOptional = buildOptional;
        this.alwaysBuildHeaders = alwaysBuildHeaders;
    }

    public boolean isBuildOptional() {
        return buildOptional;
    }

    public boolean isAlwaysBuildHeaders() {
        return alwaysBuildHeaders;
    }

    public boolean isExampleContent() {
        return exampleContent;
    }

    public boolean isTypeComments() {
        return typeComments;
    }

    public boolean isValueComments() {
        return valueComments;
    }

    public static ContextBuilder builder() {
        return new ContextBuilder();
    }

    public static class ContextBuilder {
        private boolean exampleContent = false;
        private boolean typeComments = false;
        private boolean valueComments = true;
        private boolean buildOptional = true;
        private boolean alwaysBuildHeaders = true;

        /**
         * Specifies if to generate example SOAP message content
         *
         * @param value
         * @return builder
         */
        public ContextBuilder exampleContent(boolean value) {
            this.exampleContent = value;
            return this;
        }

        /**
         * Specifies if to generate SOAP message type comments
         *
         * @param value
         * @return builder
         */
        public ContextBuilder typeComments(boolean value) {
            this.typeComments = value;
            return this;
        }

        /**
         * Specifies if to skip SOAP message comments
         *
         * @param value
         * @return builder
         */
        public ContextBuilder valueComments(boolean value) {
            this.valueComments = value;
            return this;
        }

        /**
         * Specifies if to generate content for elements marked as optional
         *
         * @param value
         * @return builder
         */
        public ContextBuilder buildOptional(boolean value) {
            this.buildOptional = value;
            return this;
        }

        /**
         * Specifies if to always build SOAP headers
         *
         * @param value
         * @return builder
         */
        public ContextBuilder alwaysBuildHeaders(boolean value) {
            this.alwaysBuildHeaders = value;
            return this;
        }

        /**
         * Builds populated context instance
         *
         * @return fully populated soap context
         */
        public SoapContext build() {
            return new SoapContext(exampleContent, typeComments, valueComments,
                    buildOptional, alwaysBuildHeaders);
        }
    }

}
