package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLTableRowElementImpl.java
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
 * @see org.w3c.dom.html.HTMLTableRowElement
 * @see ElementImpl
 */
public final class HTMLTableRowElementImpl
	extends HTMLElementImpl
	implements HTMLTableRowElement
{

	
	HTMLCollection    _cells;

  
	  /**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLTableRowElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "TR" );
	}
	public void deleteCell( int index )
	{
		Node    child;
		
		child = getFirstChild();
		while ( child != null )
		{
			if ( child instanceof HTMLTableCellElement )
			{
				if ( index == 0 )
				{
					removeChild ( child );
					return;
				}
				--index;
			}
			child = child.getNextSibling();
		}
	}
	public String getAlign()
	{
		return capitalize( getAttribute( "align" ) );
	}
	public String getBgColor()
	{
		return getAttribute( "bgcolor" );
	}
	public HTMLCollection  getCells()
	{
		if ( _cells == null )
			_cells = new HTMLCollectionImpl( this, HTMLCollectionImpl.CELL );
		return _cells;
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
	public int getRowIndex()
	{
		Node    parent;
		
		parent = getParentNode();
		if ( parent instanceof HTMLTableSectionElement )
			parent = parent.getParentNode();
		if ( parent instanceof HTMLTableElement )
			return getRowIndex( parent );;
		return -1;
	}
	int getRowIndex( Node parent )
	{
		NodeList    rows;
		int            i;
		
		// Use getElementsByTagName() which creates a snapshot of all the
		// TR elements under the TABLE/section. Access to the returned NodeList
		// is very fast and the snapshot solves many synchronization problems.
		rows = ( (HTMLElement) parent ).getElementsByTagName( "TR" );
		for ( i = 0 ; i < rows.getLength() ; ++i )
			if ( rows.item( i ) == this )
				return i;
		return -1;
	}
	public int getSectionRowIndex()
	{
		Node    parent;
		
		parent = getParentNode();
		if ( parent instanceof HTMLTableSectionElement )
			return getRowIndex( parent );
		else
			return -1;
	}
	public String getVAlign()
	{
		return capitalize( getAttribute( "valign" ) );
	}
	public HTMLElement insertCell( int index )
	{
		Node        child;
		HTMLElement    newCell;
		
		newCell = new HTMLTableCellElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "TD" );
		child = getFirstChild();
		while ( child != null )
		{
			if ( child instanceof HTMLTableCellElement )
			{
				if ( index == 0 )
				{
					insertBefore( newCell, child );
					return newCell;
				}
				--index;
			}
			child = child.getNextSibling();
		}
		appendChild( newCell );
		return newCell;
	}
	public void setAlign( String align )
	{
		setAttribute( "align", align );
	}
	public void setBgColor( String bgColor )
	{
		setAttribute( "bgcolor", bgColor );
	}
	public void setCells( HTMLCollection cells )
	{
		Node    child;
		int        i;
		
		child = getFirstChild();
		while ( child != null )
		{
			removeChild( child );
			child = child.getNextSibling();
		}
		i = 0;
		child = cells.item( i );
		while ( child != null )
		{
			appendChild ( child );
			++i;
			child = cells.item( i );
		}
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
	public void setRowIndex( int rowIndex )
	{
		Node    parent;
		
		parent = getParentNode();
		if ( parent instanceof HTMLTableSectionElement )
			parent = parent.getParentNode();
		if ( parent instanceof HTMLTableElement )
			( (HTMLTableElementImpl) parent ).insertRowX( rowIndex, this );
	}
	public void setSectionRowIndex( int sectionRowIndex )
	{
		Node    parent;
		
		parent = getParentNode();
		if ( parent instanceof HTMLTableSectionElement )
			( (HTMLTableSectionElementImpl) parent ).insertRowX( sectionRowIndex, this );
	}
	public void setVAlign( String vAlign )
	{
		setAttribute( "valign", vAlign );
	}
}