package org.openxml.util;

/**
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.openxml.org/license/
 * 
 * THIS SOFTWARE AND DOCUMENTATION IS PROVIDED ON AN "AS IS" BASIS
 * WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED,
 * INCLUDING AND WITHOUT LIMITATION, WARRANTIES THAT THE SOFTWARE
 * AND DOCUMENTATION IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGING. SEE THE LICENSE FOR THE
 * SPECIFIC LANGUAGE GOVERNING RIGHTS AND LIMITATIONS UNDER THE
 * LICENSE.
 * 
 * The Initial Developer of this code under the License is
 * OpenXML.org. Portions created by OpenXML.org and/or Assaf Arkin
 * are Copyright (C) 1998, 1999 OpenXML.org. All Rights Reserved.
 */


import java.text.*;
import java.util.*;


public class Resources
{
	

	static
	{
		setLocale( Locale.getDefault() );
	}
	
	
	private static ResourceBundle   _messages;
	
	
	private static Hashtable        _formats;
	

	public static String format( String message, Object[] args )
	{
		MessageFormat   mf;
		String          msg;
		
		mf = (MessageFormat) _formats.get( message );
		if ( mf == null )
		{
			try
			{
				msg = _messages.getString( message );
			}
			catch ( MissingResourceException except )
			{
				return message;
			}
			mf = new MessageFormat( msg );
			_formats.put( message, msg );
		}
		return mf.format( args );
	}
	public static String format( String message, Object arg1 )
	{
		return format( message, new Object[] { arg1 } );
	}
	public static String format( String message, Object arg1, Object arg2 )
	{
		return format( message, new Object[] { arg1, arg2 } );
	}
	public static String format( String message, Object arg1, Object arg2, Object arg3 )
	{
		return format( message, new Object[] { arg1, arg2, arg3 } );
	}
	public static String message( String message )
	{
		try
		{
			return _messages.getString( message );
		}
		catch ( MissingResourceException except )
		{
			return message;
		}
	}
	public static void setLocale( Locale locale )
	{
		if ( locale == null )
			_messages = ResourceBundle.getBundle( "org.openxml.util.resources.messages" ); 
		else
			_messages = ResourceBundle.getBundle( "org.openxml.util.resources.messages", locale ); 
		_formats = new Hashtable();
	}
}