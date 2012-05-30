/*
 *  soapUI, copyright (C) 2004-2011 smartbear.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.centeractive.ws.builder.soap;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SchemaDefinitionWrapper {
    private SchemaTypeSystem schemaTypes;
    private SchemaTypeLoader schemaTypeLoader;

    private Definition definition;

    public SchemaDefinitionWrapper(Definition definition, String schemaURL) {
        this.definition = definition;
        loadSchemaTypes(new UrlSchemaLoader(schemaURL));
        System.err.println(schemaTypes.toString());
    }

    public SchemaTypeLoader getSchemaTypeLoader() {
        return schemaTypeLoader;
    }

    public SchemaTypeSystem getSchemaTypeSystem() {
        return schemaTypes;
    }

    public boolean hasSchemaTypes() {
        return schemaTypes != null;
    }

    public Collection<String> getDefinedNamespaces() throws Exception {
        Set<String> namespaces = new HashSet<String>();

        SchemaTypeSystem schemaTypes = getSchemaTypeSystem();
        if (schemaTypes != null) {
            namespaces.addAll(SchemaUtils.extractNamespaces(getSchemaTypeSystem(), true));
        }

        namespaces.add(getTargetNamespace());

        return namespaces;
    }

    public String getTargetNamespace() {
        return WsdlUtils.getTargetNamespace(definition);
    }

    public SchemaType findType(QName typeName) {
        return getSchemaTypeLoader().findType(typeName);
    }

    public void loadSchemaTypes(DefinitionLoader loader) {
        schemaTypes = SchemaUtils.loadSchemaTypes(loader.getBaseURI(), loader);
        schemaTypeLoader = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[]{schemaTypes,
                XmlBeans.getBuiltinTypeSystem()});
    }

}
