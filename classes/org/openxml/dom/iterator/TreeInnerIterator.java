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
 * Implementation of a live node tree iterator. This iterator supports selective
 * node return, traversing to next and previous node, and is kept live with respect
 * to changes in the document.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author Impl/inner design contributed by <a href="mailto:ray@imall.com">Ray Whitmer</a>.
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see NodeIterator
 * @see NodeIteratorImpl
 * @see InnerIterator
 * @see org.openxml.dom.NodeImpl
 */
public class TreeInnerIterator
	extends InnerIterator
{


	TreeInnerIterator( NodeImpl owner, int whatToShow )
	{
		if ( owner == null )
			throw new NullPointerException( "Argument 'owner' is null." );
		_owner = owner;
		_whatToShow = whatToShow;
		_current = _owner;
	}
	/**
	 * Returns the next node in the tree that matches the <TT>whatToShow</TT> mask.
	 * If no such node is found, null is returned.
	 * <P>
	 * Initially {@link #_current} points to the tree top, and the first returned
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
	 * @return The next node in the tree, or null
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
			// If the current node has childern, immediately begin traversing the
			// first childern (depth-first).
			if ( _current.hasChildNodes() )
				_current = _current.getFirstChild();
			else
			// If this node has a next sibling, move to the next sibling. If the
			// node has no next sibling, try the next sibling of its parent and
			// so on until reaching the top. When reaching the top, there is no
			// next node to traverse to, so just return null.
			{
				while ( _current.getNextSibling() == null )
				{
					if ( _current.getParentNode() == _owner ||
						 _current.getParentNode() == null )
						return null;
					_current = _current.getParentNode();
				}
				_current = _current.getNextSibling();
			}
			// If the current node matches the whatToShow mask, return it.
			// Otherwise, iterate and return the next matching node.
			if ( _current == null ||
				 ( 1 << _current.getNodeType() & _whatToShow ) != 0 )
				break;
		}
		return _current;
	}
	/**
	 * Returns the previous node in the tree that matches the <TT>whatToShow</TT>
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
	 * @return The previous node in the tree, or null
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
			// If this node has a previous sibling, move to the previous sibling.
			// If the node has no previous sibling, move to its parent and possibly
			// return it. When reaching the top, _current becomes the top. This is
			// the only way to assure immediate return of prevNode and that
			// nextNode will return the first node in the tree.
			if ( _current.getPreviousSibling() == null )
			{
				_current = _current.getParentNode();
				if ( _current == _owner || _current == null )
					break;
			}
			else
			// If the previous node has children, traverse to its last child.
			// If that child has children, traverse to its last child and so on,
			// until reaching the last deepest child.
			{
				_current = _current.getPreviousSibling();
				while ( _current.hasChildNodes() )
					_current = _current.getLastChild();
			}
			// If the current node matches the whatToShow mask, return it.
			// Otherwise, iterate and return the next matching node.
			if ( _current == null ||
				 ( 1 << _current.getNodeType() & _whatToShow ) != 0 )
				break;
		}
		return result;
	}
}