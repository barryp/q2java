package org.openxml.source;

/**
 * org/openxml/source/HolderFactory.java
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


/**
 * Interface for factory for creating document holders. Holder classes are used to
 * provide access to documents through various protocols and storages locations
 * (HTTP, file, JAR, etc), and to support document caching. The factory locates
 * and creates an holder based on the supplied document source.
 * <P>
 * A number of factories are registered by default. Additional factories may be
 * defined by the application by implementing {@link HolderFactory#newHolder},
 * and registered with {@link HolderFinder#registerFactory}. The factories are
 * queried in the reverse order they were registered, giving precedence to
 * factories registered by the application over the built-in.
 * <P>
 * If a registered factory is capable of handling a specific protocol or storage
 * locations, it returns a new {@link Holder} object that encapsulates that source.
 * The document is then accessible through {@link Holder#getReadOnly} and {@link
 * Holder#newInstance}.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Holder
 * @see Source
 */
public interface HolderFactory
{


	/**
	 * Abstract method implemented by factory class. Given a document source,
	 * if the factory can handle sources of this protocol or storage location,
	 * it should create and return a new {@link Holder} object. Otherwise,
	 * it should return null allowing some other holder to provide access.
	 *
	 * @param source The document source
	 * @return A new holder, or null
	 * @throws IOException This exception is ignored
	 */
	public Holder newHolder( Source source )
		throws IOException;
}