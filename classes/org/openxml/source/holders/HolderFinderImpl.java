package org.openxml.source.holders;

/**
 * org/openxml/source/holders/HolderFinderImpl.java
 *
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.openxml.org/license.html
 *
 * THIS SOFTWARE IS DISTRIBUTED ON AN "AS IS" BASIS WITHOUT WARRANTY
 * OF ANY KIND, EITHER EXPRESSED OR IMPLIED. THE INITIAL DEVELOPER
 * AND ALL CONTRIBUTORS SHALL NOT BE LIABLE FOR ANY DAMAGES AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. SEE THE LICENSE FOR THE SPECIFIC LANGUAGE GOVERNING
 * RIGHTS AND LIMITATIONS UNDER THE LICENSE.
 *
 * The Initial Developer of this code under the License is Assaf Arkin.
 * Portions created by Assaf Arkin are Copyright (C) 1998, 1999.
 * All Rights Reserved.
 */


import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.openxml.util.Log;
import org.openxml.source.*;


/**
 * Implementation for a holder finder mechanism. Used for finding suitable holders
 * for document sources, and for registering new holder factories.
 * <P>
 * The following factories are registered by default:
 * <UL>
 * <LI>{@link URLHolderFactoryImpl} handles all HTTP/FTP and other network URLs
 * <LI>{@link FileHolderFactoryImpl} handles all file system sources, the URI
 *  being <TT>file:/<path_file></TT>)
 * <LI>{@link JARHolderFactoryImpl} handles files contained in JARs, the URI
 *  being <TT>jar:<jar_url>!<path_file></TT>
 * <LI>{@link ResHolderFactoryImpl} handles resources loaded using the
 *  default class loader, the URI being <TT>res:/<path_file>}</TT>
 * </UL>
 * This is a singleton implementation. Only a single instance can be created by
 * calling the {@link #getHolderFinder} method.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see HolderFinder
 * @see HolderFactory
 * @see XCatalog
 */
public final class HolderFinderImpl
	implements HolderFinder
{


	/**
	 * Holds a list of all registered factories. Factories accessed in the
	 * reverse order of registration, giving precedence to the last registered
	 * factory. This allows the application to implement new specific factories.
	 */
	private Vector              _factories = new Vector();


	/**
	 * Implements a simple cache for storing holders and the parsed
	 * documents. The source URI is used as the key.
	 */
	private Hashtable           _cache = new Hashtable();


	/**
	 * Holds a singleton factory finder.
	 */
	private static HolderFinder _finder;


	/**
	 * Private constructor assured that this cannot be created directly.
	 */
	private HolderFinderImpl()
	{
	}
	public Holder findHolder( Source source )
	{
		return findHolder( source, false );
	}
	public synchronized Holder findHolder( Source source, boolean refresh )
	{
		int       i;
		Holder    holder;

		holder = null;
		// If not refresh, try fetching the holder from the cache first.
		// If the source has been modified since last accessed, refresh the cache.
		if ( ! refresh )
		{
			holder = (Holder) _cache.get( source.toString() );
			if ( holder != null && holder.hasModified() )
			{
				Log.debug( "HolderFinder.findHolder: Source [" + source.toString() + "] has been modified -- removed from cache" );
				_cache.remove( source.toString() );
				holder = null;
			}
			else
			if ( holder != null )
				Log.debug( "HolderFinder.findHolder: Source [" + source.toString() + "] has been located -- returned from cache" );

		}
		// Request a suitable Holder for this url from one of the installed
		// factories. If a source can be found and is cacheable, cache it.
		if ( holder == null )
		{
			// Queries all the factories for an Holder. One of them is bound
			// to produce a usable holder that may be returned. The first holder
			// returned is used and the factories are searched in reverse order.
			i = _factories.size();
			while ( holder == null && i -- > 0 )
			{
				try
				{
					holder = ( (HolderFactory) _factories.elementAt( i ) ).newHolder( source );
				}
				catch ( Exception except )
				{
					// Just ignore any exception thrown and proceed with the
					// next holder in line.
					Log.debug( "HolderFinder.findHolder: Exception occured looking for holder" );
					Log.debug( except );
				}
			}
			if ( holder != null )
			{
				if ( holder.canCache() )
				{
					_cache.put( source.toString(), holder );
					Log.debug( "HolderFinder.findHolder: Source [" + source.toString() + "] has been located -- cached and holder returned" );
				}
				else
					Log.debug( "HolderFinder.findHolder: Source [" + source.toString() + "] has been located -- holder returned" );
			}
			else
				Log.debug( "HolderFinder.findHolder: Source [" + source.toString() + "] could not be located" );
		}
		return holder;
	}
	/**
	 * Returns a singleton holder finder with default factories already registered.
	 * This is the only way to achieve an instance of this implementation.
	 * 
	 * @return An holder finder
	 */
	public static HolderFinder getHolderFinder()
	{
		if ( _finder == null )
		{
			_finder = new HolderFinderImpl();
			_finder.registerFactory( new URLHolderFactoryImpl() ); 
			_finder.registerFactory( new FileHolderFactoryImpl() );
			_finder.registerFactory( new ResHolderFactoryImpl() );
			_finder.registerFactory( new JARHolderFactoryImpl() );
		}
		return _finder;
	}
	public synchronized void registerFactory( HolderFactory factory )
	{
		if ( ! _factories.contains( factory ) )
			_factories.addElement( factory );
	}
	public synchronized void useCatalog( String uri )
	{
		XCatalog    catalog;

		catalog = XCatalogFactory.findCatalog( uri );
		if ( catalog != null )
			registerFactory( XCatalogFactory.asHolderFactory( catalog ) );
	}
}