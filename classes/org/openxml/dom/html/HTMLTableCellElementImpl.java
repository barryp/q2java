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
 * @see org.w3c.dom.html.HTMLTableCellElement
 * @see ElementImpl
 */
public final class HTMLTableCellElementImpl
    extends HTMLElementImpl
    implements HTMLTableCellElement
{
    

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

  
    public String getAbbr()
    {
        return getAttribute( "abbr" );
    }
    
    
    public void setAbbr( String abbr )
    {
        setAttribute( "abbr", abbr );
    }

  
    public String getAlign()
    {
        return capitalize( getAttribute( "align" ) );
    }
    
    
    public void setAlign( String align )
    {
        setAttribute( "align", align );
    }
  
    
    public String getAxis()
    {
        return getAttribute( "axis" );
    }
    
    
    public void setAxis( String axis )
    {
        setAttribute( "axis", axis );
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
  
  
    public int getColSpan()
    {
        return getInteger( getAttribute( "colspan" ) );
    }
    
    
    public void setColSpan( int colspan )
    {
        setAttribute( "colspan", String.valueOf( colspan ) );
    }
    
    
    public String getHeaders()
    {
        return getAttribute( "headers" );
    }
    
    
    public void setHeaders( String headers )
    {
        setAttribute( "headers", headers );
    }
  
  
    public String getHeight()
    {
        return getAttribute( "height" );
    }
    
    
    public void setHeight( String height )
    {
        setAttribute( "height", height );
    }

  
      public boolean getNoWrap()
    {
        return getBinary( "nowrap" );
    }
    
    
    public void setNoWrap( boolean noWrap )
    {
        setAttribute( "nowrap", noWrap );
    }

    public int getRowSpan()
    {
        return getInteger( getAttribute( "rowspan" ) );
    }
    
    
    public void setRowSpan( int rowspan )
    {
        setAttribute( "rowspan", String.valueOf( rowspan ) );
    }
  
    
    public String getScope()
    {
        return getAttribute( "scope" );
    }
    
    
    public void setScope( String scope )
    {
        setAttribute( "scope", scope );
    }
  
  
    public String getVAlign()
    {
        return capitalize( getAttribute( "valign" ) );
    }
    
    
    public void setVAlign( String vAlign )
    {
        setAttribute( "valign", vAlign );
    }

  
      public String getWidth()
    {
        return getAttribute( "width" );
    }
    
    
    public void setWidth( String width )
    {
        setAttribute( "width", width );
    }

    
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLTableCellElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }

  
}

