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
 * The collection has to meet two requirements: it has to be live, and it has
 * to traverse depth first and always return results in that order. As such,
 * using an object container (such as {@link java.util.Vector}) is expensive on
 * insert/remove operations. Instead, the collection has been implemented using
 * three traversing functions. As a result, operations on large documents will
 * result in traversal of the entire document tree and consume a considerable
 * amount of time.
 * <P>
 * Note that synchronization on the traversed document cannot be achieved.
 * The document itself cannot be locked, and locking each traversed node is
 * likely to lead to a dead lock condition. Therefore, there is a chance of the
 * document being changed as results are fetched; in all likelihood, the results
 * might be out dated, but not erroneous.
 * <P>
 * Used to implement both {@link org.openxml.XMLCollection} and {@link
 * org.openxml.dom.html.HTMLAnchorElementImpl}.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLCollection
 */
public class CollectionImpl
	implements HTMLCollection
{
	

	/**
	 * This is the top level element underneath which the collection exists.
	 */
	private Element            _topLevel;
	
	
	/**
	 * This is the element type that this collection deals with. It identifies
	 * the element's tag name, and only elements matching these tag names are
	 * counted or returned by this collection.
	 */
	private String            _lookForTag;

	
	/**
	 * Hidden constructor used by derived classes that might have different
	 * _lookfor properties.
	 * 
	 * @param topLevel The element underneath which the collection exists
	 */
	public CollectionImpl( Element topLevel )
	{
		if ( topLevel == null )
			throw new NullPointerException( "Argument 'topLevel' is null." );
		_topLevel = topLevel;
	}
	/**
	 * Construct a new collection that retrieves element of the specific type
	 * (<TT>lookFor</TT>) from the specific document portion (<TT>topLevel</TT>).
	 * 
	 * @param topLevel The element underneath which the collection exists
	 * @param lookFor Tag of element to look for
	 */
	public CollectionImpl( Element topLevel, String lookFor )
	{
		if ( topLevel == null )
			throw new NullPointerException( "Argument 'topLevel' is null." );
		if ( lookFor == null || lookFor.length() == 0 )
			throw new NullPointerException( "Argument 'lookFor' is null or an empty string." );
		_topLevel = topLevel;
		_lookForTag = lookFor;
	}
	/**
	 * Determines if current element matches based on what we're looking for.
	 * The element is passed along with an optional identifier name. If the
	 * element is the one we're looking for, return true. If the name is also
	 * specified, the name must match the <TT>id</TT> attribute.
	 * 
	 * @param elem The current element
	 * @param name The identifier name or null
	 * @return The element matches what we're looking for
	 */
	protected boolean collectionMatch( Element elem, String name )
	{
		boolean    match;
		
		synchronized ( elem )
		{
			match = elem.getTagName().equals( _lookForTag );
			if ( match && name != null )
				match = name.equals( elem.getAttribute( "id" ) );
		}
		return match;
	}
	/**
	 * Returns the length of the collection. This method might traverse the entire
	 * document tree.
	 * 
	 * @return Length of the collection
	 */
	public final int getLength()
	{
		// Call recursive function on top-level element.
		return getLength( getTopLevel() );
	}
	/**
	 * Recursive function returns the number of elements of a particular type that
	 * exist under the top level element. This is a recursive function and the
	 * top level element is passed along.
	 * 
	 * @param topLevel Top level element from which to scan
	 * @return Number of elements
	 */
	private int getLength( Element topLevel )
	{
		int        length;
		Node    node;
	
		synchronized ( topLevel )
		{
			// Always count from zero and traverse all the childs of the current
			// element in the order they appear.
			length = 0;
			node = topLevel.getFirstChild();
			while ( node != null )
			{
				// If a particular node is an element (could be HTML or XML), do two
				// things: if it's the one we're looking for, count another matched
				// element; at any rate, traverse it's children as well.
				if ( node instanceof Element )
				{
					if ( collectionMatch( (Element) node, null ) )
						++ length;
					if ( recurse() )
						length += getLength( (Element) node );
				}
				node = node.getNextSibling(); 
			}
		}
		return length;
	}
	/**
	 * Returns the top level element underneath which the collection exists.
	 * 
	 * @return Top level element from which to scan
	 */
	private Element getTopLevel()
	{
		return _topLevel;
	}
	/**
	 * Retrieves the indexed node from the collection. Nodes are numbered in tree
	 * order - depth-first traversal order. This method might traverse the entire
	 * document tree.
	 * 
	 * @param index The index of the node to return
	 * @return The specified node or null if no such node found
	 */
	public final Node item( int index )
	{
		if ( index < 0 )
			throw new IllegalArgumentException( "Argument 'index' is negative." );
		// Call recursive function on top-level element.
		return item( getTopLevel(), new CollectionIndex( index ) );
	}
	/**
	 * Recursive function returns the numbered element of a particular type that
	 * exist under the top level element. This is a recursive function and the
	 * top level element is passed along.
	 * <p>
	 * Note that this function must call itself with an index and get back both
	 * the element (if one was found) and the new index which is decremeneted
	 * for any like element found. Since integers are only passed by value, this
	 * function makes use of a separate class ({@link CollectionIndex}) to
	 * hold that index.
	 * 
	 * @param topLevel Top level element from which to scan
	 * @param index The index of the item to retreive
	 * @return Number of elements
	 * @see CollectionIndex
	 */
	private Node item( Element topLevel, CollectionIndex index )
	{
		Node    node;
		Node    result;

		synchronized ( topLevel )
		{
			// Traverse all the childs of the current element in the order they appear.
			// Count from the index backwards until you reach matching element with an
			// index of zero. Return that element.
			node = topLevel.getFirstChild();
			while ( node != null )
			{
				// If a particular node is an element (could be HTML or XML), do two
				// things: if it's the one we're looking for, decrease the index and
				// if zero, return this node; at any rate, traverse it's children
				// as well.
				if ( node instanceof Element )
				{
					if ( collectionMatch( (Element) node, null ) )
					{
						if ( index.isZero() )
							return node;
						index.decrement();
					}
					if ( recurse() )
					{
						result = item( (Element) node, index );
						if ( result != null )
							return result;
					}
				}
				node = node.getNextSibling(); 
			}
		}
		return null;
	}
	/**
	 * Retrieves the named node from the collection. The name is matched case
	 * sensitive against the <TT>id</TT> attribute of each element in the
	 * collection, returning the first match. The tree is traversed in depth-first
	 * order. This method might traverse the entire document tree.
	 * 
	 * @param name The name of the node to return
	 * @return The specified node or null if no such node found
	 */
	public final Node namedItem( String name )
	{
		if ( name == null )
			throw new NullPointerException( "Argument 'name' is null." );
		// Call recursive function on top-level element.
		return namedItem( getTopLevel(), name );
	}
	/**
	 * Recursive function returns an element of a particular type with the
	 * specified name (<TT>ID</TT> attribute).
	 * 
	 * @param topLevel Top level element from which to scan
	 * @param name The named element to look for
	 * @return The first named element found
	 */
	private  Node namedItem( Element topLevel, String name )
	{
		Node    node;
		Node    result;

		synchronized ( topLevel )
		{
			// Traverse all the childs of the current element in the order they appear.
			node = topLevel.getFirstChild();
			while ( node != null )
			{
				// If a particular node is an element (could be HTML or XML),
				// do two things: if it's the one we're looking for, and the name
				// (ID attribute) attribute is the one we're looking for, return
				// this element; otherwise, traverse it's children.
				if ( node instanceof Element )
				{
					if ( collectionMatch( (Element) node, name ) )
						return node;
					if ( recurse() )
					{
						result = namedItem( (Element) node, name );
						if ( result != null )
							return result;
					}
				}
				node = node.getNextSibling(); 
			}
			return node;
		}
	}
	/**
	 * Returns true if scanning methods should iterate through the collection.
	 * When looking for elements in the document, recursing is needed to traverse
	 * the full document tree. When looking inside a specific element (e.g. for a
	 * cell inside a row), recursing can lead to erroneous results.
	 * 
	 * @return True if methods should recurse to traverse entire tree
	 */
	protected boolean recurse()
	{
		return true;
	}
}