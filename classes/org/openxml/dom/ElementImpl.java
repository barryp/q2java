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
 * + Extends ParentNSNodeImpl, removed supportChilds().
 * + Added namespace support with getElementsByTagNameNS(), get/set/remove-
 *   AttributeNS(), extending ParentNSNodeImpl, and constructor.
 * + Fixed clone() and equals().
 **/


package org.openxml.dom;
    

import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.NodeFilter;
import org.openxml.dom.traversal.*;
//import org.xml.sax.Locator;


/**
 * The most common node type, {@link Element} inherits the generic {@link Node}
 * interface and adds support for retrieving and setting attributes either as
 * nodes or as strings.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link Node#ELEMENT_NODE}
 * <LI>Node supports childern
 * <LI>Node has no value
 * <LI>Node has attributes
 * </OL>
 * <P>
 * To speed up implementation, all attributes are implemented as double-linked 
 * list implemented using <tt>_parent</tt>, <tt>_nextNode</tt> and <tt>_prevNode</tt>.
 * This support is provided to through <tt>appendAttr</tt> and <tt>removeAttr</tt>
 * methods.
 *
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.Element
 * @see org.w3c.dom.Attr
 * @see org.w3c.dom.NamedNodeMap
 * @see AttrImpl
 */
public class ElementImpl
    extends ParentNSNodeImpl
    implements Element
{
    
    
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
    

    public final void setNodeValue( String value )
    {
        throw new DOMExceptionImpl( DOMException.NO_DATA_ALLOWED_ERR,
                                    "This node type does not support values." );
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
    public NodeList getElementsByTagName( String tagName )
    {
        return (NodeList) createNodeIterator( true, NodeFilter.SHOW_ELEMENT,
                                              ElementFilter.lookup( null, tagName, false ) );
    }


    public NodeList getElementsByTagNameNS( String namespaceURI,
                                            String localName )
    {
        return (NodeList) createNodeIterator( true, NodeFilter.SHOW_ELEMENT,
                                              ElementFilter.lookup( namespaceURI, localName, false ) );
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

        
    public synchronized String getAttribute( String name )
    {
        return getAttributeNS( null, name );
    }


    public synchronized String getAttributeNS( String namespaceURI,
                                               String localName )
    {
        AttrImpl    attr;
        
        // Look for the named attribute and return it's value.
        attr = (AttrImpl) getAttributeNodeNS( namespaceURI, localName );
        if ( attr == null )
            return "";
        return attr.getValue();
    } 
    
    
    public final synchronized void setAttribute( String name, String value )
    {
        setAttributeNS( null, name, value );
    }


    public final synchronized void setAttributeNS( String namespaceURI,
                                                   String localName, String value )
        throws DOMException
    {
        AttrImpl    attr;
        
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        // If attribute value is null, might as well remove attribute.
        // This will either save space, or return the default value instead.
        if ( value == null )
            removeAttributeNS( namespaceURI, localName );
        else {
            // Get the named attribute and change it's value. If the attribute
            // does not exist, create a new attribute by that name and add it.
            // Call setValue() to assure correct behavior.
            attr = (AttrImpl) getAttributeNodeNS( namespaceURI, localName );
            if ( attr == null ) {
                checkName( localName );
                attr = new AttrImpl( _ownerDocument, namespaceURI, localName, null );
                appendAttr( attr );
            }
            // Set value now andnot in the constructor to make sure the
            // attribute is specified.
            attr.setValue( value );
        }
    }
    
    
    public final synchronized void removeAttributeNS( String namespaceURI,
                                                      String localName )
    {
        AttrImpl    attr;
        
        attr = (AttrImpl) getAttributeNodeNS( namespaceURI, localName );
        if ( attr != null )
            removeAttr( attr );
    }
    
    
    public final synchronized void removeAttribute( String name )
    {
        removeAttributeNS( null, name );
    }
    
    
    public Attr getAttributeNode( String name )
    {
        return getAttributeNodeNS( null, name );
    }
    
    
    public synchronized Attr getAttributeNodeNS( String namespaceURI,
                                                 String localName )
    {
        AttrImpl    attr;
        
        attr = _firstAttr;
        if ( namespaceURI != null && namespaceURI.length() > 0 ) {
            while ( attr != null ) {
                if ( attr.getNamespaceURI().equals( namespaceURI ) &&
                     attr.getLocalName().equals( localName ) )
                    return attr;
                // Note: getNextSibling() will return null.
                attr = attr.getNextAttr();
            }
        } else {
            while ( attr != null ) {
                if ( attr.getName().equals( localName ) )
                    return attr;
                // Note: getNextSibling() will return null.
                attr = attr.getNextAttr();
            }
        }
        return null;
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
        synchronized ( newAttr ) {
            oldAttr = (AttrImpl) getAttributeNodeNS( null, newAttr.getName() );
            if ( oldAttr != null )
                removeAttr( oldAttr );
            appendAttr( (AttrImpl) newAttr );
        }
        return oldAttr;
    }
    
    
    public final synchronized Attr setAttributeNodeNS( Attr newAttr )
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
        synchronized ( newAttr ) {
            oldAttr = (AttrImpl) getAttributeNodeNS( ( (AttrImpl) newAttr ).getNamespaceURI(),
                                                     ( (AttrImpl) newAttr ).getLocalName() );
            if ( oldAttr != null )
                removeAttr( oldAttr );
            appendAttr( (AttrImpl) newAttr );
        }
        return oldAttr;
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
    
    
    /**
     * For optimized used by {@link DOMBuilder}, adds a new attribute to this element.
     * It is assumed that an attribute of the same name does not exist for this element,
     * and that both attribute and this element are not read-only and not locked.
     *
     * @param namespaceURI The namespace URI if specified, or null
     * @param qualifiedName The qualified attribute name
     * @param value The specified attribute value
     */
    final void addNewAttribute( String namespaceURI, String qualifiedName, String value )
        throws DOMException
    {
        AttrImpl newAttr;
        
        // No need to check node name, assuming parser checked it.
        newAttr = new AttrImpl( _ownerDocument, namespaceURI, qualifiedName, value );
        newAttr._parent = this;
        newAttr.setOwnerDocument( getOwnerDocument() );
        
        // If the list has no end (it is empty) then newAttr is added as
        // the only attribute in it.
        if ( _lastAttr == null ) {
            _lastAttr = newAttr;
            _firstAttr = newAttr;
            newAttr._prevNode = null;
            newAttr._nextNode = null;
        } else {
            // newAttr becomes the new end of the list, adjusting the previous
            // last attribute.
            _lastAttr._nextNode = newAttr;
            newAttr._prevNode = _lastAttr;
            newAttr._nextNode = null;
            _lastAttr = newAttr;
        }
    }
    
    
    public final synchronized void normalize()
    {
        // Normalize children
        super.normalize();
        // Normalize attributes
        AttrImpl    attr;
        
        attr = _firstAttr;
        while ( attr != null ) {
            attr.normalize();
            attr = attr.getNextAttr();
        }
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
        synchronized ( other ) {
            otherX = (ElementImpl) other;
            // Test for attributes first, this is the faster test and
            // can tell elements apart quite easily. Since attributes might
            // be out of sequence, retrieve attributes by name not by sequence.
            // This test is recursive to some degree.
            attr = _firstAttr;
            equal = true;
            while ( equal && attr != null ) {
                equal = otherX.getAttributeNodeNS( null, attr.getNodeName() ) != null &&
                    otherX.getAttributeNodeNS( null, attr.getNodeName() ).equals( attr );
                // Note: getNextSibling() will return null.
                attr = attr.getNextAttr();
            }
        }
        return equal;
    }
    
    
    public Object clone()
    {
        ElementImpl clone;
        
        clone = (ElementImpl) getOwnerDocument().createElement( getNodeName() );
        cloneInto( clone, true );
        return clone;
    }

    
    public Node cloneNode( boolean deep )
    {
        ElementImpl clone;
        
        clone = (ElementImpl) getOwnerDocument().createElement( getNodeName() );
        cloneInto( clone, deep );
        return clone;
    }

    
    public String toString()
    {
        String    name;
        
        name = getTagName();
        if ( name.length() > 32 )
            name = name.substring( 0, 32 ) + "..";
        return "Element node: [" + name + "] (" + getChildCount() + " nodes";
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
        attr = _firstAttr;
        while ( attr != null ) {
            intoX.appendAttr( (AttrImpl) attr.cloneNode( true ) );
            // Note: getNextSibling() will return null.
            attr = attr.getNextAttr();
        }
    }
    

    /**
     * Append <TT>newAttr</TT> as the last attribute of this element.
     * If <TT>newAttr</TT> is not an attribute of this DOM, or is already
     * in use by some element, an exception is thrown.
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
        // Make sure the node is not read-only and the attribute can be added
        // to it.
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        if ( newAttr == null )
            throw new DOMExceptionImpl( DOMException.WRONG_DOCUMENT_ERR,
                                        "Attribute does not belong to same document as this element." );
        if ( newAttr._parent == this )
            return newAttr;
        if ( newAttr._parent != null )
            throw new DOMExceptionImpl( DOMException.INUSE_ATTRIBUTE_ERR );
        
        // We're going to mess with this attribute, so make sure no other
        // thread is touching it
        synchronized ( newAttr ) {
            newAttr._parent = this;
            newAttr.setOwnerDocument( getOwnerDocument() );
            
            // If the list has no end (it is empty) then newAttr is added as
            // the only attribute in it.
            if ( _lastAttr == null ) {
                _lastAttr = newAttr;
                _firstAttr = newAttr;
                newAttr._prevNode = null;
                newAttr._nextNode = null;
            } else {
                // newAttr becomes the new end of the list, adjusting the previous
                // last attribute.
                _lastAttr._nextNode = newAttr;
                newAttr._prevNode = _lastAttr;
                newAttr._nextNode = null;
                _lastAttr = newAttr;
            }
        }
        return newAttr;
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
    final synchronized AttrImpl removeAttr( AttrImpl oldAttr )
        throws DOMException
    {
        // Make sure the element is not read-only and the attribute belonds
        // to it.
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        // Note: getParentNode() will return null.
        if ( oldAttr == null || oldAttr._parent != this )
            throw new DOMExceptionImpl( DOMException.NOT_FOUND_ERR, "Not an attribute of this element." );
        
        // We're going to mess with this attribute node, so make sure no other
        // thread is touching it
        synchronized ( oldAttr ) {
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
        }
        return oldAttr;
    }

    
    /**
     * Constructor requires only owner document, optional namespace URI and
     * qualified name. 
     * HTML documents.
     * 
     * @param owner Owner document of this element
     * @param namespaceURI The namespace URI, or null
     * @param qualifiedName The qualified name of this element
     */
    public ElementImpl( DocumentImpl owner, String namespaceURI,
                        String qualifiedName )
    {
        super( owner, namespaceURI, qualifiedName, null );
    }
 
   
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
     * Attribute node map that was requested and used to traverse the
     * attributes held by this element. There is only need for one such
     * map, as it will provide a singular synchronized interface directly
     * to this element.
     */
    private AttrNodeMap _attrNodeMap;
    
    
}








