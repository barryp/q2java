package org.openxml.source.holders;

/**
 * org/openxml/source/holders/XCatalogHolderFactoryAdaptor.java
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


import org.openxml.source.*;


/**
 * Adaptor to map an {@link XCatalog} behave as an {@link HolderFactory}.
 * Not necessary when using {@link XCatalogImpl}, but when using other
 * classes derived from {@link XCatalog}.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see XCatalog
 * @see XCatalogFactory
 * @see HolderFactory
 */
final class XCatalogHolderFactoryAdaptor
	implements HolderFactory
{
	
	
	/**
	 * Holdes the {@link XCatalog} to which this adapter will forward all
	 * mapping and remapping requests.
	 */
	private XCatalog        _catalog;


	/**
	 * Constructor requires an {@link XCatalog}.to which this adapter will forward
	 * all mapping and remapping requests.
	 * 
	 * @param catalog The XCatalog
	 */
	XCatalogHolderFactoryAdaptor( XCatalog catalog )
	{
		if ( catalog == null )
			throw new NullPointerException( "Argument 'catalog' is null." );
		_catalog = catalog;
	}
	public Holder newHolder( Source source )
	{
		String  uri;
		
		// Attempt to map the source.
		uri = _catalog.mapSource( source.getPublicId(), source.getURI() );
		// URI might be a mapping of the public identifier, or it might be the
		// unmapped system identifier. In the latter case, calling findHolder()
		// again will enter an infinite loop as URI is mapped into itself.
		// Avoid that possibility.
		if ( uri != null && ! uri.equals( source.getURI() ) )
		{
			// Re-locate the holder but now with a new URI
			source = new SourceImpl( uri, null, source.getEncoding(), source.getDocClass() );
			return HolderFinderImpl.getHolderFinder().findHolder( source, false );
		}
		// No luck, no mapping, keep on with next holder factory.
		return null;
	}
}