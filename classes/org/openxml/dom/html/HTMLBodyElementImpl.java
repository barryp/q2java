package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLBodyElementImpl.java
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
 * @see org.w3c.dom.html.HTMLBodyElement
 * @see ElementImpl
 */
public final class HTMLBodyElementImpl
	extends HTMLElementImpl
	implements HTMLBodyElement
{
	
	  /**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLBodyElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "BODY" );
	}
	public String getALink()
	{
		return getAttribute( "alink" );
	}
	public String getBackground()
	{
		return getAttribute( "background" );
	}
	public String getBgColor()
	{
		return getAttribute( "bgcolor" );
	}
	public String getLink()
	{
		return getAttribute( "link" );
	}
	public String getText()
	{
		return getAttribute( "text" );
	}
	public String getVLink()
	{
		return getAttribute( "vlink" );
	}
	public void setALink(String aLink)
	{
		setAttribute( "alink", aLink );
	}
	public void setBackground( String background )
	{
		setAttribute( "background", background );
	}
	public void setBgColor(String bgColor)
	{
		setAttribute( "bgcolor", bgColor );
	}
	public void setLink(String link)
	{
		setAttribute( "link", link );
	}
	public void setText(String text)
	{
		setAttribute( "text", text );
	}
	public void  setVLink(String vLink)
	{
		setAttribute( "vlink", vLink );
	}
}