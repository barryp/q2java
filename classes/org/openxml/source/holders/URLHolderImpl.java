package org.openxml.source.holders;

/**
 * org/openxml/source/holders/URLHolderImpl.java
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
import java.net.URLConnection;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLDocument;
import org.openxml.source.*;


/**
 * Implements a URL holder. Handles URIs that begin with any known network
 * protocol, including <TT>http:</TT>, <TT>ftp:</TT>, <TT>file:</TT> and
 * <TT>jar:</TT>. Provides generic access to these sources through URL
 * connections, including support for caching. Other holders may provide more
 * efficient access to certain protocols.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Holder
 */
final class URLHolderImpl
	extends HolderImpl
{
	

	/**
	 * Holds the last modified time for the URL.
	 */
	private long            _lastModified;
	

	/**
	 * Caching state is determined at construction time based on a connection
	 * property.
	 */
	private boolean            _canCache;
	
	
	URLHolderImpl( URL url, String uri, String encoding, Class docClass )
		throws IOException
	{
		super( uri, encoding, docClass );

		String            type;
		URLConnection    connection;
		int                index;
			
		connection = url.openConnection();
		_lastModified = connection.getLastModified();
		_canCache = connection.getUseCaches();
		_encoding = connection.getContentEncoding();
		type = connection.getContentType();
		
		// If the document class was not specified, try to guess it from
		// the MIME content type. If that still doesn't work, try guessing
		// it from the file extension.
		if ( type != null && _docClass == null )
		{
			if ( type.equalsIgnoreCase( "text/html" ) )
				_docClass = Source.DOCUMENT_HTML;
			else
			if ( type.equalsIgnoreCase( "text/xml" ) ||
				 type.equalsIgnoreCase( "application/xml" ) )
				_docClass = Source.DOCUMENT_XML;
			else
			if ( type.equalsIgnoreCase( "text/xml-dtd" ) ||
				 type.equalsIgnoreCase( "application/xml-dtd" ) )
				_docClass = Source.DOCUMENT_DTD;
		}
		if ( _docClass == null )
		{
			index = _uri.lastIndexOf( '.' );
			if ( index > 0 )
			{
				type = _uri.substring( index );
				if ( type.equalsIgnoreCase( ".htm" ) || type.equalsIgnoreCase( ".html" ) )
					_docClass = Source.DOCUMENT_HTML;
				else
				if ( type.equalsIgnoreCase( ".xml" ) || type.equalsIgnoreCase( ".dom" ) )
					_docClass = Source.DOCUMENT_XML;
				if ( type.equalsIgnoreCase( ".dtd" ) )
					_docClass = Source.DOCUMENT_DTD;
			}
		}
	}
	public boolean canCache()
	{
		return _canCache;
	}
	public boolean hasModified()
	{
		try
		{
			return ( new URL( _uri ).openConnection().getLastModified() > _lastModified );
		}
		catch ( Exception except )
		{
			return true;
		}
	}
}