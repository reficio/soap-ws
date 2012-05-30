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

package com.centeractive.ws.builder.soap.protocol;


import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.*;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.List;

/**
 * Public behaviour for a SOAP Version
 * 
 * @author ole.matzura
 */

public interface SoapVersion
{
	public static final SoapVersion11 Soap11 = SoapVersion11.instance;
	public static final SoapVersion12 Soap12 = SoapVersion12.instance;

	public QName getEnvelopeQName();

	public QName getBodyQName();

	public QName getHeaderQName();

	public void validateSoapEnvelope(String soapMessage, List<XmlError> errors);

	public String getContentTypeHttpHeader(String encoding, String soapAction);

	public String getEnvelopeNamespace();

	public String getFaultDetailNamespace();

	public String getEncodingNamespace();

	public XmlObject getSoapEncodingSchema() throws XmlException, IOException;

	public XmlObject getSoapEnvelopeSchema() throws XmlException, IOException;

	/**
	 * Checks if the specified validation error should be ignored for a message
	 * with this SOAP version. (The SOAP-spec may allow some constructions not
	 * allowed by the corresponding XML-Schema)
	 */

	public boolean shouldIgnore(XmlValidationError xmlError);

	public String getContentType();

	public SchemaType getEnvelopeType();

	public SchemaType getFaultType();

	public String getName();

	/**
	 * Utilities
	 * 
	 * @author ole.matzura
	 */

	public static class Utils
	{
		public static SoapVersion getSoapVersionForContentType( String contentType, SoapVersion def )
		{
			if( StringUtils.isBlank(contentType) )
				return def;

			SoapVersion soapVersion = contentType.startsWith( SoapVersion.Soap11.getContentType() ) ? SoapVersion.Soap11
					: null;
			soapVersion = soapVersion == null && contentType.startsWith( SoapVersion.Soap12.getContentType() ) ? SoapVersion.Soap12
					: soapVersion;

			return soapVersion == null ? def : soapVersion;
		}
	}

	public String getSoapActionHeader(String soapAction);
}
