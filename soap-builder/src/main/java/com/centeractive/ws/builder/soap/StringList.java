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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * This class was extracted from the soapUI code base by centeractive ag in June 2012.
 * The main reason behind the extraction was to separate the code that is responsible
 * for the generation of the SOAP messages from the rest of the soapUI's code that is
 * tightly coupled with other modules, such as soapUI's graphical user interface, etc.
 * The goal was to create an open-source java project whose main responsibility is to
 * handle SOAP message generation and SOAP transmission purely on an XML level.
 * <br/>
 * centeractive ag would like to express strong appreciation to SmartBear Software and
 * to the whole team of soapUI's developers for creating soapUI and for releasing its
 * source code under a free and open-source licence. centeractive ag extracted and
 * modifies some parts of the soapUI's code in good faith, making every effort not
 * to impair any existing functionality and to supplement it according to our
 * requirements, applying best practices of software design.
 *
 * Changes done:
 * - changing location in the package structure
 * - removal of dependencies and code parts that are out of scope of SOAP message generation
 * - minor fixes to make the class compile out of soapUI's code base
 */

public class StringList extends ArrayList<String>
{
	public StringList()
	{
		super();
	}

	public StringList(int initialCapacity)
	{
		super( initialCapacity );
	}

	public StringList(String[] strings)
	{
		super( strings == null ? new StringList() : Arrays.asList( strings ) );
	}

	public StringList(Object[] objects)
	{
		super();

		if( objects != null )
		for( Object object : objects )
			add( object == null ? null : object.toString() );
	}

	public StringList(Collection<?> objects)
	{
		super();

		if( objects != null )
		for( Object object : objects )
			add( object == null ? null : object.toString() );
	}

	public StringList(String paramStr)
	{
		this();
		add( paramStr );
	}

	public void addAll( String[] strings )
	{
		if( strings != null && strings.length > 0 )
		addAll( Arrays.asList( strings ) );
	}

	public String[] toStringArray()
	{
		return toArray( new String[size()] );
	}

//	public static StringList fromXml( String value ) throws XmlException
//	{
//		return StringUtils.isNullOrEmpty( value ) || value.equals( "<xml-fragment/>" ) ? new StringList()
//				: new StringList( StringListConfig.Factory.parse( value ).getEntryList() );
//	}
//
//	public String toXml()
//	{
//		StringListConfig config = StringListConfig.Factory.newInstance();
//		config.setEntryArray( toStringArray() );
//		return config.xmlText();
//	}

	public boolean containsValue( String value )
	{
		for( String stringElement : this )
		{
			if( stringElement.contains( value ) )
			{
				return true;
			}
		}
		return false;
	}

}
