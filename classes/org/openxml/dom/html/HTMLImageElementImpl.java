package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLImageElementImpl.java
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
 * @see org.w3c.dom.html.HTMLImageElement
 * @see ElementImpl
 */
public final class HTMLImageElementImpl
	extends HTMLElementImpl
	implements HTMLImageElement
{

	
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLImageElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "IMG" );
	}
	public String getAlign()
	{
		return capitalize( getAttribute( "align" ) );
	}
	public String getAlt()
	{
		return getAttribute( "alt" );
	}
	public String getBorder()
	{
		return getAttribute( "border" );
	}
	  public String getHeight()
	{
		return getAttribute( "height" );
	}
	public String getHspace()
	{
		return getAttribute( "hspace" );
	}
	public boolean getIsMap()
	{
		return getAttribute( "ismap" ) != null;
	}
	public String getLongDesc()
	{
		return getAttribute( "longdesc" );
	}
	   public String getLowSrc()
	{
		return getAttribute( "lowsrc" );
	}
	  public String getName()
	{
		return getAttribute( "name" );
	}
	   public String getSrc()
	{
		return getAttribute( "src" );
	}
	public String getUseMap()
	{
		return getAttribute( "useMap" );
	}
	public String getVspace()
	{
		return getAttribute( "vspace" );
	}
	  public String getWidth()
	{
		return getAttribute( "width" );
	}
	public void setAlign( String align )
	{
		setAttribute( "align", align );
	}
	public void setAlt( String alt )
	{
		setAttribute( "alt", alt );
	}
	public void setBorder( String border )
	{
		setAttribute( "border", border );
	}
	public void setHeight( String height )
	{
		setAttribute( "height", height );
	}
	public void setHspace( String hspace )
	{
		setAttribute( "hspace", hspace );
	}
	public void setIsMap( boolean isMap )
	{
		setAttribute( "ismap", isMap ? "" : null );
	}
	public void setLongDesc( String longDesc )
	{
		setAttribute( "longdesc", longDesc );
	}
	public void setLowSrc( String lowSrc )
	{
		setAttribute( "lowsrc", lowSrc );
	}
	public void setName( String name )
	{
		setAttribute( "name", name );
	}
	public void setSrc( String src )
	{
		setAttribute( "src", src );
	}
	public void setUseMap( String useMap )
	{
		setAttribute( "useMap", useMap );
	}
	public void setVspace( String vspace )
	{
		setAttribute( "vspace", vspace );
	}
	public void setWidth( String width )
	{
		setAttribute( "width", width );
	}
}