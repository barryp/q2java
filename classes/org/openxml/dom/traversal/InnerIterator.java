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


package org.openxml.dom.traversal;


import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.openxml.dom.*;


/**
 * Interface of inner object of a live node iterator. List and tree inner
 * iterators extend this class, so a single {@link NodeIteratorImpl} can
 * use either implementations.
 * <P>
 * This interface defines a reset method required for using the iterator
 * as a node list. The destroy method is required by the care-taker design
 * pattern, which saves the need to use weak references.
 * <P>
 * The inner iterator object is held by the creating node for the purpose
 * of notification. The outer iterator object is held by the application
 * for the purpose of iterating the list. When the iterator is no longer
 * needed, the application releases the reference to the outer iterator
 * which is garbage collected. The outer iterator's finalize method
 * releases the reference between the node and the inner iterator,
 * allowing the inner iterator to be garbage collected as well.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:05 $
 * @author Impl/inner design contributed by <a href="mailto:ray@imall.com">Ray Whitmer</a>.
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see NodeIterator
 */
public abstract class InnerIterator
    implements NodeIterator
{

    
    /**
     * Called to notify this iterator that a particular node has been
     * removed. If the iterator is pointing at that node, or one of its
     * childern, it must adjust accordingly.
     * <P>
     * This method only affects the iterator if it is pointing at the
     * removed node or a child of the removed node. It does not affect
     * the iterator if it is pointing to a parent of the removed node,
     * or the removed node is on some other branch of the document tree.
     * 
     * @param node The node being removed
     */
    public void removeNode( Node removedNode )
    {
        Node    match;
        
        // Start with the current node and traversing all the parents, check which
        // is the node that has been removed. This iterator is only affected in the
        // removed node it iself or one of its parents.
        match = _current;
        while ( match != null ) {
            if ( match == removedNode ) {
                // Given the removed node, move one node back, assuring proper
                // position in the tree. A call to nextNode will return the next
                // node after the removed, a call to previousNode will return the
                // previous node before the removed.
                _current = match;
                previousNode();
                return;
            }
            match = match.getParentNode();
        }
        // Nothing to do, just return.
    }
    
    
    /**
     * Return to the beginning of the list, so the iteration can start
     * all over. The next call to {@link #nextNode} will return the first
     * node in the list.
     */
    public void reset()
    {
        _current = _owner;
    }
    
    
    /**
     * Called by {@link NodeIteratorImpl} when this iterator is no longer
     * needed. {@link NodeIteratorImpl} is being garbage collected when
     * the application ceases referencing it, but this inner iterator is
     * still held by the owner node. This method will unregister the
     * iterator with the node allowing it to be garbage collected.
     */
    public void detach()
    {
        if ( _owner != null ) {
            _owner.removeInnerIterator( this );
            _owner = null;
        }
        _current = null;
    }


    public final boolean getExpandEntityReferences()
    {
        return false;
    }


    public final void setExpandEntityReferences( boolean expand )
    {
    }


    public final NodeFilter getFilter()
    {
        return null;
    }


    public final int getWhatToShow()
    {
        return _whatToShow;
    }
    
    
    /**
     * The top node of the iterated tree and the iterator's owner. This node is
     * the parent of all traveresed nodes, but is never returned during iteration.
     * No parent or sibling of {@link #_owner} is ever returned. The iterator is
     * registered as a listener with this owner.
     */
    protected ParentNodeImpl  _owner;
    

    /**
     * The current node. This is the last node returned by {@link NodeIterator#nextNode}
     * or {@link NodeIterator#previousNode}, and is set to {@link #_owner} on initialization.
     */
    protected Node        _current;
    
    
    /**
     * Mask of node types. This mask determines which node types will be returned
     * from the iterator. The value of <TT>0xFFFF</TT> will return nodes of all types,
     * and the default value <TT>0x1A</TT> will return elements, text and CDATA
     * sections.
     * <P>
     * The mask is defined as follows. For each node type, its node type code is
     * used as the position of the one in the mask, and all masks are combined with
     * a binary and. Thus, the default value is calculated as:
     * <PRE>
     * = ( 1 << ELEMENT_NODE ) + ( 1 << TEXT_NODE ) + ( 1 << CDATA_SECTION_NODE )
     * = ( 1 << 1 ) + ( 1 << 3 ) + ( 1 << 4 )
     * = 0x02 + 0x08 + 0x10
     * = 0x1A
     * </PRE>
     * This mask is set in the constructor to be one bit off than the constants
     * defined in {@link NodeIterator} so that it can easily mask <TT>1 <<
     * getNodeType()</TT>.
     */
    protected int         _whatToShow = 0xFFFF;


}
