package org.openxml.source.holders;

/**
 * org/openxml/source/holders/JARHolderImpl.java
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
import org.openxml.source.*;


/**
 * Implements a JAR file holders. Handles URIs that begin with <TT>jar:</TT>
 * followed by the JAR URL, <TT>!</TT> and the entry path. If the JAR is stored
 * in the file system, this holder will provide caching for the document by
 * tracking the last modified date of the JAR itself.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Holder
 */
final class JARHolderImpl
	extends HolderImpl
{

	
	/**
	 * Holds the last modified time for the JAR file.
	 */
	private long        _lastModified;


	/**
	 * Points to the JAR file in which the source is contained. The last
	 * modified time is checked on the JAR file rather then the contained
	 * source for efficiency.
	 */
	private File        _jar;

	
	JARHolderImpl( String uri, File jar, String encoding, Class docClass )
		throws IOException
	{
		super( uri, encoding, docClass );
		_jar = jar;
		_lastModified = _jar.lastModified();
	}
	public boolean canCache()
	{
		return true;
	}
	public boolean hasModified()
	{
		try
		{
			return ( _jar.lastModified() > _lastModified );
		}
		catch ( Exception except )
		{
			return true;
		}
	}
}