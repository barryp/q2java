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
import org.openxml.dom.ParentNodeImpl;


/**
 * Implementation of a live node tree or list iterator. This iterator
 * supports filtering and selective node return, traversing to next and
 * previous node, and is kept live with respect to changes in the
 * document. This list iterator is also used to implement a live node
 * list.
 * <P>
 * The iterator interface is defined by {@link NodeIterator}. This class
 * serves as an adapter class that implements the interface and delegates
 * iteration requests to a inner iterator, and destroys the inner iterator
 * when garabage collected.
 * <P>
 * {@link InnerIterator} contains the actual iteration logic. Changes
 * in the document tree are reported through the {@link InnerIterator}
 * interface and the iterator has to adjust accordingly. Two inner
 * iterators exist, one for list iteration the other for tree iteration.
 * <P>
 * {@link ParentNodeImpl} creates the new iterator and registers it as a
 * listener to changes in the node's tree. The node obtains the listener
 * by calling {@link #asInnerIterator} on the iterator. The iterator can
 * unregistered itself by calling {@link ParentNodeImpl#removeInnerIterator}
 * on the node.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:05 $
 * @author Impl/inner design contributed by <a href="mailto:ray@imall.com">Ray Whitmer</a>.
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see NodeIterator
 * @see InnerIterator
 * @see org.openxml.dom.ParentNodeImpl
 */
public final class NodeIteratorImpl
    implements NodeIterator, NodeList
{
    
    
    public synchronized Node nextNode()
    {
        Node    next;
        
        // Find the next node that matches the whatToShow criteria. If a filter is
        // specified for this iterator, repeat until the filter accepts the node.
        next = _inner.nextNode();
        if ( _filter != null ) {
            while ( next != null && _filter.acceptNode( next ) != NodeFilter.FILTER_ACCEPT )
                next = _inner.nextNode();
        }
        return next;
    }

    
    public synchronized Node previousNode()
    {
        Node    prev;

        // Find the previous node that matches the whatToShow criteria. If a filter is
        // specified for this iterator, repeat until the filter accepts the node.
        prev = _inner.previousNode();
        if ( _filter != null ) {
            while ( prev != null && _filter.acceptNode( prev ) != NodeFilter.FILTER_ACCEPT )
                prev = _inner.previousNode();
        }
        return prev;
    }


    public final boolean getExpandEntityReferences()
    {
        return _expandEntityReferences;
    }


    public final void setExpandEntityReferences( boolean expand )
    {
        _expandEntityReferences = expand;
    }


    public final NodeFilter getFilter()
    {
        return _filter;
    }


    public final int getWhatToShow()
    {
        return _inner.getWhatToShow();
    }


    /**
     * Return the inner iterator. The inner object will be registered as
     * a listener on the owner for notifying the iterator when a node in
     * the iterated tree has been removed.
     * 
     * @return An inner iterator
     */
    public InnerIterator asInnerIterator()
    {
        // Return the inner iterator which will be reference by ParentNodeImpl
        // for the purpose of notifications, This outer iterator is only
        // referenced by the application.
        return _inner;
    }
    
    
    /**
     * Return the nth child in the node (zero based). If the child does not exist,
     * returns null. No exception is thrown if index is negative.
     * 
     * @param index Index of child to return (zero based)
     * @return Child or null
     */
    public synchronized Node item( int index )
    {
        Node    node;
        
        if ( index < 0 )
            return null;
        // Move to the beginning of the tree and start counting until
        // index is zero. If a node is found, return it, otherwise return
        // null.
        _inner.reset();
        node = nextNode();
        while ( node != null && index > 0 ) {
            node = nextNode();
            -- index;
        }
        return node;
    }
    
    
    /**
     * Return the number of childs in this node.
     * 
     * @return Number of childs
     */
    public synchronized int getLength()
    {
        int count;
        
        // Move to the beginning of the tree and start counting how
        // many nodes are found and return that count.
        _inner.reset();
        for ( count = 0 ; nextNode() != null ; ++count )
            ; // Empty statement
        return count;
    }
    
    
    public void detach()
    {
        // Called when this outer iterator is no longer needed by the application
        // and is being garbage collected. Tells the inner iterator that it too is
        // not needed any more and can clean itself.
        _inner.detach();
        _inner = null;
        _filter = null;
    }

    
    /**
     * Constrctor for a new node iterator. The top of the node tree is
     * also the owner of this iterator and notifies it of any changes
     * to the document tree that might affect it. Depending on the value
     * of <tt>tree</tt> this will return either a tree or a list iterator.
     * 
     * @param owner The owner of this iterator and top of the node List to iterate
     * @param tree True if tree iterator required, false for list iterator
     * @param whatToShow Flag indicating what nodes to return
     * @param filter Optional node filter
     */
    public NodeIteratorImpl( ParentNodeImpl owner, boolean tree,
                             int whatToShow, NodeFilter filter )
    {
        // Must do that because what-to-show mask is one bit out of phase with
        // result of 1 << getNodeType().
        if ( whatToShow != NodeFilter.SHOW_ALL )
            whatToShow = whatToShow << 1;
        if ( tree )
            _inner = new TreeInnerIterator( owner, whatToShow );
        else
            _inner = new ListInnerIterator( owner, whatToShow );
        _filter = filter;
    }

    
    /**
     * The node filter. If a node filter is used, only nodes that are
     * accepted by this filter will be returned. For each node that
     * matches the node mask, {@link NodeFilter#acceptNode} is called
     * on the node.
     */
    private NodeFilter          _filter;


    /**
     * References the inner iterator which provides the actual iteration
     * code. This object is created by the constructor and is destroyed
     * by the finalizer.
     */
    private InnerIterator       _inner;


    private boolean             _expandEntityReferences;


}
