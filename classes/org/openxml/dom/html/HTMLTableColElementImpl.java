package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLTableColElementImpl.java
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
 * @see org.w3c.dom.html.HTMLTableColElement
 * @see ElementImpl
 */
public final class HTMLTableColElementImpl
	extends HTMLElementImpl
	implements HTMLTableColElement
{
	
	
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLTableColElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, name );
	}
	public String getAlign()
	{
		return capitalize( getAttribute( "align" ) );
	}
	public String getCh()
	{
		String    ch;
		
		// Make sure that the access key is a single character.
		ch = getAttribute( "char" );
		if ( ch != null && ch.length() > 1 )
			ch = ch.substring( 0, 1 );
		return ch;
	}
	public String getChOff()
	{
		return getAttribute( "charoff" );
	}
	public int getSpan()
	{
		return toInteger( getAttribute( "span" ) );
	}
	public String getVAlign()
	{
		return capitalize( getAttribute( "valign" ) );
	}
	public String getWidth()
	{
		return getAttribute( "width" );
	}
	public void setAlign( String align )
	{
		setAttribute( "align", align );
	}
	public void setCh( String ch )
	{
		// Make sure that the access key is a single character.
		if ( ch != null && ch.length() > 1 )
			ch = ch.substring( 0, 1 );
		setAttribute( "char", ch );
	}
	public void setChOff( String chOff )
	{
		setAttribute( "charoff", chOff );
	}
	public void setSpan( int span )
	{
		setAttribute( "span", String.valueOf( span ) );
	}
	public void setVAlign( String vAlign )
	{
		setAttribute( "valign", vAlign );
	}
	public void setWidth( String width )
	{
		setAttribute( "width", width );
	}
}