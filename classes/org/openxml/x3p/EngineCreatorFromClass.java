package org.openxml.x3p;

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


import java.util.*;
import java.lang.reflect.*;
import org.w3c.dom.*;
import org.openxml.util.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public class EngineCreatorFromClass
	implements ProcessorEngineCreator
{


	private static Hashtable    _creators = new Hashtable();


	private Constructor         _constructor;


	private EngineCreatorFromClass( Class engineClass )
	{
		if ( engineClass == null )
			throw new NullPointerException( Resources.format( "Error001", "engineClass" ) );
		setConstructor( engineClass );
	}
	private EngineCreatorFromClass( String engineClass )
		throws ClassNotFoundException
	{
		Class   cls;

		if ( engineClass == null )
			throw new NullPointerException( Resources.format( "Error001", "engineClass" ) );
		try
		{
			setConstructor( Class.forName( engineClass ) );
		}
		catch  ( ClassNotFoundException except )
		{
			throw new IllegalArgumentException( Resources.format( "Error002", engineClass ) );
		}
	}
	public ProcessorEngine createEngine( ProcessContext ctx )
	{
		try
		{
			return (ProcessorEngine) _constructor.newInstance( new Object[] { ctx } );
		}
		catch ( Exception except )
		{
			org.openxml.util.Log.error( except );
			return null;
		}
	}
	public static ProcessorEngineCreator fromClass( Class engineClass )
	{
		EngineCreatorFromClass    creator;

		if ( _creators != null )
		{
			creator = (EngineCreatorFromClass) _creators.get( engineClass.getName() );
			if ( creator != null )
				return creator;
		}
		creator = new EngineCreatorFromClass( engineClass );
		_creators.put( engineClass.getName(), creator );
		return creator;
	}
	public static ProcessorEngineCreator fromClass( String engineClass )
		throws ClassNotFoundException
	{
		EngineCreatorFromClass  creator;

		if ( _creators != null )
		{
			creator = (EngineCreatorFromClass) _creators.get( engineClass );
			if ( creator != null )
				return creator;
		}
		creator = new EngineCreatorFromClass( engineClass );
		_creators.put( engineClass, creator );
		return creator;
	}
	private void setConstructor( Class engineClass )
	{

		if ( ! Modifier.isPublic( engineClass.getModifiers() ) )
			throw new IllegalArgumentException( Resources.format( "Processor004", engineClass ) );
		if ( ! ProcessorEngine.class.isAssignableFrom( engineClass ) )
			throw new IllegalArgumentException( Resources.format( "Processor005", engineClass ) );
		try
		{
			_constructor = engineClass.getConstructor( new Class[] { ProcessContext.class } );
		}
		catch ( NoSuchMethodException except )
		{
			throw new IllegalArgumentException( Resources.format( "Processor006", engineClass ) );
		}
		if ( ! Modifier.isPublic( _constructor.getModifiers() ) )
			throw new IllegalArgumentException( Resources.format( "Processor007", engineClass ) );
	}
	public String toString()
	{
		return _constructor.getClass().getName();
	}
}