package org.openxml;

/**
 * org/openxml/XMLBookmarks.java
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


import java.util.*;
import org.w3c.dom.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Node
 * @see java.io.Reader
 */
public final class XMLBookmarks
{


	private static Hashtable    _allBookmarks;


	private Hashtable            _bookmarks;


	private Document            _document;


	XMLBookmarks( Document document )
	{
		if ( document == null )
			throw new NullPointerException( "Argument 'document' is null." );
		_document = document;
		_bookmarks = new Hashtable();
	}
	public synchronized Element get( String mark )
	{
		Element    elem;

		elem = (Element) _bookmarks.get( mark );
		if ( elem != null && elem.getOwnerDocument() != _document )
		{
			_bookmarks.remove( mark );
			elem = null;
		}
		return elem;
	}
	public Document getDocument()
	{
		return _document;
	}
	public synchronized String[] list()
	{
		String[]    marks;
		Enumeration    enum;
		int            i = 0;

		marks = new String[ _bookmarks.size() ];
		enum = _bookmarks.keys();
		i = 0;
		while ( enum.hasMoreElements() )
		{
			marks[ i ] = (String) enum.nextElement();
			++i;
		}
		return marks;
	}
	public static XMLBookmarks of( Document doc )
	{
		XMLBookmarks    bookmarks;

		if ( _allBookmarks == null )
		{
			_allBookmarks = new Hashtable();
			bookmarks = null;
		}
		else
			bookmarks = (XMLBookmarks) _allBookmarks.get( doc );
		if ( bookmarks == null )
		{
			bookmarks = new XMLBookmarks( doc );
			_allBookmarks.put( doc, bookmarks );
		}
		return bookmarks;
	}
	public synchronized void set( String mark, Element elem )
	{
		synchronized ( elem )
		{
			if ( elem.getOwnerDocument() != _document )
				throw new IllegalArgumentException( "Element and bookmarks do not belong to same document." );
			_bookmarks.put( mark, elem );
		}
	}
	public synchronized void setUnique( String mark, Element elem )
		throws Exception
	{
		Object    old;

		synchronized ( elem )
		{
			if ( elem.getOwnerDocument() != _document )
				throw new IllegalArgumentException( "Element and bookmarks do not belong to same document." );
			old = _bookmarks.get( mark );
			if ( old != null && old != elem )
				throw new Exception( "Identifier not unique in this document." );
			_bookmarks.put( mark, elem );
		}
	}
}