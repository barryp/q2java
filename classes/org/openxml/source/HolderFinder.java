package org.openxml.source;

/**
 * org/openxml/source/HolderFinder.java
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
import java.util.*;
import org.w3c.dom.*;


/**
 * Interface for holder finder mechanism. Used for finding suitable holders for
 * document sources, and for registering new holder factories.
 * <P>
 * Given a document source, the finder will attempt to locate a holder most suitable
 * for retrieving, caching and otherwise presenting that source. The finder will
 * query any number of registered holder factories, until a factory capable of
 * retrieving the source document will create and return a document holder.
 * <P>
 * Precedence is given to the last registered factory, so application defined
 * factories will override the default implementation factories. The implementation
 * will likely offer holder factories for network and file system sources. Some
 * factories may implement mapping from public identifiers or remapping of URIs.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Source
 * @see Holder
 * @see HolderFactory
 * @see XCatalog
 */
public interface HolderFinder
{


	/**
	 * Finds and returns an holder for the url. The url is interpreted based
	 * on the installed holder factories and a suitable holder object is returned.
	 * Factories are queried in the reverse order in which they were registered,
	 * thus, the generic URL holder factory is the last to be queried.
	 * If the source could not be found or accessed, null is returned.
	 * <P>
	 * This method is equivalent to calling {@link #findHolder(Source,boolean)}
	 * with <TT>refresh = false</TT>.
	 *
	 * @param source The document source
	 * @return A new holder, or null
	 */
	public Holder findHolder( Source source );
	/**
	 * Finds and returns an holder for the url. The url is interpreted based
	 * on the installed holder factories and a suitable holder object is returned.
	 * Factories are queried in the reverse order in which they were registered,
	 * thus, the generic URL holder factory is the last to be queried.If the
	 * source could not be found or accessed, null is returned.
	 * <P>
	 * <TT>refresh</TT> is true if the source must be reloaded and not obtain
	 * from the cache. If <TT>refresh</TT> is false, the source is only loaded
	 * it it cannot be cached, or if it has been modified since it was cached.
	 *
	 * @param source The document source
	 * @param refresh True if source must be reloaded
	 * @return A new holder, or null
	 */
	public Holder findHolder( Source source, boolean refresh );
	/**
	 * Registers a new holder factory which is capable of producing holders for
	 * specific protocols or storage locations. The factories are queried in
	 * the reverse order in which they were registered, giving precedence to
	 * factories registered by the application.
	 *
	 * @param factory The factory to register
	 */
	public void registerFactory( HolderFactory factory );
	/**
	 * Use the specified XCatalog document as a catalog for mapping public
	 * identifiers to system identifiers and for remapping system identifiers.
	 * <TT>uri</TT> should be an accessible XCatalog document conforming to
	 * the XCatalog DTD.
	 *
	 * @param uri The XCatalog document URI
	 */
	public void useCatalog( String uri );
}