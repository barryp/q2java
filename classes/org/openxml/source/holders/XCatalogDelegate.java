package org.openxml.source.holders;

/**
 * org/openxml/source/holders/XCatalogImpl.java
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
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.*;
import org.openxml.util.Log;
import org.openxml.source.*;


/**
 * A delegate is another XCatalog which will perform mapping when this catalog
 * is exhuasted, if and only if, the public identifier begins with the delegate
 * prefix. The delegate prefix must be at least one character, otherwise specify
 * the catalog as extended. The delegate XCatalog is loaded from the factory and
 * may also be used as an extending catalog or by itself.
 */
class XCatalogDelegate
{
	

	/**
	 * Holds the public identifier prefix.
	 */
	private String      _prefix;
	
	
	/**
	 * Holds the delagating XCatalog.
	 */
	private XCatalog    _catalog;
	
	
	/**
	 * Constructs a new delegate to handle public identifiers with the given
	 * prefix using the supplied XCatalog.
	 * 
	 * @param prefix The public identifier prefix
	 * @param catalog The delegating XCatalog
	 */
	XCatalogDelegate( String prefix, XCatalog catalog )
	{
		if ( prefix == null || catalog == null )
			throw new NullPointerException( "Argument 'prefix' or 'catalog' is null." );
		if ( prefix.length() == 0 )
			throw new IllegalArgumentException( "Argument 'prefix' must contain at least one character." );
		_prefix = prefix;
		_catalog = catalog;
	}
	/**
	 * Returns true if this delegate's prefix matches the public identifier.
	 */
	boolean doesMatch( String publicId )
	{
		return ( publicId.startsWith( _prefix ) );
	}
	/**
	 * Returns the XCatalog for this delegate.
	 */
	XCatalog getCatalog()
	{
		return _catalog;
	}
}