package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLLabelElementImpl.java
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
 * @see org.w3c.dom.html.HTMLLabelElement
 * @see ElementImpl
 */
public final class HTMLLabelElementImpl
	extends HTMLElementImpl
	implements HTMLLabelElement, HTMLFormControl
{

	
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLLabelElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "LABEL" );
	}
	public String getAccessKey()
	{
		String    accessKey;
		
		// Make sure that the access key is a single character.
		accessKey = getAttribute( "accesskey" );
		if ( accessKey != null && accessKey.length() > 1 )
			accessKey = accessKey.substring( 0, 1 );
		return accessKey;
	}
	   public String getHtmlFor()
	{
		return getAttribute( "for" );
	}
	public void setAccessKey( String accessKey )
	{
		// Make sure that the access key is a single character.
		if ( accessKey != null && accessKey.length() > 1 )
			accessKey = accessKey.substring( 0, 1 );
		setAttribute( "accesskey", accessKey );
	}
	public void setHtmlFor( String htmlFor )
	{
		setAttribute( "for", htmlFor );
	}
}