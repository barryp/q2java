package org.openxml.source.holders;

/**
 * org/openxml/source/holders/FileHolderImpl.java
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
import java.net.URL;
import org.openxml.source.*;


/**
 * Implements a file holder. Handles URIs that begin with either <TT>file:/</TT>
 * or <TT>file:///</TT>, followed by the full file path. File holders provide
 * faster access to the file system over URL holders and support caching.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Holder
 */
final class FileHolderImpl
	extends HolderImpl
{

	
	/**
	 * Holds the last modified time for the file.
	 */
	private long    _lastModified;


	/**
	 * Holds a reference to the file being accessed.
	 */
	protected File        _file;
	

	FileHolderImpl( File file, String encoding, Class docClass )
		throws IOException
	{
		super( file.toString(), encoding, docClass );
		
		int        index;
		String    type;
		
		if ( file == null || ! file.exists() || ! file.canRead() )
			throw new IOException( "Cannot access the source file '" + file.toString() + "'." );
		_file = file;
		_uri = file.toString();
		_lastModified = _file.lastModified();
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
		return true;
	}
	public Reader getReader()
		throws IOException
	{
		return new BufferedReader( new FileReader( _file ) );
	}
	public String getURI()
	{
		return _file.toString();
	}
	public boolean hasModified()
	{
		try
		{
			return ( _file.lastModified() > _lastModified );
		}
		catch ( Exception except )
		{
			return true;
		}
	}
}