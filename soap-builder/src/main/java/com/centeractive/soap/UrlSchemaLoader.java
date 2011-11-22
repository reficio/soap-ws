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

package com.centeractive.soap;

import org.apache.commons.lang.NotImplementedException;
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
        throw new NotImplementedException("");
    }

    public boolean isAborted() {
        throw new NotImplementedException("");
    }

    public boolean abort() {
        throw new NotImplementedException("");
    }

    public void setNewBaseURI(String uri) {
        throw new NotImplementedException("");
    }

    public String getFirstNewURI() {
        throw new NotImplementedException("");
    }
}
