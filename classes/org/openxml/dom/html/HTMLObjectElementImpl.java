package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLObjectElementImpl.java
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
 * @see org.w3c.dom.html.HTMLObjectElement
 * @see ElementImpl
 */
public final class HTMLObjectElementImpl
	extends HTMLElementImpl
	implements HTMLObjectElement, HTMLFormControl
{
	
	

	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLObjectElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "OBJECT" );
	}
	public String getAlign()
	{
		return capitalize( getAttribute( "align" ) );
	}
	public String getArchive()
	{
		return getAttribute( "archive" );
	}
	public String getBorder()
	{
		return getAttribute( "border" );
	}
	public String getCode()
	{
		return getAttribute( "code" );
	}
	public String getCodeBase()
	{
		return getAttribute( "codebase" );
	}
	public String getCodeType()
	{
		return getAttribute( "codetype" );
	}
	public String getData()
	{
		return getAttribute( "data" );
	}
	  public boolean getDeclare()
	{
		return getAttribute( "declare" ) != null;
	}
	public String getHeight()
	{
		return getAttribute( "height" );
	}
	public String getHspace()
	{
		return getAttribute( "hspace" );
	}
	public String getName()
	{
		return getAttribute( "name" );
	}
	public String getStandby()
	{
		return getAttribute( "standby" );
	}
	  public int getTabIndex()
	{
		try
		{
			return Integer.parseInt( getAttribute( "tabindex" ) );
		}
		catch ( NumberFormatException except )
		{
			return 0;
		}
	}
	public String getType()
	{
		return getAttribute( "type" );
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
	public void setArchive( String archive )
	{
		setAttribute( "archive", archive );
	}
	public void setBorder( String border )
	{
		setAttribute( "border", border );
	}
	public void setCode( String code )
	{
		setAttribute( "code", code );
	}
	public void setCodeBase( String codeBase )
	{
		setAttribute( "codebase", codeBase );
	}
	public void setCodeType( String codeType )
	{
		setAttribute( "codetype", codeType );
	}
	public void setData( String data )
	{
		setAttribute( "data", data );
	}
	public void setDeclare( boolean declare )
	{
		setAttribute( "declare", declare ? "" : null );
	}
	public void setHeight( String height )
	{
		setAttribute( "height", height );
	}
	public void setHspace( String hspace )
	{
		setAttribute( "hspace", hspace );
	}
	public void setName( String name )
	{
		setAttribute( "name", name );
	}
	public void setStandby( String standby )
	{
		setAttribute( "standby", standby );
	}
	public void setTabIndex( int tabIndex )
	{
		setAttribute( "tabindex", String.valueOf( tabIndex ) );
	}
	public void setType( String type )
	{
		setAttribute( "type", type );
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