package org.openxml.source.holders;

/**
 * org/openxml/source/holders/XCatalogFactory.java
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


import java.io.IOException;
import java.util.Hashtable;
import org.openxml.source.*;


/**
 * Implements an XCatalog cached factory. Catalogs are loaded and located
 * from XCatalog documents using the {@link #findCatalog} method. To reload
 * an updated document, use {@link #reloadCatalog}. Since catalogs are used
 * mainly as holder factories, the method {@link #asHolderFactory} turns
 * any catalog into a holder factory.
 * <P>
 * This factory uses {@link XCatalogImpl} internally. Documents are located
 * with {@link Source} so they may be mapped with a previously loaded catalog.
 * The {@link #asHolderFactory} method also supported catalogs implemented with
 * different classes.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see XCatalog
 */
public final class XCatalogFactory
{


	/**
	 * A table of all the catalogs loaded into memory mapped by their URIs.
	 * Once a catalog has been loaded once, there is no need to load it again,
	 * and it may be looked upon in this cache, regardless of its role.
	 */
	private static Hashtable    _catalogs;


	/**
	 * Returns an {@link HolderFactory} that can be registered as a factory
	 * holder and will map public identifiers based on the supplied catalog.
	 * This method is used by {@link HolderFinder#useCatalog} to register
	 * catalogs as holder factories.
	 * 
	 * @param catalog The XCatalog
	 * @return Holder factory for this catalog
	 */
	public static HolderFactory asHolderFactory( XCatalog catalog )
	{
		if ( catalog instanceof HolderFactory )
			return (HolderFactory) catalog;
		return new XCatalogHolderFactoryAdaptor( catalog );
	}
	/**
	 * Locates and returns the named catalog. If the catalog has been loaded
	 * before, it is retrieved from the cache. If the catalog is requested for
	 * the first time, the URI is used to obtain the document and presuming that
	 * the document is a valid XCatalog, a working catalog will be created,
	 * cached and returned. If the document is not found or could not be read,
	 * null is returned but not exceptions are issued.
	 * <P>
	 * This method is the preferred way to locate or create a new XCatalog from
	 * an XCatalog document. That catalog may be used in any role, either as
	 * primary, extending or delegated.
	 * 
	 * @param uri The XCatalog document URI
	 * @return A catalog
	 */
	public synchronized static XCatalog findCatalog( String uri )
	{
		XCatalog    catalog;
	 
		if ( _catalogs != null )
		{
			catalog = (XCatalog) _catalogs.get( uri );
			if ( catalog != null )
				return catalog;
		}
		return reloadCatalog( uri );
	}
	/**
	 * Locates and returns the named catalog. This method is similar to {@link
	 * #findCatalog} but the document is not looked for in the cache, but rather
	 * reloaded anew. This method may be used to update the cache when a document
	 * has been updated.
	 * 
	 * @param uri The XCatalog document URI
	 * @return A new catalog
	 */
	public synchronized static XCatalog reloadCatalog( String uri )
	{
		XCatalog    catalog;
	 
		if ( _catalogs == null )
			_catalogs = new Hashtable();
		try
		{
			catalog = new XCatalogImpl( uri );
			_catalogs.put( uri, catalog );
			return catalog;
		}
		catch ( IOException except )
		{
		}
		return null;
	}
}