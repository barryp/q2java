package org.openxml.dom;

/**
 * org/openxml/dom/CollectionImpl.java
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
import org.w3c.dom.html.*;
import org.openxml.XMLCollection;


/**
 * {@link CollectionImpl#item} must traverse down the tree and decrement the
 * index until it matches an element who's index is zero. Since integers are
 * passed by value, this class servers to pass the index into each recursion
 * by reference. It encompasses all the operations that need be performed on
 * the index, although direct access is possible.
 * 
 * @see CollectionImpl#item
 */
class CollectionIndex
{
	
	
	/**
	 * Holds the actual value that is passed by reference using this class.
	 */
	private int        _index;
	

	/**
	 * Constructs a new index with the specified initial value. The index will
	 * then be decremeneted until it reaches zero.
	 * 
	 * @param index The initial value
	 */
	CollectionIndex( int index )
	{
		_index = index;
	}
	/**
	 * Decrements the index by one.
	 */
	void decrement()
	{
		-- _index;
	}
	/**
	 * Returns the current index.
	 * 
	 * @return Current index
	 */
	int getIndex()
	{
		return _index;
	}
	/**
	 * Returns true if index is zero (or negative).
	 * 
	 * @return True if index is zero
	 */
	boolean isZero()
	{
		return _index <= 0;
	}
}