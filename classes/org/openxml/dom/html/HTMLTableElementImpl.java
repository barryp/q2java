package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLTableElementImpl.java
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
 * @see org.w3c.dom.html.HTMLAnchorElement
 * @see ElementImpl
 */
public final class HTMLTableElementImpl
	extends HTMLElementImpl
	implements HTMLTableElement
{
	
	
	private HTMLCollectionImpl    _rows;
	
	
	private HTMLCollectionImpl    _bodies;
  
	
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLTableElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "TABLE" );
	}
	public synchronized HTMLElement createCaption()
	{
		HTMLElement    section;
		
		section = getCaption();
		if ( section != null )
			return section;
		section = new HTMLTableSectionElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "CAPTION" );
		appendChild( section );
		return section;
	}
	public synchronized HTMLElement createTFoot()
	{
		HTMLElement    section;
		
		section = getTFoot();
		if ( section != null )
			return section;
		section = new HTMLTableSectionElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "TFOOT" );
		appendChild( section );
		return section;
	}
	public synchronized HTMLElement createTHead()
	{
		HTMLElement    section;
		
		section = getTHead();
		if ( section != null )
			return section;
		section = new HTMLTableSectionElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "THEAD" );
		appendChild( section );
		return section;
	}
	public synchronized void deleteCaption()
	{
		Node    old;
		
		old = getCaption();
		if ( old != null )
			removeChild ( old );
	}
	public synchronized void deleteRow( int index )
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
					return;
				}
			}
			else
			if ( child instanceof HTMLTableSectionElementImpl )
			{
				index = ( (HTMLTableSectionElementImpl) child ).deleteRowX( index );
				if ( index < 0 )
					return;
			}
			child = child.getNextSibling();
		}
	}
	public synchronized void deleteTFoot()
	{
		Node    old;
		
		old = getTFoot();
		if ( old != null )
			removeChild ( old );
	}
	public synchronized void deleteTHead()
	{
		Node    old;
		
		old = getTHead();
		if ( old != null )
			removeChild ( old );
	}
	public String getAlign()
	{
		return capitalize( getAttribute( "align" ) );
	}
	public String getBgColor()
	{
		return getAttribute( "bgcolor" );
	}
	public String getBorder()
	{
		return getAttribute( "border" );
	}
	public synchronized HTMLTableCaptionElement getCaption()
	{
		Node    child;
		
		child = getFirstChild();
		while ( child != null )
		{
			if ( child instanceof HTMLTableCaptionElement &&
				 child.getNodeName().equals( "CAPTION" ) )
				return (HTMLTableCaptionElement) child;
			child = child.getNextSibling();
		}
		return null;
	}
	public String getCellPadding()
	{
		return getAttribute( "cellpadding" );
	}
	public String getCellSpacing()
	{
		return getAttribute( "cellspacing" );
	}
	public String getFrame()
	{
		return capitalize( getAttribute( "frame" ) );
	}
	public HTMLCollection getRows()
	{
		if ( _rows == null )
			_rows = new HTMLCollectionImpl( this, HTMLCollectionImpl.ROW );
		return _rows;
	}
	public String getRules()
	{
		return capitalize( getAttribute( "rules" ) );
	}
	public String getSummary()
	{
		return getAttribute( "summary" );
	}
	public HTMLCollection getTBodies()
	{
		if ( _bodies == null )
			_bodies = new HTMLCollectionImpl( this, HTMLCollectionImpl.TBODY );
		return _bodies;
	}
	public synchronized HTMLTableSectionElement getTFoot()
	{
		Node    child;
		
		child = getFirstChild();
		while ( child != null )
		{
			if ( child instanceof HTMLTableSectionElement &&
				 child.getNodeName().equals( "TFOOT" ) )
				return (HTMLTableSectionElement) child;
			child = child.getNextSibling();
		}
		return null;
	}
	public synchronized HTMLTableSectionElement getTHead()
	{
		Node    child;
		
		child = getFirstChild();
		while ( child != null )
		{
			if ( child instanceof HTMLTableSectionElement &&
				 child.getNodeName().equals( "THEAD" ) )
				return (HTMLTableSectionElement) child;
			child = child.getNextSibling();
		}
		return null;
	}
	  public String getWidth()
	{
		return getAttribute( "width" );
	}
	public HTMLElement insertRow( int index )
	{
		HTMLTableRowElementImpl    newRow;

		newRow = new HTMLTableRowElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "TR" );
		newRow.insertCell( 0 );
		insertRowX( index, newRow );
		return newRow;
	}
	void insertRowX( int index, HTMLTableRowElementImpl newRow )
	{
		Node    child;
		Node    lastSection = null;
				
		child = getFirstChild();
		while ( child != null )
		{
			if ( child instanceof HTMLTableRowElement )
			{
				if ( index == 0 )
				{
					insertBefore( newRow, child );
					return;
				}
			}
			else
			if ( child instanceof HTMLTableSectionElementImpl )
			{
				lastSection = child;
				index = ( (HTMLTableSectionElementImpl) child ).insertRowX( index, newRow );
				if ( index < 0 )
					return;
			}
			child = child.getNextSibling();
		}
		if ( lastSection != null )
			lastSection.appendChild( newRow );
		else
			appendChild( newRow );
	}
	public void setAlign( String align )
	{
		setAttribute( "align", align );
	}
	public void setBgColor( String bgColor )
	{
		setAttribute( "bgcolor", bgColor );
	}
	public void setBorder( String border )
	{
		setAttribute( "border", border );
	}
	public synchronized void setCaption( HTMLTableCaptionElement caption )
	{
		if ( caption != null && ! caption.getTagName().equals( "CAPTION" ) )
			throw new IllegalArgumentException( "Argument 'caption' is not an element of type <CAPTION>." );
		deleteCaption();
		if ( caption != null )
			appendChild( caption );
	}
	public void setCellPadding( String cellPadding )
	{
		setAttribute( "cellpadding", cellPadding );
	}
	public void setCellSpacing( String cellSpacing )
	{
		setAttribute( "cellspacing", cellSpacing );
	}
	public void setFrame( String frame )
	{
		setAttribute( "frame", frame );
	}
	public void setRules( String rules )
	{
		setAttribute( "rules", rules );
	}
	public void setSummary( String summary )
	{
		setAttribute( "summary", summary );
	}
	public synchronized void setTFoot( HTMLTableSectionElement tFoot )
	{
		if ( tFoot != null && ! tFoot.getTagName().equals( "TFOOT" ) )
			throw new IllegalArgumentException( "Argument 'tFoot' is not an element of type <TFOOT>." );
		deleteTFoot();
		if ( tFoot != null )
			appendChild( tFoot );
	}
	public synchronized void setTHead( HTMLTableSectionElement tHead )
	{
		if ( tHead != null && ! tHead.getTagName().equals( "THEAD" ) )
			throw new IllegalArgumentException( "Argument 'tHead' is not an element of type <THEAD>." );
		deleteTHead();
		if ( tHead != null )
			appendChild( tHead );
	}
	public void setWidth( String width )
	{
		setAttribute( "width", width );
	}
}