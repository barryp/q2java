/**
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


/**
 * Sep 23, 1999
 * + Modified to use getNextAttr() on Attr instead of accessing member
 *   variable directly.
 * + Added namespace support with getNamedItemNS() and removeNamedItemNS().
 */


package org.openxml.dom;


import org.w3c.dom.*;


/**
 * Used to traverse attributes of an element. This is a live list, meaning
 * that any change to the element is reflected in this list and vice versa.
 * This is implemented by iterating the element list in response to each
 * method call.
 * <P>
 * This map must be implemented outside of {@link ElementImpl} due to
 * method name conflict with certain HTML elements (specifically {@link
 * #getLength}).
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see NamedNodeMap
 */
public final class AttrNodeMap
    implements NamedNodeMap
{
    
    
    /**
     * Return the nth attribute of the element (zero based). If the
     * attribute does not exist, returns null. No exception is thrown if
     * index is negative.
     * 
     * @param index Index of attribute to return (zero based)
     * @return Attribute or null
     */
    public Node item( int index )
    {
        AttrImpl    attr;
        
        attr = _elem._firstAttr;
        while ( attr != null && index > 0 ) {
            // Note: getNextSibling() will return null.
            attr = attr.getNextAttr();
            -- index;
        }
        return attr;
    }
    
    
    /**
     * Return the number of attributes in this element.
     * 
     * @return Number of attributes
     */
    public int getLength()
    {
        AttrImpl attr;
        int      count;
        
        count = 0;
        attr = _elem._firstAttr;
        while ( attr != null ) {
            ++count;
            attr = attr.getNextAttr();
        }
        return count;
    }


    public synchronized Node getNamedItem( String name )
    {
        return _elem.getAttributeNodeNS( null, name );
    }


    public synchronized Node getNamedItemNS( String namespaceURI,
                                             String localName )
    {
        return _elem.getAttributeNodeNS( namespaceURI, localName );
    }
    
    
    public synchronized Node setNamedItem( Node arg )
        throws DOMException
    {
        if ( ! ( arg instanceof AttrImpl ) )
            throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
                                        "Node is not an attribute compatible with this element." );
        return _elem.setAttributeNode( (AttrImpl) arg );
    }


    public synchronized Node setNamedItemNS( Node arg )
        throws DOMException
    {
        if ( ! ( arg instanceof AttrImpl ) )
            throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
                                        "Node is not an attribute compatible with this element." );
        return _elem.setAttributeNode( (AttrImpl) arg );
    }
    
    
    public synchronized Node removeNamedItem( String name )
        throws DOMException
    {
        return removeNamedItemNS( null, name );
    }


    public synchronized Node removeNamedItemNS( String namespaceURI,
                                                String localName )
        throws DOMException
    {
        AttrImpl    attr;
        
        attr = (AttrImpl) _elem.getAttributeNodeNS( namespaceURI, localName );
        if ( attr != null ) {
            _elem.removeAttributeNode( attr );
            return attr;
        } else
            return null;
    }
    
    
    /**
     * Construct a new attribute map from the given element.
     * 
     * @param elem The element
     */
    AttrNodeMap( ElementImpl elem )
    {
        if ( elem == null )
            throw new NullPointerException( "Argument 'elem' is null." );
        _elem = elem;
    }

    
    /**
     * The element for which attributes are listed.
     */
    private ElementImpl     _elem;
    
    
}
