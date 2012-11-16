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

    private final boolean exampleContent;
    private final boolean typeComment;
    private final boolean skipComments;
    private final boolean buildOptional;
    private final boolean alwaysBuildHeaders;

    private SoapContext(boolean exampleContent, boolean typeComment, boolean skipComments,
                        boolean buildOptional, boolean alwaysBuildHeaders) {
        this.exampleContent = exampleContent;
        this.typeComment = typeComment;
        this.skipComments = skipComments;
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

    public boolean isTypeComment() {
        return typeComment;
    }

    public boolean isSkipComments() {
        return skipComments;
    }

    public static ContextBuilder builder() {
        return new ContextBuilder();
    }

    public static class ContextBuilder {
        private boolean exampleContent = false;
        private boolean typeComment = false;
        private boolean skipComments = true;
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
        public ContextBuilder typeComment(boolean value) {
            this.typeComment = value;
            return this;
        }

        /**
         * Specifies if to skip SOAP message comments
         *
         * @param value
         * @return builder
         */
        public ContextBuilder skipComments(boolean value) {
            this.skipComments = value;
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
            return new SoapContext(exampleContent, typeComment, skipComments,
                    buildOptional, alwaysBuildHeaders);
        }
    }

}
