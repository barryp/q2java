package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLFormElementImpl.java
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


import org.openxml.dom.*;
import org.w3c.dom.*;
import org.w3c.dom.html.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLFormElement
 * @see ElementImpl
 */
public final class HTMLFormElementImpl
	extends HTMLElementImpl
	implements HTMLFormElement
{

	
	/**
	 * Collection of all elements contained in this FORM.
	 */
	private HTMLCollectionImpl    _elements;
	
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLFormElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "FORM" );
	}
	public String getAcceptCharset()
	{
		return getAttribute( "accept-charset" );
	}
	  public String getAction()
	{
		return getAttribute( "action" );
	}
	public HTMLCollection getElements()
	{
		if ( _elements == null )
			_elements = new HTMLCollectionImpl( this, HTMLCollectionImpl.ELEMENT );
		return _elements;
	}
	  public String getEnctype()
	{
		return getAttribute( "enctype" );
	}
	public int getLength()
	{
		return getElements().getLength();
	}
	  public String getMethod()
	{
		return capitalize( getAttribute( "method" ) );
	}
	public String getName()
	{
		return getAttribute( "name" );
	}
	public String getTarget()
	{
		return getAttribute( "target" );
	}
	public void reset()
	{
		// No scripting in server-side DOM. This method is moot.
	}
	public void setAcceptCharset( String acceptCharset )
	{
		setAttribute( "accept-charset", acceptCharset );
	}
	public void setAction( String action )
	{
		setAttribute( "action", action );
	}
	public void setEnctype( String enctype )
	{
		setAttribute( "enctype", enctype );
	}
	public void setMethod( String method )
	{
		setAttribute( "method", method );
	}
	public void setName( String name )
	{
		setAttribute( "name", name );
	}
	public void setTarget( String target )
	{
		setAttribute( "target", target );
	}
	public void submit()
	{
		// No scripting in server-side DOM. This method is moot.
	}
}