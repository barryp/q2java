package org.openxml.source.holders;

/**
 * org/openxml/source/holders/JARHolderFactoryImpl.java
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
import java.io.File;
import java.net.URL;
import org.openxml.source.*;


/**
 * Implements a factory for JAR file holders. Handles URIs that begin with
 * <TT>jar:</TT> followed by the JAR URL, <TT>!</TT> and the entry path.
 * If the JAR is stored in the file system, the holder will provide caching
 * for the document by tracking the last modified date of the JAR itself.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see HolderFactory
 */
final class JARHolderFactoryImpl
	extends HolderFactoryImpl
{

	
	public Holder newHolder( Source source )
		throws IOException
	{
		int        index;
		String    uri;
		
		// The URL is defined: jar:<url>!/<entry> where url is: file:///<jar>
		// If the protocol is 'jar', we might be interested in observing this JAR,
		// if and only if, it resides on the local file system. The rest of the
		// URL (the file part), contains a URL in its own, so get the actual
		// protocol from it. Strip away the entry that follows the '!'.
		uri = source.getURI();
		if ( uri != null && uri.startsWith( "jar:file:///" ) )
		{
			uri = uri.substring( 12 );
			index = uri.indexOf( '!' );
			return new JARHolderImpl( uri, new File( uri.substring( 12, index ) ),
									  source.getEncoding(), source.getDocClass() );
		}
		return null;
	}
}