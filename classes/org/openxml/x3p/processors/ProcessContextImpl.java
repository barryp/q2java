package org.openxml.x3p.processors;

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
import org.openxml.x3p.*;


/**
 * Default implementation of {@link ProcessContext}. Processors can use this
 * implementation by creating an instance as required, or by extending this class.
 * This implementation is minimal and does not create any additional objects until
 * objects are placed in it. Processors should use the {@link
 * #ProcessContextImpl(Object)} constructor; if the processor is constructed as an
 * engine, it may opt to use the {@link #ProcessContextImpl()} constructor.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see ProcessContext
 * @see Processor
 */
public class ProcessContextImpl
	implements ProcessContext
{


	/**
	 * The object that activated this process. The activator may be
	 * used to with an external context, e.g. a Servlet or an EJB bean.
	 */
	private Object      _activator;


	/**
	 * Objects stored in this context with {@link #setObject}. This hashtable
	 * is created on demand when the first object is placed in it.
	 */
	private Hashtable   _objects;
	
	
	/**
	 * The locale associated with thie processor. Initially this is null.
	 */
	private Locale      _locale;



	/**
	 * Constructor should be used only when derived class is used as a
	 * processor engine.
	 */
	protected ProcessContextImpl()
	{
	}
	/**
	 * Constructor for a new context process, or when a processor extends this
	 * class.
	 *
	 * @return The activator object
	 */
	public ProcessContextImpl( Object activator )
	{
		_activator = activator;
	}
	public final Enumeration enumerate()
	{
		if ( _objects == null )
			_objects = new Hashtable();
		return _objects.keys();
	}
	public final Object getActivator()
	{
		return _activator;
	}
	public Locale getLocale()
	{
		if ( _locale != null )
			return _locale;
		else
			return Locale.getDefault();
	}
	public final Object getObject( String name )
	{
		if ( _objects == null )
			_objects = new Hashtable();
		return _objects.get( name );
	}
	public void setLocale( Locale newLocale )
	{
		_locale = newLocale;
	}
	public final void setObject( String name, Object object )
	{
		if ( object == null &&  _objects != null )
			_objects.remove( name );
		else
		if ( object != null )
		{
			if ( _objects == null )
				_objects = new Hashtable();
			_objects.put( name, object );
		}
	}
}