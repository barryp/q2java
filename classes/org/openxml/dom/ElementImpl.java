package org.openxml.dom;

/**
 * org/openxml/dom/ElementImpl.java
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
import org.openxml.dom.iterator.ElementTagFilter;


/**
 * The most common node type, {@link org.w3c.dom.Element} inherits the generic
 * {@link Node} interface and adds support for retrieving and setting attributes
 * either as nodes or as strings.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link org.w3c.dom.Node#ELEMENT_NODE}
 * <LI>Node supports childern
 * <LI>Node has no value
 * <LI>Node has attributes
 * </OL>
 * <P>
 * To speed up implementation, all attributes are implemented as double-linked 
 * list implemented using {@link NodeImpl#_parent}, {@link NodeImpl#_nextNode} and
 * {@link NodeImpl#_prevNode}. This support is provided to through {@link
 * #getNamedAttr}, {@link #appendAttr} and {@link #removeAttr} methods.
 *
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Element
 * @see org.w3c.dom.Attr
 * @see org.w3c.dom.NamedNodeMap
 * @see AttrImpl
 */
public class ElementImpl
	extends NodeImpl
	implements Element
{
	
	
	/**
	 * The attributes of this element are arranged in a doubly linked lists.
	 * This reference identifies the first attribute in the list.
	 */
	AttrImpl            _firstAttr;

	
	/**
	 * The attributes of this element are arranged in a doubly linked lists.
	 * This reference identifies the last attribute in the list.
	 */
	AttrImpl            _lastAttr;
	
	
	/**
	 * Counts how many attributes belong to this element.
	 */
	int                 _attrCount;
	
	
	/**
	 * Attribute node map that was requested and used to traverse the
	 * attributes held by this element. There is only need for one such
	 * map, as it will provide a singular synchronized interface directly
	 * to this element.
	 */
	private AttrNodeMap _attrNodeMap;


	/**
	 * Constructor requires only owner document and tag name of element.
	 * Tag name is case sensitive for XML, but converted to upper case for
	 * HTML documents.
	 * 
	 * @param owner Owner document of this element
	 * @param name The tag name of the element
	 */
	public ElementImpl( DocumentImpl owner, String name )
	{
		// Make sure that all element tag names are converted to upper case
		// for some documents (HTML).
		super( owner, name, null, true );
	}
	/**
	 * Append <TT>newAttr</TT> as the last attribute of this element.
	 * If <TT>newAttr</TT> is not an attribute of this DOM, or is already in use
	 * by some element, an exception is thrown.
	 * 
	 * @param newAttr The new attribute to add
	 * @return The newly added attribute
	 * @throws org.w3c.dom.DOMException <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
	 *  Node is read-only and cannot be modified
	 * @throws org.w3c.dom.DOMException <TT>WRONG_DOCUMENT_ERR</TT>
	 *  <TT>newAttr</TT> is not an attribute in this DOM
	 * @throws org.w3c.dom.DOMException <TT>INUSE_ATTRIBUTE_ERR</TT>
	 *  <TT>newAttr</TT> is already in use by some other element
	 */
	private final synchronized AttrImpl appendAttr( AttrImpl newAttr )
	{
		// Make sure the node is not read-only and the attribute can be added to it.
		if ( isReadOnly() )
			throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
		if ( newAttr == null )
			throw new DOMExceptionImpl( DOMException.WRONG_DOCUMENT_ERR,
				"Attribute does not belong to same document as this element." );
		if ( newAttr._parent == this )
			return newAttr;
		if ( newAttr._parent != null )
			throw new DOMExceptionImpl( DOMException.INUSE_ATTRIBUTE_ERR );

		// We're going to mess with this attribute, so make sure no other thread
		// is touching it
		synchronized ( newAttr )
		{
			newAttr._parent = this;
			newAttr.setOwnerDocument( getOwnerDocument() );

			// If the list has no end (it is empty) then newAttr is added as the
			// only attribute in it.
			if ( _lastAttr == null )
			{
				_lastAttr = newAttr;
				_firstAttr = newAttr;
				newAttr._prevNode = null;
				newAttr._nextNode = null;
			}
			// newAttr becomes the new end of the list, adjusting the previous
			// last attribute.
			else
			{
				_lastAttr._nextNode = newAttr;
				newAttr._prevNode = _lastAttr;
				newAttr._nextNode = null;
				_lastAttr = newAttr;
			}
			// Keep this count accurate at all times.                
			++ _attrCount;                
		}
		return newAttr;
	}
	public final Object clone()
	{
		ElementImpl clone;
		
		clone = (ElementImpl) _ownerDocument.createElement( getNodeName() );
		cloneInto( clone, true );
		return clone;
	}
	protected synchronized void cloneInto( NodeImpl into, boolean deep )
	{
		AttrImpl    attr;
		ElementImpl intoX;
		
		super.cloneInto( into, deep );
		intoX = (ElementImpl) into;
		// Duplicate all attributes . Note that attributes are duplicated with deep
		// cloning, since an attribute might contain Text and EntityReference nodes.
		 intoX._firstAttr = null;
		intoX._lastAttr = null;
		intoX._attrCount = 0;
		attr = _firstAttr;
		while ( attr != null )
		{
			intoX.appendAttr( (AttrImpl) attr.cloneNode( true ) );
			// Note: getNextSibling() will return null.
			attr = (AttrImpl) attr._nextNode;
		}
	}
	public final Node cloneNode( boolean deep )
	{
		ElementImpl clone;
			
		clone = (ElementImpl) _ownerDocument.createElement( getNodeName() );
		cloneInto( clone, deep );
		return clone;
	}
	public synchronized boolean equals( Object other )
	{
		ElementImpl    otherX;
		AttrImpl    attr;
		boolean        equal;
		
		// If both objects are the same, return true. If one is null, or they
		// do not belong to the same class, return false. Equality is not
		// tested across different DOMs or different ClassLoaders.
		if ( this == other )
			return true;
		if ( other == null || ! ( other instanceof ElementImpl ) )
			return false;
		if ( ! super.equals( other ) )
			return false;
		synchronized ( other )
		{
			otherX = (ElementImpl) other;
			equal = ( this._attrCount == otherX._attrCount );
			if ( equal )
			{
				// Test for attributes first, this is the faster test and
				// can tell elements apart quite easily. Since attributes might
				// be out of sequence, retrieve attributes by name not by sequence.
				// This test is recursive to some degree.
				attr = _firstAttr;
				while ( equal && attr != null )
				{
					equal = otherX.getNamedAttr( attr.getNodeName() ) != null &&
							otherX.getNamedAttr( attr.getNodeName() ).equals( attr );
					// Note: getNextSibling() will return null.
					attr = (AttrImpl) attr._nextNode;
				}
			}
		}
		return equal;
	}
	public final synchronized String getAttribute( String name )
	{
		AttrImpl    attr;
		
		// Look for the named attribute and return it's value.
		attr = getNamedAttr( name );
		if ( attr == null )
			return null;
		return attr.getValue();
	}
	public final Attr getAttributeNode( String name )
	{
		return getNamedAttr( name );
	}
	public synchronized final NamedNodeMap getAttributes()
	{
		// We only need a single instance of the attribute node map,
		// since it is fully synchronized and only allows one form of
		// access to the attributes in this list.
		if ( _attrNodeMap == null )
			_attrNodeMap = new AttrNodeMap( this );
		return _attrNodeMap;
	}
	/**
	 * Returns a list of elements extracted based on their tag name (or all of
	 * them if the tag name is "*"). The returned list is a snapshot of the
	 * element's contents at the time of calling. Subsequent updates to the
	 * element are not reflected in the list. This might result in inaccuracies
	 * when working from multiple threads.
	 * 
	 * @param tagName The element tag name to look for or "*" for all elements
	 * @return A snapshot of the named elements contained within this element
	 */
	public final NodeList getElementsByTagName( String tagName )
	{
		return (NodeList) createTreeIterator( TW_ELEMENT, ElementTagFilter.lookup( tagName ) );
	}
	/**
	 * Returns the named attribute or null if attribute not found.
	 * 
	 * @param name The name of the attribute to return
	 * @return The named attribute or null
	 */
	final AttrImpl getNamedAttr( String name )
	{
		AttrImpl    attr;

		attr = _firstAttr;
		while ( attr != null )
		{
			if ( attr.getName().equals( name ) )
				return attr;
			// Note: getNextSibling() will return null.
			attr = (AttrImpl) attr._nextNode;
		}
		return null;
	}
	public final short getNodeType()
	{
		return ELEMENT_NODE;
	}
	/**
	 * Returns the name of the tag, same as calling {@link #getNodeName}.
	 * In XML documents, the return value preserves case. In HTML documents,
	 * the return value is always upper case regardless of the original value.
	 * 
	 * @return Tag name
	 */
	public final String getTagName()
	{
		return getNodeName();
	}
	public final synchronized void normalize()
	{
		Node    node;
		Node    next;
		
		// Run through all child nodes of this element. If a particular child
		// is an Element, normalize it. If a particular child is a Text and is
		// followed by a second Text node, concatenate the data from the second
		// to the first and remove the second node.
		node = getFirstChild();
		while ( node != null )
		{
			if ( node instanceof ElementImpl )
				( (ElementImpl) node ).normalize();
			else
			if ( node instanceof TextImpl )
			{
				next = node.getNextSibling();
				while ( next != null && next instanceof TextImpl )
				{
					( (TextImpl) node ).appendData( ( (TextImpl) next ).getData() );
					removeChild( next );
					next = node.getNextSibling();
				}
			}
			node = node.getNextSibling();
		}
	}
	/**
	 * Remove <TT>oldAttr</TT> from this element. If <TT>oldAttr</TT> is not an
	 * attribute of this element, an exception is thrown.
	 * 
	 * @param oldAttr The attribute to remove
	 * @return The removed attribute
	 * @throws org.w3c.dom.DOMException <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
	 *  Node is read-only and cannot be modified
	 * @throws org.w3c.dom.DOMException <TT>NOT_FOUND_ERR</TT>
	 *  <TT>oldAttr</TT> is not an attribute of this element
	 */
	final AttrImpl removeAttr( AttrImpl oldAttr )
		throws DOMException
	{
		// Make sure the element is not read-only and the attribute belonds to it.
		if ( isReadOnly() )
			throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
		// Note: getParentNode() will return null.
		if ( oldAttr == null || oldAttr._parent != this )
			throw new DOMExceptionImpl( DOMException.NOT_FOUND_ERR, "Not an attribute of this element." );

		// We're going to mess with this attribute node, so make sure no other
		// thread is touching it
		synchronized ( oldAttr )
		{
			// Attribute becomes orphan. It is no longer first or last attribute of
			// this element. Removed from linked list.
			oldAttr._parent = null;
			if ( _firstAttr == oldAttr )
				_firstAttr = (AttrImpl) oldAttr._nextNode;
			if ( _lastAttr == oldAttr )
				_lastAttr = (AttrImpl) oldAttr._prevNode;
			if ( oldAttr._prevNode != null )
				oldAttr._prevNode._nextNode = oldAttr._nextNode;
			if ( oldAttr._nextNode != null )
				oldAttr._nextNode._prevNode = oldAttr._prevNode;
			oldAttr._prevNode = null;
			oldAttr._nextNode = null;
			// Keep this count accurate at all times.                
			-- _attrCount;                
		}
		return oldAttr;
	}
	public final synchronized void removeAttribute( String name )
	{
		AttrImpl    attr;
		
		attr = getNamedAttr( name );
		if ( attr != null )
			removeAttr( attr );
	}
	public final synchronized Attr removeAttributeNode( Attr oldAttr )
	{
		if ( isReadOnly() )
			throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
		if ( ! ( oldAttr instanceof AttrImpl ) )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"Node is not an attribute compatible with this element." );
		oldAttr = removeAttr( (AttrImpl) oldAttr );
		if ( oldAttr == null )
			throw new DOMExceptionImpl( DOMException.NOT_FOUND_ERR );
		return oldAttr;
	}
	public final synchronized void setAttribute( String name, String value )
		throws DOMException
	{
		AttrImpl    attr;
		
		if ( isReadOnly() )
			throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
		// If attribute value is null, might as well remove attribute. This will
		// either save space, or return the default value instead.
		if ( value == null )
			removeAttribute( name );
		else
		{
			// Get the named attribute and change it's value. If the attribute
			// does not exist, create a new attribute by that name and add it.
			// Call setValue() to assure correct behavior.
			attr = getNamedAttr( name );
			if ( attr == null )
			{
				attr = new AttrImpl( _ownerDocument, name, "" );
				appendAttr( attr );
			}
			attr.setValue( value );
		}
	}
	public final synchronized Attr setAttributeNode( Attr newAttr )
		throws DOMException
	{
		AttrImpl    oldAttr;
		
		if ( isReadOnly() )
			throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
		if ( newAttr == null || ! ( newAttr instanceof AttrImpl ) )
			throw new DOMExceptionImpl( DOMException.WRONG_DOCUMENT_ERR );
		// Note: getParentNode() will return null.
		if ( ( (AttrImpl) newAttr )._parent != null )
			throw new DOMExceptionImpl( DOMException.INUSE_ATTRIBUTE_ERR );
		synchronized ( newAttr )
		{
			oldAttr = getNamedAttr( newAttr.getName() );
			if ( oldAttr != null )
				removeAttr( oldAttr );
			appendAttr( (AttrImpl) newAttr );
		}
		return oldAttr;
	}
	public final void setNodeValue( String value )
	{
		throw new DOMExceptionImpl( DOMException.NO_DATA_ALLOWED_ERR,
			"This node type does not support values." );
	}
	protected boolean supportsChildern()
	{
		return true;
	}
	public String toString()
	{
		String    name;
		
		name = getTagName();
		if ( name.length() > 32 )
			name = name.substring( 0, 32 ) + "..";
		return "Element node: [" + name + "] (" + getChildCount() + " nodes, "
	    + _attrCount + " attrs)";
	}
}