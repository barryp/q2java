package org.openxml.source.holders;

/**
 * org/openxml/source/holders/URLHolderFactoryImpl.java
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
import org.openxml.source.*;


/**
 * Implements a factory for URL holders. Handles URIs that begin with any known
 * network protocol, including <TT>http:</TT>, <TT>ftp:</TT>, <TT>file:</TT>
 * and <TT>jar:</TT>. Provides generic access to these sources through URL
 * connections, including support for caching. Other holders may provide more
 * efficient access to certain protocols.
 * <P>
 * This is the default holder factory, so any source that was not accepted
 * by any of the other holders better be accepted by this one. If the URI
 * is not a valid URL, an {@link IOException} will be thrown.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see HolderFactory
 */
final class URLHolderFactoryImpl
	extends HolderFactoryImpl
{


	public Holder newHolder( Source source )
		throws IOException
	{
		URL url;
		
		try
		{
			url = new URL( source.getURI() );
			return new URLHolderImpl( url, url.toString(), source.getEncoding(), source.getDocClass() );
		}
		catch ( MalformedURLException except )
		{
		}
		return null;
	}
}