package org.openxml;

/**
 * org/openxml/XMLCollection.java
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


import org.w3c.dom.*;
import org.openxml.dom.*;


/**
 * Implements a live collection of elements. This collection is based on the
 * {@link org.w3c.dom.html.HTMLCollection} defined for HTML documents but works
 * with XML documents.
 * <P>
 * The collection is defined in terms of a root element and the elements to look
 * for under this root. Only elements of the specified type are contained in the
 * collection. Elements are returned by index or by identifier (the <TT>id</TT>
 * attribute). The collection is live, meaning that changes to the document tree
 * are immediately reflected in the collection. The collection is not optimized for
 * traversing large document trees.
 * <P>
 * Application specific collections can be defined by overriding the methods
 * {@link #recurse} and {@link #collectionMatch}. The first indicates whether the
 * collection is recursive or flat. The second returns true for each element that
 * matches the collection. The default implementation looks for elements based on
 * their tag name.
 * <P>
 * Note that synchronization on the traversed document cannot be achieved.
 * The document itself cannot be locked, and locking each traversed node is
 * likely to lead to a dead lock condition. Therefore, there is a chance of the
 * document being changed as results are fetched; in all likelihood, the results
 * might be out dated, but not erroneous.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see XMLDocument
 * @see org.w3c.dom.html.HTMLCollection
 */
public class XMLCollection
	extends CollectionImpl
{


	/**
	 * Construct a collection to look for named elements (<TT>lookFor</TT>
	 * matches the tag name) under the top-level element.
	 *
	 * @param topLevel The top level element underneath which to look
	 * @param lookFor The element to look for
	 */
	public XMLCollection( XMLElement topLevel, String lookFor )
	{
		super( topLevel, lookFor );
	}
	/**
	 * Construct a collection to look for named elements (<TT>lookFor</TT>
	 * matches the tag name) inside the document.
	 *
	 * @param document The document in which to look
	 * @param lookFor The element to look for
	 */
	public XMLCollection( Document document, String lookFor )
	{
		super( document.getDocumentElement(), lookFor );
	}
}