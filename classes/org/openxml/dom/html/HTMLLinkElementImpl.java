package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLLinkElementImpl.java
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
 * @see org.w3c.dom.html.HTMLLinkElement
 * @see ElementImpl
 */
public final class HTMLLinkElementImpl
	extends HTMLElementImpl
	implements HTMLLinkElement
{

	
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLLinkElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "LINK" );
	}
	public String getCharset()
	{
		return getAttribute( "charset" );
	}
	public boolean getDisabled()
	{
		return getAttribute( "disabled" ) != null;
	}
	public String getHref()
	{
		return getAttribute( "href" );
	}
	public String getHreflang()
	{
		return getAttribute( "hreflang" );
	}
	public String getMedia()
	{
		return getAttribute( "media" );
	}
	public String getRel()
	{
		return getAttribute( "rel" );
	}
	public String getRev()
	{
		return getAttribute( "rev" );
	}
	public String getTarget()
	{
		return getAttribute( "target" );
	}
	public String getType()
	{
		return getAttribute( "type" );
	}
	public void setCharset( String charset )
	{
		setAttribute( "charset", charset );
	}
	public void setDisabled( boolean disabled )
	{
		setAttribute( "disabled", disabled ? "" : null );
	}
	public void setHref( String href )
	{
		setAttribute( "href", href );
	}
	public void setHreflang( String hreflang )
	{
		setAttribute( "hreflang", hreflang );
	}
	public void setMedia( String media )
	{
		setAttribute( "media", media );
	}
	public void setRel( String rel )
	{
		setAttribute( "rel", rel );
	}
	public void setRev( String rev )
	{
		setAttribute( "rev", rev );
	}
	public void setTarget( String target )
	{
		setAttribute( "target", target );
	}
	public void setType( String type )
	{
		setAttribute( "type", type );
	}
}