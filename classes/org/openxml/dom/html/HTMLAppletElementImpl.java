package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLAppletElementImpl.java
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
 * @see org.w3c.dom.html.HTMLAppletElement
 * @see HTMLElementImpl
 */
public final class HTMLAppletElementImpl
	extends HTMLElementImpl
	implements HTMLAppletElement
{

	
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLAppletElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "APPLET" );
	}
	public String getAlign()
	{
		return getAttribute( "align" );
	}
	public String getAlt()
	{
		return getAttribute( "alt" );
	}
	public String getArchive()
	{
		return getAttribute( "archive" );
	}
	public String getCode()
	{
		return getAttribute( "code" );
	}
	public String getCodeBase()
	{
		return getAttribute( "codebase" );
	}
	public String getHeight()
	{
		return getAttribute( "height" );
	}
	public String getHspace()
	{
		return getAttribute( "height" );
	}
	public String getName()
	{
		return getAttribute( "name" );
	}
	public String getObject()
	{
		return getAttribute( "object" );
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
	public void setArchive( String archive )
	{
		setAttribute( "archive", archive );
	}
	public void setCode( String code )
	{
		setAttribute( "code", code );
	}
	public void setCodeBase( String codeBase )
	{
		setAttribute( "codebase", codeBase );
	}
	public void setHeight( String height )
	{
		setAttribute( "height", height );
	}
	public void setHspace( String height )
	{
		setAttribute( "height", height );
	}
	public void setName( String name )
	{
		setAttribute( "name", name );
	}
	public void setObject( String object )
	{
		setAttribute( "object", object );
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