package org.w3c.dom.fi;

/**
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */


import org.w3c.dom.*;


/**
 * NodeIterators are used to step through a set of nodes, e.g. the set of nodes
 * in a NodeList, the document subtree governed by a particular node, the results
 * of a query, or any other set of nodes. The set of nodes to be iterated is
 * determined by the factory that creates the iterator.
 * <P>
 * Any iterator that returns nodes may implement the NodeIterator interface.
 * Users and vendor libraries may also choose to create iterators that implement
 * the NodeIterator interface.
 */
public interface NodeIterator 
{

	
	/**
	 * Returns the next node in the set and advances the position of the iterator
	 * in the set. After a NodeIterator is created, the first call to nextNode()
	 * returns the first node in the set.
	 * 
	 * @return The next Node in the set being iterated over, or NULL if there are
	 *  no more members in that set
	 */
	public Node nextNode();
	/**
	 * Returns the previous node in the set and moves the position of the
	 * iterator backwards in the set
	 * 
	 * @return The previous Node in the set being iterated over, or NULL if there
	 *  are no more members in that set.
	 */
	public Node prevNode();
}