package org.openxml.dom;

/**
 * org/openxml/dom/AttrImpl.java
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
import org.w3c.dom.html.HTMLDocument;


/**
 * Represents an attribute in an {@link org.w3c.dom.Element} node.
 * <P>
 * Attributes are not real nodes, they are not children in their parent element
 * and the methods {@link AttrImpl#getParentNode}, {@link AttrImpl#getNextSibling}
 * and {@link #getPreviousSibling} always return null. 
 * <P>
 * Attributes in XML documents support children, but only of the type {@link
 * org.w3c.dom.Text} and {@link org.w3c.dom.EntityReference}.
 * <P>
 * The specified value of an attribute indicates whether it's value has been
 * changed since it was constructed with the default value. The specified value
 * is not used when cloning an attribute or testing for equality.
 * <P>
 * To speed up implementation, all attributes are implemented as double-linked 
 * list using {@link NodeImpl#_parent}, {@link NodeImpl#_nextNode} and
 * {@link NodeImpl#_prevNode}.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Attr
 * @see NodeImpl
 * @see ElementImpl
 */
final class AttrImpl
	extends NodeImpl
	implements Attr
{


	/**
	 * True if a value has been specified for this node in the document.
	 * This value is not used when cloning or testing for equality.
	 */
	private boolean    _specified;
	
	
	/**
	 * Hidden constructor for attribute. Note that <TT>value</TT> is the DTD's
	 * default value for this attribute, it is not the value specified in the XML
	 * document. The value specified in the XML document should be passed by
	 * calling {@link #setValue} explicitly.
	 * <P>
	 * The attribute name is case sensitive for XML, but is case insensitive and
	 * all lower case for HTML documents. <TT>owner</TT> must point to a valid
	 * document object.
	 * 
	 * @param owner The owner document
	 * @param name The attribute's name
	 * @param value The attribute's value as specified in the DTD or null
	 */
	AttrImpl( DocumentImpl owner, String name, String defValue )
	{
		// Make sure that all attribute names are converted to lower case
		// for HTML documents.
		super( owner, ( owner instanceof HTMLDocument ) ? name.toLowerCase() : name, defValue, true );
		// Specified is always false. If a value was specified, it was the
		// default.
		_specified = false;
	}
	/**
	 * Assures that the children of an attribute are either {@link
	 * org.w3c.dom.Text} or {@link org.w3c.dom.EntityReference}.
	 * 
	 * @see NodeImpl#castNewChild
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
	public final Object clone()
	{
		AttrImpl    clone;
		
		clone = new AttrImpl( _ownerDocument, getNodeName(), getNodeValue() );
		cloneInto( clone, true );
		return clone;
	}
	protected synchronized void cloneInto( NodeImpl into, boolean deep )
	{
		Attr        attr;
		ElementImpl    intoX;
		
		super.cloneInto( into, deep );
		( (AttrImpl) into )._specified = this._specified;
	}
	public final Node cloneNode( boolean deep )
	{
		AttrImpl    clone;
			
		clone = new AttrImpl( _ownerDocument, getNodeName(), getNodeValue() );
		cloneInto( clone, deep );
		return clone;
	}
	public String getName()
	{
		return getNodeName();
	}
	public Node getNextSibling()
	{
		// Must return null per DOM spec.
		return null;
	}
	public short getNodeType()
	{
		return ATTRIBUTE_NODE;
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
	public boolean getSpecified()
	{
		return _specified;
	}
	public String getValue()
	{
		return getNodeValue();
	}
	public void setNodeValue( String value )
	{
		super.setNodeValue( value );
		_specified = true;
	}
	public void setValue( String value )
	{
		super.setNodeValue( value );
		_specified = true;
	}
	protected boolean supportsChildern()
	{
		return true;
	}
	public String toString()
	{
		String    value;
		
		value = getValue();
		if ( value.length() > 32 )
			value = value.substring( 0, 32 ) + "..";
		return "Attribute node: [" + getName() + "] [" + value +
			   ( _specified ? "] SPECIFIED" : "]" );
	}
}