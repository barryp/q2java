package org.openxml.source.holders;

/**
 * org/openxml/source/holders/FileHolderFactoryImpl.java
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


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import org.openxml.source.*;


/**
 * Implements a factory for file holders. Handles URIs that begin with either
 * <TT>file:/</TT> or <TT>file:///</TT>, followed by the full file path.
 * File holders provide faster access to the file system over URL holders
 * and support caching.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see HolderFactory
 */
final class FileHolderFactoryImpl
	extends HolderFactoryImpl
{
	
	
	public Holder newHolder( Source source )
		throws IOException
	{
		URL     url;

		try
		{
			url = new URL( source.getURI() );
			if ( url.getProtocol().equalsIgnoreCase( "file" ) )
				return new FileHolderImpl( new File( url.getFile() ), source.getEncoding(), source.getDocClass() );
			return null;
		}
		catch ( MalformedURLException e1 )
		{
			try
			{
				url = new URL( "file:/" + source.getURI() );
				return new FileHolderImpl( new File( url.getFile() ), source.getEncoding(), source.getDocClass() );
			}
			catch ( MalformedURLException e2 )
			{
			}
		}
		return null;
	}
}