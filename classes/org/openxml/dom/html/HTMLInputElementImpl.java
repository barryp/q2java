package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLInputElementImpl.java
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
 * @see org.w3c.dom.html.HTMLInputElement
 * @see ElementImpl
 */
public final class HTMLInputElementImpl
	extends HTMLElementImpl
	implements HTMLInputElement, HTMLFormControl
{
	
	
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLInputElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "INPUT" );
	}
	public void blur()
	{
		// No scripting in server-side DOM. This method is moot.
	}
	public void click()
	{
		// No scripting in server-side DOM. This method is moot.
	}
	public void focus()
	{
		// No scripting in server-side DOM. This method is moot.
	}
	public String getAccept()
	{
		return getAttribute( "accept" );
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
	public String getAlign()
	{
		return capitalize( getAttribute( "align" ) );
	}
	public String getAlt()
	{
		return getAttribute( "alt" );
	}
	public boolean getChecked()
	{
		return getAttribute( "checked" ) != null;
	}
	public boolean getDefaultChecked()
	{
		// ! NOT FULLY IMPLEMENTED !
		return getAttribute( "defaultChecked" ) != null;
	}
	public String getDefaultValue()
	{
		// ! NOT FULLY IMPLEMENTED !
		return getAttribute( "defaultValue" );
	}
	public boolean getDisabled()
	{
		return getAttribute( "disabled" ) != null;
	}
	public int getMaxLength()
	{
		return toInteger( getAttribute( "maxlength" ) );
	}
	public String getName()
	{
		return getAttribute( "name" );
	}
	public boolean getReadOnly()
	{
		return getAttribute( "readonly" ) != null;
	}
	public String getSize()
	{
		return getAttribute( "size" );
	}
	public String getSrc()
	{
		return getAttribute( "src" );
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
	public String getValue()
	{
		return getAttribute( "value" );
	}
	public void select()
	{
		// No scripting in server-side DOM. This method is moot.
	}
	public void setAccept( String accept )
	{
		setAttribute( "accept", accept );
	}
	public void setAccessKey( String accessKey )
	{
		// Make sure that the access key is a single character.    
		if ( accessKey != null && accessKey.length() > 1 )
			accessKey = accessKey.substring( 0, 1 );
		setAttribute( "accesskey", accessKey );
	}
	public void setAlign( String align )
	{
		setAttribute( "align", align );
	}
	public void setAlt( String alt )
	{
		setAttribute( "alt", alt );
	}
	public void setChecked( boolean checked )
	{
		setAttribute( "checked", checked ? "" : null );
	}
	public void setDefaultChecked( boolean defaultChecked )
	{
		// ! NOT FULLY IMPLEMENTED !
		setAttribute( "defaultChecked", defaultChecked ? "" : null );
	}
	public void setDefaultValue( String defaultValue )
	{
		// ! NOT FULLY IMPLEMENTED !
		setAttribute( "defaultValue", defaultValue );
	}
	public void setDisabled( boolean disabled )
	{
		setAttribute( "disabled", disabled ? "" : null );
	}
	public void setMaxLength( int maxLength )
	{
		setAttribute( "maxlength", String.valueOf( maxLength ) );
	}
	public void setName( String name )
	{
		setAttribute( "name", name );
	}
	public void setReadOnly( boolean readOnly )
	{
		setAttribute( "readonly", readOnly ? "" : null );
	}
	public void setSize( String size )
	{
		setAttribute( "size", size );
	}
	public void setSrc( String src )
	{
		setAttribute( "src", src );
	}
	public void setTabIndex( int tabIndex )
	{
		setAttribute( "tabindex", String.valueOf( tabIndex ) );
	}
	public void setUseMap( String useMap )
	{
		setAttribute( "useMap", useMap );
	}
	public void setValue( String value )
	{
		setAttribute( "value", value );
	}
}