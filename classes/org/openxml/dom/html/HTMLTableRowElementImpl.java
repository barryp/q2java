/**
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.openxml.org/license/
 *
 * THIS SOFTWARE AND DOCUMENTATION IS PROVIDED ON AN "AS IS" BASIS
 * WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED,
 * INCLUDING AND WITHOUT LIMITATION, WARRANTIES THAT THE SOFTWARE
 * AND DOCUMENTATION IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGING. SEE THE LICENSE FOR THE
 * SPECIFIC LANGUAGE GOVERNING RIGHTS AND LIMITATIONS UNDER THE
 * LICENSE.
 *
 * The Initial Developer of this code under the License is
 * OpenXML.org. Portions created by OpenXML.org and/or Assaf Arkin
 * are Copyright (C) 1998, 1999 OpenXML.org. All Rights Reserved.
 */


package org.openxml.dom.html;


import org.openxml.dom.*;
import org.w3c.dom.*;
import org.w3c.dom.html.*;


/**
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:57:05 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLTableRowElement
 * @see ElementImpl
 */
public final class HTMLTableRowElementImpl
    extends HTMLElementImpl
    implements HTMLTableRowElement
{

    
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
    
    
    public void setRowIndex( int rowIndex )
    {
        Node    parent;
        
        parent = getParentNode();
        if ( parent instanceof HTMLTableSectionElement )
            parent = parent.getParentNode();
        if ( parent instanceof HTMLTableElement )
            ( (HTMLTableElementImpl) parent ).insertRowX( rowIndex, this );
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
    
    
    public void setSectionRowIndex( int sectionRowIndex )
    {
        Node    parent;
        
        parent = getParentNode();
        if ( parent instanceof HTMLTableSectionElement )
            ( (HTMLTableSectionElementImpl) parent ).insertRowX( sectionRowIndex, this );
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

  
    public HTMLCollection  getCells()
    {
        if ( _cells == null )
            _cells = new HTMLCollectionImpl( this, HTMLCollectionImpl.CELL );
        return _cells;
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
    
    
    public void setAlign( String align )
    {
        setAttribute( "align", align );
    }

    
    public String getBgColor()
    {
        return getAttribute( "bgcolor" );
    }
    
    
    public void setBgColor( String bgColor )
    {
        setAttribute( "bgcolor", bgColor );
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
    
    
    public void setCh( String ch )
    {
        // Make sure that the access key is a single character.
        if ( ch != null && ch.length() > 1 )
            ch = ch.substring( 0, 1 );
        setAttribute( "char", ch );
    }

    
    public String getChOff()
    {
        return getAttribute( "charoff" );
    }
    
    
    public void setChOff( String chOff )
    {
        setAttribute( "charoff", chOff );
    }
  
  
    public String getVAlign()
    {
        return capitalize( getAttribute( "valign" ) );
    }
    
    
    public void setVAlign( String vAlign )
    {
        setAttribute( "valign", vAlign );
    }

    
      /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLTableRowElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }
  
  
    HTMLCollection    _cells;

  
}

