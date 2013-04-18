/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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
