package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLTableCellElementImpl.java
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
 * @see org.w3c.dom.html.HTMLTableCellElement
 * @see ElementImpl
 */
public final class HTMLTableCellElementImpl
	extends HTMLElementImpl
	implements HTMLTableCellElement
{
	

	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLTableCellElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "TD" );
	}
	public String getAbbr()
	{
		return getAttribute( "abbr" );
	}
	public String getAlign()
	{
		return capitalize( getAttribute( "align" ) );
	}
	public String getAxis()
	{
		return getAttribute( "axis" );
	}
	public String getBgColor()
	{
		return getAttribute( "bgcolor" );
	}
	public int getCellIndex()
	{
		Node    parent;
		Node    child;
		int        index;
		
		parent = getParentNode();
		index = 0;
		if ( parent instanceof HTMLTableRowElement )
		{
			child = parent.getFirstChild();
			while ( child != null )
			{
				if ( child instanceof HTMLTableCellElement )
				{
					if ( child == this )
						return index;
					++ index;
				}
				child = child.getNextSibling();
			}
		}
		return -1;
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
	public int getColSpan()
	{
		return toInteger( getAttribute( "colspan" ) );
	}
	public String getHeaders()
	{
		return getAttribute( "headers" );
	}
	public String getHeight()
	{
		return getAttribute( "height" );
	}
	  public boolean getNoWrap()
	{
		return getAttribute( "nowrap" ) != null;
	}
	public int getRowSpan()
	{
		return toInteger( getAttribute( "rowspan" ) );
	}
	public String getScope()
	{
		return getAttribute( "scope" );
	}
	public String getVAlign()
	{
		return capitalize( getAttribute( "valign" ) );
	}
	  public String getWidth()
	{
		return getAttribute( "width" );
	}
	public void setAbbr( String abbr )
	{
		setAttribute( "abbr", abbr );
	}
	public void setAlign( String align )
	{
		setAttribute( "align", align );
	}
	public void setAxis( String axis )
	{
		setAttribute( "axis", axis );
	}
	public void setBgColor( String bgColor )
	{
		setAttribute( "bgcolor", bgColor );
	}
	public void setCellIndex( int cellIndex )
	{
		Node    parent;
		Node    child;
		int        index;
		
		parent = getParentNode();
		if ( parent instanceof HTMLTableRowElement )
		{
			child = parent.getFirstChild();
			while ( child != null )
			{
				if ( child instanceof HTMLTableCellElement )
				{
					if ( cellIndex == 0 )
					{
						if ( this != child )
							parent.insertBefore( this, child );
						return;
					}
					-- cellIndex;
				}
				child = child.getNextSibling();
			}
		}
		parent.appendChild( this );
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
	public void setColSpan( int colspan )
	{
		setAttribute( "colspan", String.valueOf( colspan ) );
	}
	public void setHeaders( String headers )
	{
		setAttribute( "headers", headers );
	}
	public void setHeight( String height )
	{
		setAttribute( "height", height );
	}
	public void setNoWrap( boolean noWrap )
	{
		setAttribute( "nowrap", noWrap ? "" : null );
	}
	public void setRowSpan( int rowspan )
	{
		setAttribute( "rowspan", String.valueOf( rowspan ) );
	}
	public void setScope( String scope )
	{
		setAttribute( "scope", scope );
	}
	public void setVAlign( String vAlign )
	{
		setAttribute( "valign", vAlign );
	}
	public void setWidth( String width )
	{
		setAttribute( "width", width );
	}
}