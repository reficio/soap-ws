/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.centeractive.ws.builder.core;

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
         * @return
         */
        public ContextBuilder exampleContent(boolean value) {
            this.exampleContent = value;
            return this;
        }

        /**
         * Specifies if to generate SOAP message type comments
         *
         * @param value
         * @return
         */
        public ContextBuilder typeComment(boolean value) {
            this.typeComment = value;
            return this;
        }

        /**
         * Specifies if to skip SOAP message comments
         *
         * @param value
         * @return
         */
        public ContextBuilder skipComments(boolean value) {
            this.skipComments = value;
            return this;
        }

        /**
         * Specifies if to generate content for elements marked as optional
         *
         * @param value
         * @return
         */
        public ContextBuilder buildOptional(boolean value) {
            this.buildOptional = value;
            return this;
        }

        /**
         * Specifies if to always build SOAP headers
         *
         * @param value
         * @return
         */
        public ContextBuilder alwaysBuildHeaders(boolean value) {
            this.alwaysBuildHeaders = value;
            return this;
        }

        /**
         * Builds populated context instance
         *
         * @return
         */
        public SoapContext build() {
            return new SoapContext(exampleContent, typeComment, skipComments,
                    buildOptional, alwaysBuildHeaders);
        }
    }

}
