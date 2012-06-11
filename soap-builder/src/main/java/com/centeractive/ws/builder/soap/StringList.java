package com.centeractive.ws.builder.soap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
