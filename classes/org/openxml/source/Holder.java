package org.openxml.source;

/**
 * org/openxml/source/Holder.java
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
import java.io.Reader;
import org.w3c.dom.Document;


/**
 * Interface describing a document holder. Holder classes are used to provide
 * access to documents through various protocols and storages locations
 * (network, file system, JAR, etc) and to support document caching.
 * <P>
 * The holder implements methods for determining whether the source document
 * can be cached ({@link #canCache}) and whether the cached image is up to
 * date ({@link #hasModified}). It also provides access to the document source
 * through {@link #getReader}. It is up to the particular holder to determine
 * how to implement these features.
 * <P>
 * The document is obtained using two methods. {@link #getReadOnly} returns
 * a read-only instance of the document that should not be modified by the
 * application. If the document supports caching, {@link #newInstance} returns
 * a new modifiable instance of the document each time. The application may
 * modify a given instance without affecting other instances. If the document
 * does not support caching, then both methods return the same single instance,
 * but calling {@link HolderFactory#newHolder} will return a different holder
 * each time.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see HolderFactory
 */
public abstract class Holder
{


	/**
	 * Returns true if document can be cached. Some document can be cached and
	 * will only be reloaded if the original source has been updated. The holder
	 * must be capable of determining if the document can be cached, if in doubt,
	 * it should return false.
	 *
	 * @return True if document can be cached
	 */
	public abstract boolean canCache();
	/**
	 * Returns the encoding for reading the source. A default encoding might
	 * have been specified when the holder was created, or derived from the
	 * source itself (e.g. the content type returned by a Web server).
	 *
	 * @return Default encoding or null
	 */
	public abstract String getEncoding();
	/**
	 * Returns the last exception generated when parsing the document. The return
	 * value is only valid after obtaining a document instance, and    will indicate
	 * a validity or well formed parsing exception. I/O exceptions are thrown
	 * directly by the document retrieving methods.
	 *
	 * @return Last error generated when parsing document
	 */
	public abstract IOException getLastException();
	/**
	 * Returns a reader for the source. This reader is used by the parser and
	 * should assume the specified encoding, unless a different encoding has
	 * been determined. A new reader is returned each time this method is called.
	 *
	 * @return The reader for the source
	 * @throws IOException Failed to create a reader for this source
	 */
	public abstract Reader getReader()
		throws IOException;
	/**
	 * Returns a read-only instance of the document. The read-only instance is
	 * the one cached by this holder (if caching is supported) and the caller
	 * agrees not to modify its contents. The same document will be returned each
	 * time this method is called.
	 * <P>
	 * A default implementation would call {@link #getReader} to obtain a reader
	 * to the source, parse the document using the selected document class, and
	 * returns that document. The document is rendered read-only only if this
	 * holder can be cached.
	 *
	 * @return A read-only instance of the document
	 */
	public abstract Document getReadOnly();
	/**
	 * Returns the source URI. This name might be different than the one by
	 * which the source was requested and will be used for caching the source.
	 *
	 * @return The resource URI
	 */
	public abstract String getURI();
	/**
	 * Returns true if the source has been modified since it was created.
	 * The holder must be capable of determining whether the source has been
	 * modified since construction, if in doubt, it should return true.
	 *
	 * @return True if source has been modified
	 */
	public abstract boolean hasModified();
	/**
	 * Returns a new modifiable instance of the document. This instance can be
	 * modified by the caller without affecting other callers requesing the same
	 * source. A new instance will be returned each time this method is called.
	 * However, if caching is not supported by this holder, this method is
	 * equivalent to calling {@link #getReadOnly}.
	 * <P>
	 * A default implementation would obtain the instance from {@link #getReadOnly};
	 * if caching is supported, a new modifiable instance woudld be created and
	 * returned, otherwise, the same instance would be returned.
	 *
	 * @return A new modifiable instance of the document
	 */
	public abstract Document newInstance();
}