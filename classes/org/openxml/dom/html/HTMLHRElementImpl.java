package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLHRElementImpl.java
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
 * @see org.w3c.dom.html.HTMLHRElement
 * @see ElementImpl
 */
public final class HTMLHRElementImpl
	extends HTMLElementImpl
	implements HTMLHRElement
{

	
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLHRElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "HR" );
	}
	public String getAlign()
	{
		return capitalize( getAttribute( "align" ) );
	}
	public boolean getNoShade()
	{
		return getAttribute( "noshade" ) != null;
	}
	public String getSize()
	{
		return getAttribute( "size" );
	}
	  public String getWidth()
	{
		return getAttribute( "width" );
	}
	public void setAlign( String align )
	{
		setAttribute( "align", align );
	}
	public void setNoShade( boolean noShade )
	{
		setAttribute( "noshade", noShade ? "" : null );
	}
	public void setSize( String size )
	{
		setAttribute( "size", size );
	}
	public void setWidth( String width )
	{
		setAttribute( "width", width );
	}
}