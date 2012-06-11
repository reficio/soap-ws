package com.centeractive.ws.builder.core;

/**
 * @author Tom Bujok
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

        public ContextBuilder exampleContent(boolean value) {
            this.exampleContent = value;
            return this;
        }

        public ContextBuilder typeComment(boolean value) {
            this.typeComment = value;
            return this;
        }

        public ContextBuilder skipComments(boolean value) {
            this.skipComments = value;
            return this;
        }

        public ContextBuilder buildOptional(boolean value) {
            this.buildOptional = value;
            return this;
        }

        public ContextBuilder alwaysBuildHeaders(boolean value) {
            this.alwaysBuildHeaders = value;
            return this;
        }

        public SoapContext create() {
            return new SoapContext(exampleContent, typeComment, skipComments,
                    buildOptional, alwaysBuildHeaders);
        }
    }

}
