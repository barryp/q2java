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


/**
 * Dec 23, 1999
 * + Moved all implementation of child nodes support from NodeImpl.
 **/


package org.openxml.dom;
    

import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.openxml.dom.traversal.*;


/**
 * Implements a node that has child elements. To conserve memory space, nodes
 * which do not support child elements are extended from {@link NodeImpl},
 * while nodes that support child elements are extended from {@link
 * ParentNodeImpl} containing the additional member variables required to
 * support child nodes.
 * <P>
 * This class is abstract. All derived classes must extend {@link
 * #getNodeType}. In addition, derived classes might wish to extend {@link
 * NodeImpl#cloneNode(boolean)}, {@link #equals} and {@link #toString()}
 * and othe methods as necessary. Many methods cannot be extended.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.Node
 * @see NodeImpl
 */
public abstract class ParentNodeImpl
    extends NodeImpl
    implements Node, Cloneable
{
    
  
    public final NodeList getChildNodes()
    {
        return (NodeList) createNodeIterator( false, NodeFilter.SHOW_ALL, null );
    }


    public final Node getFirstChild()
    {
        return _firstChild;
    }


    public final Node getLastChild()
    {
        return _lastChild;
    }


    public final boolean hasChildNodes()
    {
        return ( _firstChild != null );
    }


    public synchronized void normalize()
    {
        Node    node;
        Node    next;
        
        // Run through all child nodes of this element. If a particular child
        // is an Element, normalize it. If a particular child is a Text and is
        // followed by a second Text node, concatenate the data from the second
        // to the first and remove the second node.
        node = getFirstChild();
        while ( node != null ) {
            if ( node instanceof ParentNodeImpl )
                node.normalize();
            else if ( node instanceof TextImpl ) {
                next = node.getNextSibling();
                while ( next != null && next instanceof TextImpl ) {
                    ( (TextImpl) node ).appendData( ( (TextImpl) next ).getData() );
                    removeChild( next );
                    next = node.getNextSibling();
                }
            }
            node = node.getNextSibling();
        }
    }
    
    
    public final synchronized Node appendChild( Node newChild )
    {
        // Node arguments must be casted to NodeEx in order to operate on them.
        NodeImpl newChildX;
        
        // Make sure the node is not read-only.
        // Throw exception if children not supported by derived class.
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        
        // Cast newChild to NodeImpl and make sure it can be inserted to this
        // node.
        newChildX = castNewChild( newChild );
        
        // We're going to mess with this child node, so make sure no other
        // thread is touching it
        synchronized ( newChild ) {
            // If the newChild is already a child or some node, remove it first
            // before becoming child of this node. Make sure that parent is not
            // read-only.
            if ( newChildX._parent != null ) {
                if ( newChildX._parent.isReadOnly() )
                    throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
                newChildX._parent.removeChild( newChildX );
            }
            
            // Special case: newChild is a DocumentFragment and instead of
            // adding itself, all of its childs are added one by one.
            if ( newChildX instanceof DocumentFragment ) {
                NodeImpl    nextChild;
                
                newChildX = (NodeImpl) newChildX.getFirstChild();
                while ( newChildX != null ) {
                    nextChild = newChildX._nextNode;
                    appendChild( newChildX );
                    newChildX = nextChild;    
                }
                return newChild;
            }
            
            // Node becomes child of this parent and part of this document.
            // Note that this code comes after the test for a DocumentFragment.
            // A fragment does not become part of this node, only its children.
            // The fragment becomes parent-less and child-less.
            newChildX._parent = this;
            // The child becomes owned by the document owner, or by this
            // document if direcly added to the document.
            if ( _ownerDocument != null )
                newChildX.setOwnerDocument( _ownerDocument );
            else
                if ( this instanceof DocumentImpl )
                    newChildX.setOwnerDocument( (DocumentImpl) this );
            
            
            // If the list has no end (it is empty) then newChild is added as
            // the only child in it.
            if ( _lastChild == null ) {
                _lastChild = newChildX;
                _firstChild = newChildX;
                newChildX._prevNode = null;
                newChildX._nextNode = null;
            } else {
                // newChild becomes the new end of the list, adjusting the previous
                // last child.
                _lastChild._nextNode = newChildX;
                newChildX._prevNode = _lastChild;
                newChildX._nextNode = null;
                _lastChild = newChildX;
            }
        }
        return newChild;
    }


    public final synchronized Node insertBefore( Node newChild, Node refChild )
        throws DOMException
    {
        // Node arguments must be casted to NodeEx in order to operate on them.
        NodeImpl newChildX;
        NodeImpl refChildX;

        // Make sure the node is not read-only.
        // Throw exception if children not supported by derived class.
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
   
        // If refChild is null, act as if appendChild was called.
        if ( refChild == null )
            return appendChild( newChild );
        
        // Cast newChild to NodeImpl and make sure it can be inserted to this
        // node. Cast refChild to NodeImpl, making sure it is a child of this
        // node.
        newChildX = castNewChild( newChild );
        refChildX = castOldChild( refChild );
        
        // We're going to mess with this child node, so make sure no other
        // thread is touching it
        synchronized ( newChild ) {
            // .. or this
            synchronized ( refChild ) {
                // If the newChild is already a child or some node, remove it
                // first before becoming child of this node. Make sure that
                // parent is not read-only.
                if ( newChildX._parent != null ) {
                    if ( newChildX._parent.isReadOnly() )
                        throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
                    newChildX._parent.removeChild( newChildX );
                }
                
                // Special case: newChild is a DocumentFragment and instead of
                // inserting itself, all of its childs are inserted one by one.
                if ( newChildX instanceof DocumentFragment ) {
                    NodeImpl    nextChild;
                    
                    newChildX = (NodeImpl) newChildX.getFirstChild();
                    while ( newChildX != null ) {
                        nextChild = newChildX._nextNode;
                        insertBefore( newChildX, refChild );
                        newChildX = nextChild;    
                    }
                    return newChild;
                }
                
                // Node becomes child of this parent and part of this document.
                // Note that this code comes after the test for a
                // DocumentFragment. A fragment does not become part of this
                // node, only its children. The fragment becomes parent-less
                // and child-less.
                newChildX._parent = this;
                newChildX.setOwnerDocument( _ownerDocument );
                
                // If refChild is the first child, newChild becomes the first
                // child on the list.
                if ( _firstChild == refChildX )
                    _firstChild = newChildX;
                // refChild is not the first child, so adjust the previous
                // child to point at newChild instead.
                if ( refChildX._prevNode != null ) {
                    newChildX._prevNode = refChildX._prevNode;
                    refChildX._prevNode._nextNode = newChildX;
                }
                // Adjust the refChild to point at this child and vice versa.
                refChildX._prevNode = newChildX;
                newChildX._nextNode = refChildX;
            }
        }
        return newChild;
    }
   

    public final synchronized Node removeChild( Node oldChild )
        throws DOMException
    {
        NodeImpl oldChildX;
        int     i;
        
        // Make sure the node is not read-only.
        // Throw exception if children not supported by derived class.
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        
        // Cast refChild to NodeImpl, making sure it is a child of this node.
        oldChildX = castOldChild( oldChild );
        
        // We're going to mess with this child node, so make sure no other
        // thread is touching it
        synchronized ( oldChild ) {
            // Need to tell all the iterators that might be observing the
            // child node that the child node is removed from the current
            // tree. The iterators will reflect the changed by selecting
            // a different child to point to. Interesting iterators are
            // those the observer the tree underneath this node and all its
            // parents.
            notifyIterators( oldChild );
            
            // Child becomes orphan. It is no longer first or last child of
            // this node. Removed from linked list.
            oldChildX._parent = null;
            if ( _firstChild == oldChildX )
                _firstChild = oldChildX._nextNode;
            if ( _lastChild == oldChildX )
                _lastChild = oldChildX._prevNode;
            if ( oldChildX._prevNode != null )
                oldChildX._prevNode._nextNode = oldChildX._nextNode;
            if ( oldChildX._nextNode != null )
                oldChildX._nextNode._prevNode = oldChildX._prevNode;
            oldChildX._prevNode = null;
            oldChildX._nextNode = null;
        }
        return oldChild;
    }
 

    public final synchronized Node replaceChild( Node newChild, Node oldChild )
        throws DOMException
    {
        // Node arguments must be casted to NodeEx in order to operate on them.
        NodeImpl newChildX;
        NodeImpl oldChildX;
        
        // Make sure the node is not read-only.
        // Throw exception if children not supported by derived class.
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        
        // Cast newChild to NodeImpl and make sure it can be inserted to this
        // node. Cast oldChild to NodeImpl, making sure it is a child of this
        // node.
        if ( newChild != null )
            newChildX = castNewChild( newChild );
        oldChildX = castOldChild( oldChild );
        
        // We're going to mess with this child node, so make sure no other
        // thread is touching it
        synchronized ( oldChild ) {
            if ( newChild != null ) {
                // .. or this
                synchronized ( newChild ) {
                    // Lazy implementation adds newChild before oldChild and
                    // then takes oldChild away. Might be a touch slowed,
                    // but is way more reliable.
                    insertBefore( newChild, oldChild );
                    removeChild( oldChild );
                }
            } else {
                // The case of just removing the old child, when the new one
                // is null.
                removeChild( oldChild );
            }
        }
        return oldChild;
    }
    

    /**
     * Checks whether <TT>newChild</TT> can be added to this node as a child,
     * and if so, performs a necessary cast. <TT>newChild</TT> cannot be null
     * and must belong to this DOM. It is impossible to transfer nodes between
     * different DOM implementations.
     * <P>
     * The following rules govern the allowed <TT>newChild</TT> types:
     * <UL>
     * <LI>Parent is an {@link Attr}, <TT>newChild</TT> must be
     *  either a {@link Text} or an {@link EntityReference}
     * <LI>Parent is a {@link DocumentType}, <TT>newChild</TT> must be
     *  either an {@link Entity} or a {@link Notation}.
     * <LI>Parnet is any other node type, <TT>newChild</TT> must be an {@link
     *  Element}, a {@link CharacterData} derived type, a {@link
     *  DocumentFragment}, an {@link EntityReference} or a {@link
     *  ProcessingInstruction}.
     * </UL>
     * Any deviation will throw an exception.
     * 
     * @param newChild New child node
     * @return <TT>newChild</TT> cast to type {@link NodeImpl}
     * @throws org.w3c.dom.DOMException <TT>HIERARCHY_REQUEST_ERR</TT>
     *  <TT>newChild</TT> is null, does not belong to this DOM, or its node
     *  type is not supported for this parent
     */
    protected NodeImpl castNewChild( Node newChild )
        throws DOMException
    {
        if ( newChild == null )
            throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
                "Child reference is null." );

        // newChild must be Element, CDATASection, Text, Comment (all three
        // derived from CharacterData), DocumentFragment, EntityReference,
        // or ProcessingInstruction.
        if ( ! ( newChild instanceof NodeImpl ) )
            throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
                                        "Child is not a compatible type for this node." );
        if ( ! ( newChild instanceof Element ||
                 newChild instanceof CharacterData ||
                 newChild instanceof DocumentFragment ||
                 newChild instanceof EntityReference ||
                 newChild instanceof ProcessingInstructionImpl ) )
            throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
                                        "Child is not a compatible type for this node." );
        return (NodeImpl) newChild;
    }
    
    
    /**
     * Checks whether <TT>oldChild</TT> is a direct child of this node, and
     * if so, performs a necessary cast. <TT>oldChild</TT> cannot be null.
     * 
     * @param oldChild Old child node
     * @return <T>oldChild</TT> cast to type {@link NodeImpl}
     * @throws org.w3c.dom.DOMException <TT>NOT_FOUND_ERR</TT>
     *  <TT>oldChild</TT> is null, or not a direct child of this node
     */
    protected final NodeImpl castOldChild( Node oldChild )
        throws DOMException
    {
        if ( oldChild == null || ! ( oldChild instanceof NodeImpl ) ||
             ( (NodeImpl) oldChild )._parent != this )
            throw new DOMExceptionImpl( DOMException.NOT_FOUND_ERR,
                                        "Not a direct child of this node." );
        return (NodeImpl) oldChild;
    }
    

    /**
     * Returns the <TT>index</TT>-th child of this node. This method is used
     * exclusively by {@link NodeListImpl}.
     * 
     * @param index Index of child to retrieve
     * @return The child node or null
     * @see NodeListImpl#item(int)
     */
    final synchronized NodeImpl getChild( int index )
    {
        Node    node;
        
        if ( index < 0 )
            return null;
        node = getFirstChild();
        while ( node != null && index > 0 ) {
            node = node.getNextSibling();
            --index;
        }
        return (NodeImpl) node;
    }
    
    
    /**
     * Returns the number of children in this node.  This method is used
     * exclusively by {@link NodeListImpl}.
     * 
     * @return Number of childern in this node
     * @see NodeListImpl#getLength
     */
    final synchronized int getChildCount()
    {
        NodeImpl child;
        int      count = 0;
        
        child = _firstChild;
        while ( child != null ) {
            ++count;
            child = child._nextNode;
        }
        return count;
    }


    protected synchronized NodeIterator createNodeIterator( boolean tree,
                                                            int whatToShow, NodeFilter nodeFilter )
    {
        int                 i;
        NodeIteratorImpl    iterator;
        InnerIterator       inner;
        InnerIterator[]     newList;
        
        // Create the new iterator to point at this node's tree. Add the node
        // to the iterators list.
        iterator = new NodeIteratorImpl( this, tree, whatToShow, nodeFilter );
        inner = iterator.asInnerIterator();
        if ( _iterators == null ) {
            _iterators = new InnerIterator[ 1 ];
            _iterators[ 0 ] = inner;
        } else {
            for ( i = _iterators.length ; i-- > 0 ; )
                if ( _iterators[ i ] == inner )
                    break;
            if ( i < 0 ) {
                newList = new InnerIterator[ _iterators.length + 1 ];
                System.arraycopy( _iterators, 0, newList, 0, _iterators.length );
                newList[ _iterators.length ] = inner;
                _iterators = newList;
            }
        }
        return iterator;
    }


    public synchronized void removeInnerIterator( InnerIterator iterator )
    {
        int             i;
        InnerIterator[] newList;
        
        for ( i = _iterators.length ; i-- > 0 ; ) {
            if ( _iterators[ i ] == iterator ) {
                newList = new InnerIterator[ _iterators.length - 1 ];
                if ( i > 0 )
                    System.arraycopy( _iterators, 0, newList, 0, i );
                if ( i < newList.length )
                    System.arraycopy( _iterators, i + 1, newList, i, newList.length - i );
                _iterators = newList;
                return;
            }
        }
    }

    
    /**
     * Called to notify all the iterators created from this node that a
     * child of this node has been removed. Iterators that point at this
     * child might choose to select another child to point to. This method
     * is called before the child is removed.
     * <P>
     * The removed node is a direct child of this node. Affected iterators
     * are those that point at the document tree directly below this node,
     * or the tree below one of its parents. Other iterators are not affected
     * by the change. This method also performs a notification on all the
     * parents of this node.
     * 
     * @param removedChild The child node being removed
     */
    private void notifyIterators( Node removedChild )
    {
        ParentNodeImpl node;
        int            i;
        
        node = this;
        while ( node != null ) {
            if ( node._iterators != null )
                for ( i = node._iterators.length ; i -- > 0 ; )
                    ( (InnerIterator) node._iterators[ i ] ).removeNode( removedChild );
            node = (ParentNodeImpl) node._parent;
        }
    }

    
    /**
     * This one is called directly from {@link SAXBuilder}. It assumes that the new node has
     * just been created, is not locked or read-only, and is not a document fragment.
     * It assumes that this parent node is not read-only, locked or a document.
     *
     * @param newChild The new child node to append
     */
    final void appendNewChild( NodeImpl newChild )
    {
        // Node becomes child of this parent and part of this document.
        // Note that this code comes after the test for a DocumentFragment.
        // A fragment does not become part of this node, only its children.
        // The fragment becomes parent-less and child-less.
        newChild._parent = this;
        newChild.setOwnerDocument( _ownerDocument );

        // If the list has no end (it is empty) then newChild is added as
        // the only child in it.
        if ( _lastChild == null ) {
            _lastChild = newChild;
            _firstChild = newChild;
            newChild._prevNode = null;
            newChild._nextNode = null;
        } else {
            // newChild becomes the new end of the list, adjusting the previous
            // last child.
            _lastChild._nextNode = newChild;
            newChild._prevNode = _lastChild;
            newChild._nextNode = null;
            _lastChild = newChild;
        }
    }


    protected synchronized void cloneInto( NodeImpl into, boolean deep )
    {
        Node    child;
        
        super.cloneInto( into, deep );
        if ( deep ) {
            child = getFirstChild();
            while ( child != null ) {
                into.appendChild( (Node) child.cloneNode( true ) );
                child = child.getNextSibling();
            }
        }
    }


    public synchronized boolean equals( Object other )
    {
        ParentNodeImpl otherX;
        Node           child;
        Node           childX;
        boolean        equal;

        // If both objects are the same, return true. If one is null, or they
        // do not belong to the same class, return false. Equality is not
        // tested across different DOMs or different ClassLoaders.
        if ( this == other )
            return true;
        if ( other == null || ! ( other instanceof ParentNodeImpl ) ||
             other.getClass() != getClass() )
            return false;
        if ( ! super.equals( other ) )
            return false;
        synchronized ( other ) {
            otherX = (ParentNodeImpl) other;
            // Test for children, matching each children and also the
            // exact sequence of the children. This test is recursive.
            equal = true;
            child = this.getFirstChild();
            childX = otherX.getFirstChild();
            while ( equal && child != null && childX != null ) {
                equal = child.equals( childX );
                child = child.getNextSibling();
                childX = childX.getNextSibling();
            }
            if ( equal )
                equal = ( child == null && childX == null );
        }
        return equal;
    }

        
   /**
     * Hidden constructor creates a new node. Only one constructor is
     * supported, although cloning is also supported. Owner document must be
     * supplied except for {@link DocumentImpl} or {@link DocumentTypeImpl},
     * <P>
     * If <TT>checkName</TT> is true, the supplied named is assumed to be a
     * valid XML name token, one that can contain any Unicode letters and
     * digits, must start with a letter, and may also contain hyphen,
     * underscore, digit or colon.
     * 
     * @param owner Document owner of this node, or null
     * @param name Name of node
     * @param value Initial value of node or null
     * @throws org.w3c.dom.DOMException <TT>INVALID_CHARACTER_ERR</TT>
     *  Node name cannot contain whitespaces or non-printable characters
     */
    protected ParentNodeImpl( DocumentImpl owner, String name, String value  )
        throws DOMException
    {
        super( owner, name, value );
    }


    /**
     * The children of this node are arranged in a doubly linked lists.
     * This reference identifies the first child in the list.
     */
    private NodeImpl        _firstChild;

    
    /**
     * The children of this node are arranged in a doubly linked lists.
     * This reference identifies the last child in the list.
     */
    private NodeImpl        _lastChild;
    
    
    /**
     * Holdes a list of iterators that are observing this node of its
     * childern.
     */
    private InnerIterator[] _iterators;

 
}
