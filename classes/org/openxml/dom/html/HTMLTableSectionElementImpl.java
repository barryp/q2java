package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLTableSectionElementImpl.java
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
 * @see org.w3c.dom.html.HTMLTableSectionElement
 * @see ElementImpl
 */
public final class HTMLTableSectionElementImpl
	extends HTMLElementImpl
	implements HTMLTableSectionElement
{    
	
	
	private HTMLCollectionImpl    _rows;


	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLTableSectionElementImpl( HTMLDocumentImpl owner, String section )
	{
		super( owner, section );
	}
	public void deleteRow( int index )
	{
		deleteRowX( index );
	}
	int deleteRowX( int index )
	{
		Node    child;
		
		child = getFirstChild();
		while ( child != null )
		{
			if ( child instanceof HTMLTableRowElement )
			{
				if ( index == 0 )
				{
					removeChild ( child );
					return -1;
				}
				--index;
			}
			child = child.getNextSibling();
		}
		return index;
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
	public HTMLCollection getRows()
	{
		if ( _rows == null )
			_rows = new HTMLCollectionImpl( this, HTMLCollectionImpl.ROW );
		return _rows;
	}
	public String getVAlign()
	{
		return capitalize( getAttribute( "valign" ) );
	}
	public HTMLElement insertRow( int index )
	{
		HTMLTableRowElementImpl    newRow;
		
		newRow = new HTMLTableRowElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "TR" );
		newRow.insertCell( 0 );
		if ( insertRowX( index, newRow ) >= 0 )
			appendChild( newRow );
		return newRow;
	}
	int insertRowX( int index, HTMLTableRowElementImpl newRow )
	{
		Node    child;
		
		child = getFirstChild();
		while ( child != null )
		{
			if ( child instanceof HTMLTableRowElement )
			{
				if ( index == 0 )
				{
					insertBefore( newRow, child );
					return -1;
				}
				--index;
			}
			child = child.getNextSibling();
		}
		return index;
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
	public void setVAlign( String vAlign )
	{
		setAttribute( "valign", vAlign );
	}
}