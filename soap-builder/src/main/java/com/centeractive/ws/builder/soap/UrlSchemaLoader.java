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
package com.centeractive.ws.builder.soap;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.net.URL;

public class UrlSchemaLoader implements SchemaLoader, DefinitionLoader
{
	private String baseURI;

	public UrlSchemaLoader(String baseURI)
	{
		this.baseURI = baseURI;
	}

	public XmlObject loadXmlObject( String wsdlUrl, XmlOptions options ) throws Exception
	{
		return XmlUtils.createXmlObject(new URL(wsdlUrl), options);
	}

	public String getBaseURI()
	{
		return baseURI;
	}

    public void setProgressInfo(String info) {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isAborted() {
        throw new RuntimeException("Not Implemented");
    }

    public boolean abort() {
        throw new RuntimeException("Not Implemented");
    }

    public void setNewBaseURI(String uri) {
        throw new RuntimeException("Not Implemented");
    }

    public String getFirstNewURI() {
        throw new RuntimeException("Not Implemented");
    }
}
