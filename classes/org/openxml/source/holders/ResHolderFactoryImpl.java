package org.openxml.source.holders;

/**
 * org/openxml/source/holders/ResHolderFactoryImpl.java
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
import org.openxml.source.*;


/**
 * Implements a resource holders factory. Handles URIs that begin with
 * <TT>res:</TT> followed by the URI of a resource (e.g. <TT>/mypkg/doc.xml</TT>).
 * The resource is located by calling {@link java.lang.Class#getResource} on the
 * default class loader. Since the location may be a network, file or JAR
 * protocol, it will generally be handled by some other holder class.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see HolderFactory
 */
public final class ResHolderFactoryImpl
	extends HolderFactoryImpl
{

	
	private ClassLoader     _loader;

	
	public ResHolderFactoryImpl()
	{
	}
	public ResHolderFactoryImpl( ClassLoader loader )
	{
		_loader = loader;
	}
	public Holder newHolder( Source source )
		throws IOException
	{
		String  uri;
		URL     url;

		// Get the source URI and it it begins with 'res:', attempt to obtain
		// the named resource. If the resource could not be found, throw an
		// exception to indicate that. Otherwise, use the URL to construct some
		// other holder (may be file, network, JAR).
		uri = source.getURI();
		if ( uri != null && uri.startsWith( "res:" ) )
		{
			if ( _loader != null )
				url = _loader.getResource( uri.substring( 4 ) );
			else
				url = getClass().getResource( uri.substring( 4 ) );
			if ( url == null )
				throw new IOException( "Resource '" + uri + "' cannot be obtained with the default class loader." );
			uri = url.toString();
			source = new SourceImpl( uri, null, source.getEncoding(), source.getDocClass() );
			return findHolder( source );
		}
		return null;
	}
}