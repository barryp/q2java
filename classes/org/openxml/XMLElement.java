package org.openxml;

/**
 * org/openxml/XMLElement.java
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
import org.openxml.dom.*;


/**
 * Base class for user XML elements. {@link XMLDocument} is designed to create
 * elements of classes derived from {@link XMLElement}. In addition to several
 * API extensions, user XML elements can be used to map XML documents directly
 * into application data structures.
 * <P>
 * {@link XMLElement} extends the DOM {@link Element} with the following methods:
 * <UL>
 * <LI>{@link #makeReadOnly} renders an element read-only preventing any changes
 *  to it's attribute or content
 * <LI>{@link #setUserObject} and {@link #getUserObject} may be used to associate
 *  a user object with an element
 * </UL>
 * <P>
 * In order to support user elements, a document class must extend {@link
 * XMLDocument}. It then registers tag name to element class associations using
 * {@link XMLDocument#registerElement}, or uses an external element factory
 * with {@link XMLDocument#useElementFactory}.
 * <P>
 * The user elements will be returns whenever {@link XMLDocument#createElement}
 * is called on the document, or when a document of this type is parsed.
 * <P>
 * A user element derived from {@link XMLElement} must pass the owner document
 * and tag name to its constructor. The class and its constructor must be
 * declared public and the constructor must have the same signature as the
 * {@link XMLElement} constructor. The last three requirements may be relaxed
 * if an external element factory is used.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Element
 * @see XMLDocument#registerElement
 * @see XMLElementFactory
 * @deprecated Alternative API will be introduced in OpenXML 1.1
 */
public class XMLElement
	extends ElementImpl
	implements Element
{


	/**
	 * Holds a user object. Any object can be associated with this element
	 * using the appropriate set/get methods.
	 */
	protected Object    _userObject;


	/**
	 * Constructor requires owner document and element tag name. This will be
	 * provided when an element of this or a derived class is being constructed
	 * by an {@link XMLDocument}. The arguments must pass to this constructor
	 * unaffected. Derived classes must implement at least one constructor with
	 * the exact same signature to support element class registration; this
	 * requirement is relaxed if an external element factory is used.
	 *
	 * @param owner The owner document
	 * @param tagName The element's tag name
	 */
	public XMLElement( Document owner, String tagName )
	{        super( (DocumentImpl) owner, tagName );
	}
	/**
	 * Returns the identifier of this element. Unless specifies otherwise in
	 * the DTD, this would be the value of the <TT>id</TT> attribute. It may
	 * be a textual value or null.
	 *
	 * @return The identifier of this element
	 */
	public String getID()
	{
		return getAttribute( "id" );
	}
	/**
	 * Returns the user object associated with this element. There is no limit
	 * on what the user object may hold.
	 *
	 * @return The user object
	 */
	public Object getUserObject()
	{
		return _userObject;
	}
	/**
	 * Associates this element with a user object. There is no limit on what
	 * the user object may hold.
	 *
	 * @param userObject The user object
	 */
	public void setUserObject( Object userObject )
	{
		_userObject = userObject;
	}
}