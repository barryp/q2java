package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLIFrameElementImpl.java
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
 * @see org.w3c.dom.html.HTMLIFrameElement
 * @see ElementImpl
 */
public final class HTMLIFrameElementImpl
	extends HTMLElementImpl
	implements HTMLIFrameElement
{

	
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLIFrameElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "IFRAME" );
	}
	public String getAlign()
	{
		return capitalize( getAttribute( "align" ) );
	}
	public String getFrameBorder()
	{
		return getAttribute( "frameborder" );
	}
	public String getHeight()
	{
		return getAttribute( "height" );
	}
	public String getLongDesc()
	{
		return getAttribute( "longdesc" );
	}
	public String getMarginHeight()
	{
		return getAttribute( "marginheight" );
	}
	public String getMarginWidth()
	{
		return getAttribute( "marginwidth" );
	}
	  public String getName()
	{
		return getAttribute( "name" );
	}
	public String getScrolling()
	{
		return capitalize( getAttribute( "scrolling" ) );
	}
	   public String getSrc()
	{
		return getAttribute( "src" );
	}
	  public String getWidth()
	{
		return getAttribute( "width" );
	}
	public void setAlign( String align )
	{
		setAttribute( "align", align );
	}
	public void setFrameBorder( String frameBorder )
	{
		setAttribute( "frameborder", frameBorder );
	}
	public void setHeight( String height )
	{
		setAttribute( "height", height );
	}
	public void setLongDesc( String longDesc )
	{
		setAttribute( "longdesc", longDesc );
	}
	public void setMarginHeight( String marginHeight )
	{
		setAttribute( "marginheight", marginHeight );
	}
	public void setMarginWidth( String marginWidth )
	{
		setAttribute( "marginwidth", marginWidth );
	}
	public void setName( String name )
	{
		setAttribute( "name", name );
	}
	public void setScrolling( String scrolling )
	{
		setAttribute( "scrolling", scrolling );
	}
	public void setSrc( String src )
	{
		setAttribute( "src", src );
	}
	public void setWidth( String width )
	{
		setAttribute( "width", width );
	}
}