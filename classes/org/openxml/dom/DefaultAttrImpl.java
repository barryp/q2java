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
 * + Added DefaultAttrImpl to support default attributes derived from the DTD.
 */

package org.openxml.dom;


import org.w3c.dom.*;



/**
 * Represents an attribute in an {@link org.w3c.dom.Element} node.
 * <P>
 * Attributes are not real nodes, they are not children in their parent
 * element. Siblings and parents are not accessible.
 * <P>
 * Attributes in XML documents support children, but only of the type {@link
 * Text} and {@link EntityReference}.
 * <P>
 * The specified value of an attribute indicates whether it's value has been
 * changed since it was constructed with the default value. The specified
 * value is not used when cloning an attribute or testing for equality.
 * <P>
 * To speed up implementation, all attributes are implemented as double-linked 
 * list using {@link ParentNodeImpl}. Namespace support is provided by
 * {@link ParentNSNodeImpl}.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.Attr
 * @see AttrImpl
 */
final class DefaultAttrImpl
    extends NodeImpl
    implements Attr
{


    public short getNodeType()
    {
        return ATTRIBUTE_NODE;
    }


    public String getName()
    {
	return getNodeName();
    }


    public String getValue()
    {
	return getNodeValue();
    }

    
    public String getNamespaceURI()
    {
        return _dtdAttr.getNamespaceURI();
    }


    public String getPrefix()
    {
	return _dtdAttr.getPrefix();
    }


    public void setPrefix( String newPrefix )
    {
        throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
    }


    public String getLocalName()
    {
	return _dtdAttr.getNodeName();
    }


    public boolean getSpecified()
    {
        return false;
    }
    
    
    public void setNodeValue( String value )
    {
        throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
    }
    
    
    public void setValue( String value )
    {
        throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
    }

        
    public NodeList getChildNodes()
    {
	return _dtdAttr.getChildNodes();
    }


    public Node getFirstChild()
    {
	return _dtdAttr.getFirstChild();
    }
    

    public Node getLastChild()
    {
        return _dtdAttr.getLastChild();
    }
    

    public Node getParentNode()
    {
        // Must return null per DOM spec.
        return null;
    }


    public Node getPreviousSibling()
    {
        // Must return null per DOM spec.
        return null;
    }


    public Node getNextSibling()
    {
        // Must return null per DOM spec.
        return null;
    }


    public Element getOwnerElement()
    {
	return (Element) _parent;
    }
    

    public  boolean hasChildNodes()
    {
        return _dtdAttr.hasChildNodes();
    }


    public Node appendChild( Node newChild )
    {
        throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
    }    


    public Node insertBefore( Node newChild, Node refChild )
        throws DOMException
    {
        throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
    }
   

    public Node removeChild( Node oldChild )
        throws DOMException
    {
        throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
    }
    

    public Node replaceChild( Node newChild, Node oldChild )
        throws DOMException
    {
        throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
    }


    public String toString()
    {
        String    value;
        
        value = getValue();
        if ( value.length() > 32 )
            value = value.substring( 0, 32 ) + "..";
        return "Attribute node: [" + getName() + "] [" + value +
               "] DEFAULT";
    }
    

    public final Object clone()
    {
        DefaultAttrImpl    clone;
        
        clone = new DefaultAttrImpl( _ownerDocument, _dtdAttr );
        cloneInto( clone, true );
        return clone;
    }

    
    public final Node cloneNode( boolean deep )
    {
        DefaultAttrImpl    clone;
            
        clone = new DefaultAttrImpl( _ownerDocument, _dtdAttr );
        cloneInto( clone, deep );
        return clone;
    }


    /**
     * Return the next attribute following this one. {@link #getNextSibling}
     * returns null by definition of DOM, but outside classes ({@link
     * ElementImpl} and {@link AttrListImpl}) require traversal over attribute
     * list.
     *
     * @return Next attribute in list
     */
    protected AttrImpl getNextAttr()
    {
        return (AttrImpl) _nextNode;
    }
    

    public synchronized boolean equals( Object other )
    {
        if ( this == other )
            return true;
	return _dtdAttr.equals( other );
    }


    /**
     * Hidden constructor for attribute. If <TT>namespaceURI</TT> is specified,
     * this attribute has namespace support and may return a prefix.
     *
     * <TT>defValue</TT> is the default value specified by the DTD, it is
     * not a specified value. The attribute value from the document should
     * be passed using {@link #setValue}.
     *
     * @param owner The owner document
     * @param namespaceURI The namespace URI, or null
     * @param qualifiedName The qualified name
     * @param value The attribute's value as specified in the DTD or null
     * @throws DOMException Attribute name or value are invalid
     */
    DefaultAttrImpl( DocumentImpl owner, AttrImpl dtdAttr )
        throws DOMException
    {
        super( owner, dtdAttr.getNodeName(), dtdAttr.getNodeValue() );
	makeReadOnly();
	_dtdAttr = dtdAttr;
    }


    private AttrImpl    _dtdAttr;
    
    
}
