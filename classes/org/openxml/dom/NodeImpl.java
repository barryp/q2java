package org.openxml.dom;

/**
 * org/openxml/dom/NodeImpl.java
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
import org.w3c.dom.fi.*;
import org.openxml.dom.iterator.*;


/**
 * {@link org.w3c.dom.Node} is the primary class in the DOM, representing a single
 * node in the document tree. All other classes derived and extend their major
 * functionality from this class.
 * <P>
 * This class is abstract. All derived classes must extend at the least the two
 * abstract methods {@link #getNodeType} amd {@link #supportsChildern}. In addition,
 * derived classes might wish to extend {@link #clone(NodeImpl,boolean)},
 * {@link #equals} and {@link #toString()} and other methods as necessary.
 * Many methods cannot be extended.
 * <P>
 * For speed and consistency, this is a "kitchen sink" class. It implements most
 * of the functionality for all derived class types.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Node
 * @see org.w3c.dom.fi.NodeEx
 * @see NodeListImpl
 */
public abstract class NodeImpl
	implements Node, NodeEx, Cloneable
{
	
	
	/**
	 * Element declaration node. Not part of the DOM, identifies an element
	 * declaration node appearing in the DTD.
	 */
	public static final short    ELEMENT_DECL_NODE = 13;

	
	/**
	 * Attributes list declaration node. Not part of the DOM, identifies an
	 * attributes list declaration node appearing in the DTD..
	 */
	public static final short    ATTLIST_DECL_NODE = 14;

	
	/**
	 * Parameter entity declaration node. Not part of the DOM, identifies an
	 * internal or external parameter entity declaration node appearing in the
	 * DTD (see {@link org.openxml.dom.ParamEntity}).
	 */
	public static final short    PARAM_ENTITY_NODE = 15;
	
   
	/**
	 * This node ia part of a double-linked list that belongs to its parent.
	 * This reference identifies the next child in the list. Class access
	 * required by derived classes.
	 */
	NodeImpl                _nextNode;

	
	/**
	 * This node ia part of a double-linked list that belongs to its parent.
	 * This reference identifies the previous child in the list. Class access
	 * required by derived classes.
	 */
	NodeImpl                _prevNode;
	
	
	/**
	 * The parent of this node or null if the node has no parent. Class access
	 * required by derived classes.
	 */
	NodeImpl                _parent;


	/**
	 * The document owner of this node, or the document itself. If the node belongs
	 * to any document, this will point to that document. For a document this will
	 * point at the document itself ({@link #getOwnerDocument} will return null,
	 * though). Class access required by derived classes.
	 */
	DocumentImpl            _ownerDocument;

	
	/**
	 * The name of this node. All nodes have names, some are dynamic (e.g. the
	 * tag name of an element), others are static (e.g. "#document").
	 */
	private String            _nodeName;
	
	
	/**
	 * The value of this node. Not all nodes support values and this might be
	 * null for some nodes.
	 */
	private String            _nodeValue;
	
	
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
	 * Counts how many children nodes belong to this parent. Used to speed up
	 * some checks.
	 */
	private int             _childsCount;

	
	/**
	 * True if this node is read-only and its contents cannot be modified.
	 */
	private boolean         _readOnly;
	
	
	/**
	 * Holdes a list of iterators that are observing this node of its
	 * childern.
	 */
	private InnerIterator[] _iterators;


	/**
	 * Hidden constructor creates a new node. Only one constructor is supported,
	 * although cloning is also supported. Owner document must be supplied except
	 * for {@link DocumentImpl} in which case the document itself becomes its
	 * owner. Name must be supplied, either dynamic or static (e.g. "#document#").
	 * <P>
	 * If <TT>checkName</TT> is true, the supplied named is assumed to be a valid
	 * XML name token, one that can contain any Unicode letter and digit, must
	 * start with a letter, and may also contain hyphen, underscore, digit or colon.
	 * 
	 * @param owner Document owner of this node, or null
	 * @param name Name of node
	 * @param value Initial value of node or null
	 * @param checkName True if name is an XML name token
	 * @throws org.w3c.dom.DOMException <TT>INVALID_CHARACTER_ERR</TT>
	 *  Node name cannot contain whitespaces or non-printable characters
	 */
	protected NodeImpl( DocumentImpl owner, String name, String value,
						boolean checkName )
		throws DOMException
	{
		char    ch;
		int        i;

		if ( name == null )
			throw new NullPointerException( "Argument 'name' is null." );
		_nodeName = name;
		_ownerDocument = owner;
		// Check the node name one character at a time to assure that no
		// illegal characters are used. Node name must conform to Name token
		// as defined in XML spec, including use of all Unicode letters and
		// digits.
		if ( checkName && name.length() > 0 )
		{
			ch = name.charAt( 0 );
			if ( ! Character.isLetter( ch ) && ch != '_' && ch != ':' )
				throw new DOMExceptionImpl( DOMException.INVALID_CHARACTER_ERR );
			for ( i = 1 ; i < name.length() ; ++i )
			{
				ch = name.charAt( 1 );
				if ( ! Character.isLetterOrDigit( ch ) &&
					 ch != '_' && ch != ':' && ch != '-' && ch != '.' )
					throw new DOMExceptionImpl( DOMException.INVALID_CHARACTER_ERR );
			}
		}
		if ( value != null )
			setNodeValue( value );
	}
	/**
	 * Insert <TT>newChild</TT> as the last child of this parent.
	 * <P>
	 * If <TT>newChild</TT> is null, <TT>newChild</TT> does not belong to this DOM,
	 * or childern are not supported by this node type, an exception is thrown.
	 * <P>
	 * <TT>newChild</TT> is removed from its original parent before adding to this
	 * parent. If <TT>newChild</TT> is a {@link org.w3c.dom.DocumentFragment}, all
	 * its children are inserted one by one into this parent.
	 * 
	 * @param newChild The new child to add
	 * @return The newly inserted child
	 * @throws org.w3c.dom.DOMException <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
	 *  Node is read-only and cannot be modified
	 * @throws org.w3c.dom.DOMException <TT>HIERARCHY_REQUEST_ERR</TT>
	 *  Children are not supported by this node type, or <TT>newChild</TT> is not
	 *  a compatible type for this node
	 * @see #castNewChild
	 * @see #castOldChild
	 */
	public final synchronized Node appendChild( Node newChild )
	{
		// Node arguments must be casted to NodeEx in order to operate on them.
		NodeImpl newChildX;

		// Make sure the node is not read-only.
		// Throw exception if children not supported by derived class.
		if ( isReadOnly() )
			throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
		if ( ! supportsChildern() )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"No childern supported by this node type." );

		// Cast newChild to NodeImpl and make sure it can be inserted to this node.
		newChildX = castNewChild( newChild );
		
		// We're going to mess with this child node, so make sure no other thread
		// is touching it
		synchronized ( newChild )
		{
			// If the newChild is already a child or some node, remove it first
			// before becoming child of this node. Make sure that parent is not
			// read-only.
			if ( newChildX._parent != null )
			{
				if ( newChildX._parent.isReadOnly() )
					throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
				newChildX._parent.removeChild( newChildX );
			}

			// Special case: newChild is a DocumentFragment and instead of adding
			// itself, all of its childs are added one by one.
			if ( newChildX instanceof DocumentFragment )
			{
				NodeImpl    nextChild;
				
				newChildX = newChildX._firstChild;
				while ( newChildX != null )
				{
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
			if ( _ownerDocument != null )
				newChildX.setOwnerDocument( _ownerDocument );

			// If the list has no end (it is empty) then newChild is added as the
			// only child in it.
			if ( _lastChild == null )
			{
				_lastChild = newChildX;
				_firstChild = newChildX;
				newChildX._prevNode = null;
				newChildX._nextNode = null;
			}
			// newChild becomes the new end of the list, adjusting the previous
			// last child.
			else
			{
				_lastChild._nextNode = newChildX;
				newChildX._prevNode = _lastChild;
				newChildX._nextNode = null;
				_lastChild = newChildX;
			}
			// Keep this count accurate at all times.                
			++ _childsCount;                
		}
		return newChild;
	}
	/**
	 * Checks whether <TT>newChild</TT> can be added to this node as a child, and
	 * if so, performs a necessary cast. <TT>newChild</TT> cannot be null and must
	 * belong to this DOM. It is impossible to transfer nodes between different
	 * DOM implementations.
	 * <P>
	 * The following rules govern the allowed <TT>newChild</TT> types:
	 * <UL>
	 * <LI>Parent is an {@link org.w3c.dom.Attr}, <TT>newChild</TT> must be either
	 *  a {@link org.w3c.dom.Text} or an {@link org.w3c.dom.EntityReference}
	 * <LI>Parent is a {@link org.w3c.dom.DocumentType}, <TT>newChild</TT> must be
	 *  either an {@link org.w3c.dom.Entity} or a {@link org.w3c.dom.Notation}.
	 * <LI>Parnet is any other node type, <TT>newChild</TT> must be an {@link
	 *  org.w3c.dom.Element}, a {@link org.w3c.dom.CharacterData} derived type,
	 *  a {@link org.w3c.dom.DocumentFragment}, an {@link
	 *  org.w3c.dom.EntityReference} or a {@link org.w3c.dom.ProcessingInstruction}.
	 * </UL>
	 * Any deviation will throw an exception.
	 * 
	 * @param newChild New child node
	 * @return <TT>newChild</TT> cast to type {@link NodeImpl}
	 * @throws org.w3c.dom.DOMException <TT>HIERARCHY_REQUEST_ERR</TT>
	 *  <TT>newChild</TT> is null, does not belong to this DOM, or its node type
	 *  is not supported for this parent
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
		if ( ! ( newChild instanceof Element || newChild instanceof CharacterData ||
				 newChild instanceof DocumentFragment || newChild instanceof EntityReference ||
				 newChild instanceof ProcessingInstructionImpl ) )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"Child is not a compatible type for this node." );
		return (NodeImpl) newChild;
	}
	/**
	 * Checks whether <TT>oldChild</TT> is a direct child of this node, and if so,
	 * performs a necessary cast. <TT>oldChild</TT> cannot be null.
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
	 * This clone method is called after a new node has been constructed to copy
	 * the contents of this node into the new one. It clones in contents but not
	 * in context, and guarantees that the cloned node will pass the equality
	 * test (see {@link #equals}).
	 * <P>
	 * <TT>into</TT> must be a valid node of the exact same class as this one.
	 * <TT>deep</TT> is true if deep cloning (includes all children nodes) is to
	 * be performed. If <TT>deep</TT> is false, the clone might not pass the
	 * equality test.
	 * <P>
	 * Derived classes override and call this method to add per-class variable
	 * copying. This method is called by {@link #cloneNode} and the default
	 * {@link java.lang.Object#clone} method.
	 * <P>
	 * Contents cloning duplicates the node's name and value, and its children.
	 * It does not duplicate it's context, that is, the node's parent or sibling.
	 * Initially a clone node has no parents or siblings. However, the node does
	 * belong to the same document, since all nodes must belong to some document.
	 * The cloned node is never read-only.
	 * 
	 * @param into A node into which to duplicate this one
	 * @param deep True if deep cloning is required
	 */
	protected synchronized void cloneInto( NodeImpl into, boolean deep )
	{
		Node    child;

		// Make sure no function messed up with the class types.
		if ( this.getClass() != into.getClass() )
			throw new IllegalArgumentException( "Argument 'into' not same type as this node." );
		
		// Duplicate node name and value.
		into._nodeName = _nodeName;
		into._nodeValue = _nodeValue;
		into._ownerDocument= _ownerDocument;
		if ( deep )
		{
			child = getFirstChild();
			while ( child != null )
			{
				into.appendChild( (Node) child.cloneNode( true ) );
				child = child.getNextSibling();
			}
		}
	}
	public synchronized NodeIterator createNodeIterator( boolean tree, int whatToShow, NodeFilter nodeFilter )
	{
		int                 i;
		NodeIteratorImpl    iterator;
		InnerIterator       inner;
		InnerIterator[]     newList;
		
		// Create the new iterator to point at this node's tree. Add the node
		// to the iterators list.
		iterator = new NodeIteratorImpl( this, tree, whatToShow, nodeFilter );
		inner = iterator.asInnerIterator();
		if ( _iterators == null )
		{
			_iterators = new InnerIterator[ 1 ];
			_iterators[ 0 ] = inner;
		}
		else
		{
			for ( i = _iterators.length ; i-- > 0 ; )
				if ( _iterators[ i ] == inner )
					break;
			if ( i < 0 )
			{
				newList = new InnerIterator[ _iterators.length + 1 ];
				System.arraycopy( _iterators, 0, newList, 0, _iterators.length );
				newList[ _iterators.length ] = inner;
				_iterators = newList;
			}
		}
		return iterator;
	}
	public NodeIterator createTreeIterator( int whatToShow, NodeFilter nodeFilter )
	{
		return createNodeIterator( true, whatToShow, nodeFilter );
	}
	/**
	 * Returns true if this node and <TTother</TT> are identical by content but
	 * not context. Test by content but not context adheres to the following:
	 * <UL>
	 * <LI>Both nodes must be of the same type, must have the same name and
	 *  value (case sensitive) and for elements, the same tag name
	 * <LI>For elements, both must have identical attribute list, although
	 *  attributes may be specified/unspecified and in different orders
	 * <LI>Both nodes must have no children, or must have the exact same
	 *  sequence of children; children are tested for equality by calling
	 *  {@link #equals} on each pair
	 * <LI>However, both nodes may reside in different documents, belong to
	 *  different parents, have different siblings, different read-only flags,
	 *  etc.
	 * <UL>
	 * It is guaranteed that the act of cloning a node will return a second
	 * node that will pass the test of equality.
	 * <p>
	 * Note that for large document roots, the equality operation can be very
	 * expensive.
	 * 
	 * @param other The other node to test for equality
	 * @return True if both nodes are equal based on their content but not
	 *  their context
	 */    
	public synchronized boolean equals( Object other )
	{
		NodeImpl    otherX;
		String        value;
		String        valueX;
		Node        child;
		Node        childX;
		boolean        equal;

		// If both objects are the same, return true. If one is null, or they
		// do not belong to the same class, return false. Equality is not
		// tested across different DOMs or different ClassLoaders.
		if ( this == other )
			return true;
		if ( other == null || ! ( other instanceof NodeImpl ) ||
			 other.getClass() != getClass() )
			return false;
		synchronized ( other )
		{
			otherX = (NodeImpl) other;
			// Test equality of name and value. Note that values might be null,
			// so equality tests for both being null, or both being equal in
			// value. Temporary variables are used as getNodeValue() might
			// be expensive operation if text substitution is involved.
			equal = this._nodeName.equals( otherX._nodeName );
			if ( equal )
			{
				value = this.getNodeValue();
				valueX = otherX.getNodeValue();
				if ( value != null && valueX != null )
					equal = value.equals( valueX );
				else
					equal = ( value == null && valueX == null );
			}
			// Test for children, matching each children and also the
			// exact sequence of the children. This test is recursive.
			equal = ( this._childsCount == otherX._childsCount );
			if ( equal )
			{
				child = this.getFirstChild();
				childX = otherX.getFirstChild();
				while ( equal && child != null && childX != null )
				{
					equal = child.equals( childX );
					child = child.getNextSibling();
					childX = childX.getNextSibling();
				}
				if ( equal )
					equal = ( child == null && childX == null );
			}
		}
		return equal;
	}
	/**
	 * Return attributes of node. Returns null unless node is of type {@link
	 * org.w3c.dom.Element}, in which case the returned {@link
	 * org.w3c.dom.NamedNodeMap} will provide access to all the element's
	 * attributes.
	 * 
	 * @return Attributes of node or null
	 */
	public NamedNodeMap getAttributes()
	{
		return null;
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
		
		if ( index < 0 || index > _childsCount )
			return null;
		node = getFirstChild();
		while ( node != null && index > 0 )
		{
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
	final int getChildCount()
	{
		return _childsCount;
	}
	/**
	 * Returns a {@link org.w3c.dom.NodeList} object that can be used to traverse
	 * this node's children. The node list is live, so every change to this node
	 * is reflected in it.
	 * <P>
	 * If children are not supported by the derived class, an exception is thrown.
	 * 
	 * @return {@link org.w3c.dom.NodeList} on this node
	 * @throws org.w3c.dom.DOMException HIERARCHY_REQUEST_ERR Childern not supported
	 *  by this node type
	 * @see org.w3c.dom.NodeList
	 * @see NodeListImpl
	 */
	public final NodeList getChildNodes()
	{
		// Throw exception if children not supported by derived class.
		if ( ! supportsChildern() )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"No childern supported by this node type." );
		return (NodeList) createNodeIterator( false, this.TW_ALL, null );
	}
	/**
	 * Returns the first child of the node. If node has no children, returns null.
	 * 
	 * @return First child or null 
	 */
	public final Node getFirstChild()
	{
		return _firstChild;
	}
	/**
	 * Returns the last child of the node. If node has no children, returns null.
	 * 
	 * @return Last child or null 
	 */
	public final Node getLastChild()
	{
		return _lastChild;
	}
	/**
	 * Returns the next sibling of this node. If node has no next siblings,
	 * returns null.
	 * 
	 * @return Next sibling or null 
	 */
	public Node getNextSibling()
	{
		return _nextNode;
	}
	/**
	 * Returns the name of the node, set from the constructor. Some derived classes
	 * do not have the notion of a name, and will return the same name each time.
	 * They should do so by setting the default name (e.g. <TT>"#comment"</TT>)
	 * in the constructor. This value is never null.
	 * 
	 * @see org.w3c.dom.Node#getNodeName
	 */
	public final String getNodeName()
	{
		return _nodeName;
	}
	/**
	 * Abstract method must be implemented by each node class.
	 * 
	 * @see org.w3c.dom.Node#getNodeType
	 */
	public abstract short getNodeType();
	/**
	 * Returns the value of the node. Depending on the node type, this value
	 * is either the node value (e.g. the text in {@link org.w3c.dom.Text}),
	 * or always null is node has no notion of a value (e.g. {@link
	 * org.w3c.dom.Element}). For complete list of which node types will return
	 * what, see {@link #setNodeValue}.
	 * 
	 * @return Value of node, null if node has no value
	 */
	public final String getNodeValue()
	{
		return _nodeValue;
	}
	public final Document getOwnerDocument()
	{
		if ( _ownerDocument != this )
			return _ownerDocument;
		else
			return null;
	}
	/**
	 * Returns the parent node of this node. Node may not necessarily have a
	 * parent node. If node has been created but not added to any other node,
	 * it will be parentless. The {@link org.w3c.dom.Document} node is always
	 * parentless.
	 * 
	 * @return Parent node of this node
	 */
	public Node getParentNode()
	{
		return _parent;
	}
	/**
	 * Returns the previous sibling of this node. If node has no previous siblings,
	 * returns null.
	 * 
	 * @return Previous sibling or null 
	 */
	public Node getPreviousSibling()
	{
		return _prevNode;
	}
	/**
	 * Return true if there are any childern to this node. Less intensive than
	 * calling {#link getChildNodes}.
	 * 
	 * @return True if node has any children
	 */
	public final boolean hasChildNodes()
	{
		return ( _firstChild != null );
	}
	/**
	 * Insert <TT>newChild</TT> in this parent, before the existing child
	 * <TT>refChild</TT>. If <TT>refChild</TT> is null, insert <TT>newChild</TT>
	 * as the last child of this parent, akin to calling {@link #appendChild}.
	 * <P>
	 * If <TT>newChild</TT> is null, <TT>newChild</TT> does not belong to this DOM,
	 * <TT>refChild</TT> is not a direct child of this node, or childern are not
	 * supported by this node type, an exception is thrown.
	 * <P>
	 * <TT>newChild</TT> is removed from its original parent before adding to this
	 * parent. If <TT>newChild</TT> is a {@link org.w3c.dom.DocumentFragment}, all
	 * its children are inserted one by one into this parent.
	 * 
	 * @param newChild The new child to add
	 * @param refChild Insert new child before this child, or insert at the end
	 *  if this child is null
	 * @return The newly inserted child
	 * @throws org.w3c.dom.DOMException <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
	 *  Node is read-only and cannot be modified
	 * @throws org.w3c.dom.DOMException <TT>HIERARCHY_REQUEST_ERR</TT>
	 *  Children are not supported by this node type, or <TT>newChild</TT> is not
	 *  a compatible type for this node
	 * @throws org.w3c.dom.DOMException <TT>NOT_FOUND_ERR</TT>
	 *  <TT>oldChild</TT> is not null and not a direct child of this node
	 * @see #castNewChild
	 * @see #castOldChild
	 */
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
		if ( ! supportsChildern() )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"No childern supported by this node type." );

		// If refChild is null, act as if appendChild was called.
		if ( refChild == null )
			return appendChild( newChild );
		
		// Cast newChild to NodeImpl and make sure it can be inserted to this node.
		// Cast refChild to NodeImpl, making sure it is a child of this node.
		newChildX = castNewChild( newChild );
		refChildX = castOldChild( refChild );
		
		// We're going to mess with this child node, so make sure no other thread
		// is touching it
		synchronized ( newChild )
		{
			// .. or this
			synchronized ( refChild )
			{
				// If the newChild is already a child or some node, remove it first
				// before becoming child of this node. Make sure that parent is not
				// read-only.
				if ( newChildX._parent != null )
				{
					if ( newChildX._parent.isReadOnly() )
						throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
					newChildX._parent.removeChild( newChildX );
				}

				// Special case: newChild is a DocumentFragment and instead of
				// inserting itself, all of its childs are inserted one by one.
				if ( newChildX instanceof DocumentFragment )
				{
					NodeImpl    nextChild;
					
					newChildX = newChildX._firstChild;
					while ( newChildX != null )
					{
						nextChild = newChildX._nextNode;
						insertBefore( newChildX, refChild );
						newChildX = nextChild;    
					}
					return newChild;
				}

				// Node becomes child of this parent and part of this document.
				// Note that this code comes after the test for a DocumentFragment.
				// A fragment does not become part of this node, only its children.
				// The fragment becomes parent-less and child-less.
				newChildX._parent = this;
				newChildX.setOwnerDocument( _ownerDocument );
				
				// If refChild is the first child, newChild becomes the first
				// child on the list.
				if ( _firstChild == refChildX )
					_firstChild = newChildX;
				// refChild is not the first child, so adjust the previous child
				// to point at newChild instead.
				if ( refChildX._prevNode != null )
				{
					newChildX._prevNode = refChildX._prevNode;
					refChildX._prevNode._nextNode = newChildX;
				}
				// Adjust the refChild to point at this child and vice versa.
				refChildX._prevNode = newChildX;
				newChildX._nextNode = refChildX;
				// Keep this count accurate at all times.                
				++ _childsCount;                
			}
		}
		return newChild;
	}
	/**
	 * Returns true if node is read-only and cannot be modified, or if node
	 * belongs to a read-only document.
	 * 
	 * @return True if node is read-only and cannot be modified
	 * @see #makeReadOnly
	 */
	public final boolean isReadOnly()
	{
		return _readOnly;
	}
	/**
	 * Renders this node read only, preventing it's contents from being modified.
	 * Attempts to modify the node's contents will throw an exception. The node's
	 * children are also made read-only.
	 */
	public final synchronized void makeReadOnly()
	{
		NodeImpl    child;
		
		_readOnly = true;
		// Make all children read-only as well: this allows us to lock a branch
		// but, for example, move it to a different tree.
		child = (NodeImpl) getFirstChild();
		while ( child != null )
		{
			child.makeReadOnly();
			child = (NodeImpl) child.getNextSibling();    
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
	protected void notifyIterators( Node removedChild )
	{
		NodeImpl    node;
		int         i;
		
		node = this;
		while ( node != null )
		{
			if ( node._iterators != null )
				for ( i = node._iterators.length ; i -- > 0 ; )
					( (InnerIterator) node._iterators[ i ] ).removeNode( removedChild );
			node = (NodeImpl) node._parent;
		}
	}
	/**
	 * Remove <TT>oldChild</TT> from this parent. If <TT>oldChild</TT> is not
	 * a direct child of this parent, or childern are not supported by this node
	 * type, an exception is thrown.
	 * 
	 * @param oldChild The child to remove
	 * @return The removed child
	 * @throws org.w3c.dom.DOMException <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
	 *  Node is read-only and cannot be modified
	 * @throws org.w3c.dom.DOMException <TT>HIERARCHY_REQUEST_ERR</TT>
	 *  Children are not supported by this node type
	 * @throws org.w3c.dom.DOMException <TT>NOT_FOUND_ERR</TT>
	 *  <TT>oldChild</TT> is not a direct child of this node
	 * @see #castOldChild
	 */
	public final synchronized Node removeChild( Node oldChild )
		throws DOMException
	{
		NodeImpl oldChildX;
		int     i;
		
		// Make sure the node is not read-only.
		// Throw exception if children not supported by derived class.
		if ( isReadOnly() )
			throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
		if ( ! supportsChildern() )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"No childern supported by this node type." );

		// Cast refChild to NodeImpl, making sure it is a child of this node.
		oldChildX = castOldChild( oldChild );

		// We're going to mess with this child node, so make sure no other thread
		// is touching it
		synchronized ( oldChild )
		{
			// Need to tell all the iterators that might be observing the
			// child node that the child node is removed from the current
			// tree. The iterators will reflect the changed by selecting
			// a different child to point to. Interesting iterators are
			// those the observer the tree underneath this node and all its
			// parents.
			notifyIterators( oldChild );
			
			// Child becomes orphan. It is no longer first or last child of this
			// node. Removed from linked list.
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
			// Keep this count accurate at all times.                
			-- _childsCount;                
		}
		return oldChild;
	}
	public synchronized void removeInnerIterator( InnerIterator iterator )
	{
		int             i;
		InnerIterator[] newList;
		
		for ( i = _iterators.length ; i-- > 0 ; )
			if ( _iterators[ i ] == iterator )
			{
				newList = new InnerIterator[ _iterators.length - 1 ];
				if ( i > 0 )
					System.arraycopy( _iterators, 0, newList, 0, i );
				if ( i < newList.length )
					System.arraycopy( _iterators, i + 1, newList, i, newList.length - i );
				_iterators = newList;
				return;
			}
	}
	/**
	 * Replace <TT>oldChild</TT> with <TT>newChild</TT>, adding the new child and
	 * removing the old one.
	 * <P>
	 * If <TT>newChild</TT> does not belong to this DOM, <TT>oldChild</TT> is not
	 * a direct child of this parent, or childern are not supported by this node
	 * type, an exception is thrown.
	 * <P>
	 * <TT>newChild</TT> is removed from its original parent before adding to this
	 * parent. If <TT>newChild</TT> is a {@link org.w3c.dom.DocumentFragment}, all
	 * its children are inserted one by one into this parent.
	 * 
	 * @param newChild The new child to add
	 * @param oldChild The old child to take away
	 * @return The old child
	 * @throws org.w3c.dom.DOMException <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
	 *  Node is read-only and cannot be modified
	 * @throws org.w3c.dom.DOMException <TT>HIERARCHY_REQUEST_ERR</TT>
	 *  Children are not supported by this node type, or <TT>newChild</TT> is not
	 *  a compatible type for this node
	 * @throws org.w3c.dom.DOMException <TT>NOT_FOUND_ERR</TT>
	 *  <TT>oldChild</TT> is not a direct child of this node
	 * @see #castNewChild
	 * @see #castOldChild
	 */
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
		if ( ! supportsChildern() )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"No childern supported by this node type." );

		// Cast newChild to NodeImpl and make sure it can be inserted to this node.
		// Cast oldChild to NodeImpl, making sure it is a child of this node.
		if ( newChild != null )
			newChildX = castNewChild( newChild );
		oldChildX = castOldChild( oldChild );
		
		// We're going to mess with this child node, so make sure no other thread
		// is touching it
		synchronized ( oldChild )
		{
			if ( newChild != null )
			{
				// .. or this
				synchronized ( newChild )
				{
					// Lazy implementation adds newChild before oldChild and then takes
					// oldChild away. Might be a touch slowed, but is way more reliable.
					insertBefore( newChild, oldChild );
					removeChild( oldChild );
				}
			}
			else
				// The case of just removing the old child, when the new one
				// is null.
				removeChild( oldChild );
		}
		return oldChild;
	}
	/**
	 * Changes the value of the node. Not all node types support the notion of
	 * a value. If the value is not supported by a particular node type, it will
	 * throw an exception when calling this method. The following table specifies
	 * which node types support values:
	 * <PRE>
	 * Element                  Not supported
	 * Attr                     Value supported
	 * Text                        Value supported
	 * CDATASection             Value supported
	 * EntityReference          Not supported
	 * Entity                   Not supported
	 * ProcessingInstruction    Value supported
	 * Comment                  Value supported
	 * Document                    Not supported
	 * DocumentType             Not supported
	 * DocumentFragment         Not supported
	 * Notation                 Not supported
	 * </PRE>
	 * For most node types, if the value is set to null, {@link #getNodeValue}
	 * will return an empty string instead.
	 * 
	 * @param value New value of node
	 * @throws org.w3c.dom.DOMExceptionImpl <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
	 *  Node is read-only and cannot be modified
	 * @throws org.w3c.dom.DOMExceptionImpl <TT>NO_DATA_ALLOWED_ERR</TT>
	 *  This node does not support a value
	 */
	public void setNodeValue( String value )
	{
		if ( isReadOnly() )
			throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
		_nodeValue = value == null ? "" : value;
	}
	protected synchronized void setOwnerDocument( Document owner )
	{
		Node    node;

		if ( owner == null )
			_ownerDocument = null;
		else
		{
			if ( ! ( owner instanceof DocumentImpl ) )
				throw new IllegalArgumentException( "Argument 'owner' not of compatible DOM class." );
			_ownerDocument = (DocumentImpl) owner;
		}
		node = getFirstChild();
		while ( node != null )
		{
			( (NodeImpl) node ).setOwnerDocument( owner );
			node = node.getNextSibling();
		}
	}
	/**
	 * Returns true if this node supports children. Other methods query this to
	 * determine whether to properly support childern, return null or throw an
	 * exception in response. The default method returns false.
	 * 
	 * @return True if childern supported by this node type
	 */
	boolean supportsChildern()
	{
		 return false;
	}
}