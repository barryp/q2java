package org.openxml.dom;

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


import org.w3c.dom.*;


/**
 * Used to traverse attributes of an element. {@link
 * org.w3c.dom.NamedNodeMap} is a live list, meaning that any change
 * to the element is reflected in this list and vice versa. This is
 * implemented by iterating the element list in response to each method
 * call.
 * <P>
 * This map must be implemented outside of {@link ElementImpl} since
 * some of the methods defined in this interface are also used in HTML
 * elements (specifically {@link #getLength}.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see NamedNodeMapl
 */
public final class AttrNodeMap
	implements NamedNodeMap
{
	
	
	/**
	 * The element for which attributes are listed.
	 */
	private ElementImpl     _elem;
	
	
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
	 * Return the number of attributes in this element.
	 * 
	 * @return Number of attributes
	 */
	public int getLength()
	{
		return _elem._attrCount;
	}
	public synchronized Node getNamedItem( String name )
	{
		return _elem.getNamedAttr( name );
	}
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
		while ( attr != null && index > 0 )
		{
			// Note: getNextSibling() will return null.
			attr = (AttrImpl) attr._nextNode;
			-- index;
		}
		return attr;
	}
	public synchronized Node removeNamedItem( String name )
		throws DOMException
	{
		AttrImpl    attr;
		
		attr = _elem.getNamedAttr( name );
		if ( attr != null )
		{
			_elem.removeAttr( attr );
			return attr;
		}
		else
			return null;
	}
	public synchronized Node setNamedItem( Node arg )
		throws DOMException
	{
		if ( ! ( arg instanceof AttrImpl ) )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"Node is not an attribute compatible with this element." );
		return _elem.setAttributeNode( (AttrImpl) arg );
	}
}