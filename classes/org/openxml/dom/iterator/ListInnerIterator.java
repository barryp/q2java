package org.openxml.dom.iterator;

/**
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.openxml.org/license/
 * 
 * THIS SOFTWARE AND DOCUMENTATION IS PROVIDED ON AN "AS IS" BASIS
 * WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED,
 * INCLUDING AND WITHOUT LIMITATION, WARRANTIES THAT THE SOFTWARE
 * AND DOCUMENTATION IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGING. SEE THE LICENSE FOR THE
 * SPECIFIC LANGUAGE GOVERNING RIGHTS AND LIMITATIONS UNDER THE
 * LICENSE.
 * 
 * The Initial Developer of this code under the License is
 * OpenXML.org. Portions created by OpenXML.org and/or Assaf Arkin
 * are Copyright (C) 1998, 1999 OpenXML.org. All Rights Reserved.
 */


import org.w3c.dom.*;
import org.w3c.dom.fi.*;
import org.openxml.dom.NodeImpl;


/**
 * Implementation of a live node list iterator. This iterator supports selective
 * node return, traversing to next and previous node, and is kept live with respect
 * to changes in the document.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author Impl/inner design contributed by <a href="mailto:ray@imall.com">Ray Whitmer</a>.
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see NodeIterator
 * @see NodeIteratorImpl
 * @see NodeIteratorListener
 * @see org.openxml.dom.NodeImpl
 */
public class ListInnerIterator
	extends InnerIterator
{


	/**
	 * Constructor only accessible from {@link ListIteratorImpl}. The top of the node
	 * List is also the owner of this iterator and notifies it of any changes to the
	 * document List that might affect it through the {@link NodeIteratorListener}
	 * interface.
	 * 
	 * @param owner The owner of this iterator and top of the node List to iterate
	 * @param whatToShow Flag indicating what nodes to return
	 */
	ListInnerIterator( NodeImpl owner, int whatToShow )
	{
		if ( owner == null )
			throw new NullPointerException( "Argument 'owner' is null." );
		_owner = owner;
		_whatToShow = whatToShow;
		_current = _owner;
	}
	/**
	 * Returns the next node in the list that matches the <TT>whatToShow</TT> mask.
	 * If no such node is found, null is returned.
	 * <P>
	 * Initially {@link #_current} points to the list top, and the first returned
	 * node is the one directly underneath it. The forward traversal algorithm
	 * is defined as:
	 * <UL>
	 * <LI>Move to the next node and return it
	 * <LI>If there is no next node, return null
	 * <LI>If this node has children, move to the first child
	 * <LI>If this node has no children but has a sibling, move to the sibling
	 * <LI>If this node has no children or sibling, move to the parent's sibling
	 * </UL>
	 * 
	 * @return The next node in the list, or null
	 */
	public Node nextNode()
	{
		// Simply setting _current to null guarantees a reset.
		if ( _current == null )
			_current = _owner;;

		// Repeat until we run out of nodes to traverse. Should never happen,
		// but it still makes sense since _current might start as null, and
		// we need to iterate skipping over nodes that do not match the
		// whatToShow mask.
		while ( _current != null )
		{
			// If _current starts as the owner, go to the first child of the
			// owner, otherwise go to the next sibling of _current. If there
			// is no next sibling, return null, but don't go anywhere.
			if ( _current == _owner )
				_current = _owner.getFirstChild();
			else
			if ( _current.getNextSibling() == null )
				return null;
			else
				_current = _current.getNextSibling();
			// If the current node matches the whatToShow mask, return it.
			// Otherwise, iterate and return the next matching node.
			if ( _current == null ||
				 ( 1 << _current.getNodeType() & _whatToShow ) != 0 )
				break;
		}
		return _current;
	}
	/**
	 * Returns the previous node in the list that matches the <TT>whatToShow</TT>
	 * mask. If no such node is found, null is returned.
	 * <P>
	 * By definition, the current position is between the next node and the
	 * previous one, thus, if a call to {@link #nextNode} will return the following
	 * node, a call to this method will return the last value of {@link #nextNode}.
	 * The backward traversal algorithm is defined as:
	 * <UL>
	 * <LI>The current node is the return value, move to the next node
	 * <LI>If there is no previous node, return null
	 * <LI>If this node has a previous sibling, return the last deepest children
	 *  of the previous sibling
	 * <LI>If this node has no sibling, return its parent
	 * </UL>
	 * 
	 * @return The previous node in the list, or null
	 */
	public Node prevNode()
	{
		Node    result;
		
		// If at the top of the list, no where back to go, just return null.
		if ( _current == _owner || _current == null )
			return null;

		result = _current;
		// Repeat until we run out of nodes to traverse. Should never happen,
		// but it still makes sense since _current might start as null, and
		// we need to iterate skipping over nodes that do not match the
		// whatToShow mask.
		while ( _current != null )
		{
			// If this node has a previous sibling, move to it. Otherwise, set
			// _current to null (effectively the beginning of the list).
			if ( _current.getPreviousSibling() == null )
				_current = null;
			else
				_current = _current.getPreviousSibling();
			// If the current node matches the whatToShow mask, return it.
			// Otherwise, iterate and return the next matching node.
			if ( _current == null ||
				 ( 1 << _current.getNodeType() & _whatToShow ) != 0 )
				break;
		}
		return result;
	}
}