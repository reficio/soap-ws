package org.reficio.ws.legacy;

import org.apache.xmlbeans.SchemaTypeLoader;

import javax.wsdl.Binding;
import javax.wsdl.Definition;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
class WsdlContext {

    private final SoapMessageBuilder builder;
    private final Binding binding;

    WsdlContext(SoapMessageBuilder builder, Binding binding) {
        this.builder = builder;
        this.binding = binding;
    }

    Definition getDefinition() {
        return builder.getDefinition();
    }

    SoapVersion getSoapVersion() {
        return SoapMessageBuilder.getSoapVersion(binding);
    }

    boolean hasSchemaTypes() {
        return builder.getSchemaDefinitionWrapper().hasSchemaTypes();
    }

    SchemaTypeLoader getSchemaTypeLoader() {
        return builder.getSchemaDefinitionWrapper().getSchemaTypeLoader();
    }

}
