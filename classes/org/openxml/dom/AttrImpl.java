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
 * + Added namespace support by extending ParentNSImpl and adding argument
 *   to the constructor.
 * + Added getOwnerDocument() for DOM 2 support.
 * + Added getNextAttr() to prevent need to access _nextNode from external
 *   objects.
 * Sep 25
 * - Removed specified support, this attribute is always specified, an
 *   unspecified attribute would be DefaultAttrImpl.
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
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.Attr
 * @see ParentNSNodeImpl
 * @see ElementImpl
 */
public final class AttrImpl
    extends ParentNSNodeImpl
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
    

    public boolean getSpecified()
    {
        return true;
    }
    
    
    public String getValue()
    {
        return getNodeValue();
    }
    

    public void setNodeValue( String value )
    {
        super.setNodeValue( value );
    }
    
    
    public void setValue( String value )
    {
        super.setNodeValue( value );
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


    /**
     * Returns the element node this attribute is attached to, or null if
     * attribute not in use in any element.
     *
     * @return Element node of this attribute, or null
     */
    public Element getOwnerElement()
    {
        return (Element) _parent;
    }
    

    public String toString()
    {
        String    value;
        
        value = getValue();
        if ( value.length() > 32 )
            value = value.substring( 0, 32 ) + "..";
        return "Attribute node: [" + getName() + "] [" + value + "] SPECIFIED";
    }
    

    public final Object clone()
    {
        AttrImpl    clone;
        
        clone = new AttrImpl( _ownerDocument, getNamespaceURI(), getNodeName(), getNodeValue() );
        cloneInto( clone, true );
        return clone;
    }

    
    public final Node cloneNode( boolean deep )
    {
        AttrImpl    clone;
            
        clone = new AttrImpl( _ownerDocument, getNamespaceURI(), getNodeName(), getNodeValue() );
        cloneInto( clone, deep );
        return clone;
    }


    /**
     * Return the next attribute following this one. {@link #getNextSibling}
     * returns null by definition of DOM, but outside classes ({@link
     * ElementImpl} and {@link AttrNodeMap}) require traversal over attribute
     * list.
     *
     * @return Next attribute in list
     */
    protected AttrImpl getNextAttr()
    {
        return (AttrImpl) _nextNode;
    }
    

    protected synchronized void cloneInto( NodeImpl into, boolean deep )
    {
        Attr        attr;
        ElementImpl intoX;
        
        super.cloneInto( into, deep );
    }
    

    /**
     * Assures that the children of an attribute are either {@link
     * Text} or {@link EntityReference}.
     */
    protected NodeImpl castNewChild( Node newChild )
        throws DOMException
    {
        NodeImpl    result;
        
        if ( newChild == null )
            throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
                                        "Child reference is null." );
        if ( ! ( newChild instanceof TextImpl ||
                 newChild instanceof EntityReferenceImpl ) )
            throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
                                        "Child is not a compatible type for this node." );
        return (NodeImpl) newChild;
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
    AttrImpl( DocumentImpl owner, String namespaceURI, String qualifiedName,
              String defValue )
        throws DOMException
    {
        super( owner, namespaceURI, qualifiedName, defValue );
    }


}









