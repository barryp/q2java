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
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:57:04 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLObjectElement
 * @see ElementImpl
 */
public final class HTMLObjectElementImpl
    extends HTMLElementImpl
    implements HTMLObjectElement, HTMLFormControl
{
    
    

    public String getCode()
    {
        return getAttribute( "code" );
    }
    
    
    public void setCode( String code )
    {
        setAttribute( "code", code );
    }

  
    public String getAlign()
    {
        return capitalize( getAttribute( "align" ) );
    }
    
    
    public void setAlign( String align )
    {
        setAttribute( "align", align );
    }
  
    
    public String getArchive()
    {
        return getAttribute( "archive" );
    }
    
    
    public void setArchive( String archive )
    {
        setAttribute( "archive", archive );
    }
    
    public String getBorder()
    {
        return getAttribute( "border" );
    }
    
    
    public void setBorder( String border )
    {
        setAttribute( "border", border );
    }

    
    public String getCodeBase()
    {
        return getAttribute( "codebase" );
    }
    
    
    public void setCodeBase( String codeBase )
    {
        setAttribute( "codebase", codeBase );
    }

    
    public String getCodeType()
    {
        return getAttribute( "codetype" );
    }
    
    
    public void setCodeType( String codeType )
    {
        setAttribute( "codetype", codeType );
    }

    
    public String getData()
    {
        return getAttribute( "data" );
    }
    
    
    public void setData( String data )
    {
        setAttribute( "data", data );
    }

  
      public boolean getDeclare()
    {
        return getBinary( "declare" );
    }
    
    
    public void setDeclare( boolean declare )
    {
        setAttribute( "declare", declare );
    }

    
    public String getHeight()
    {
        return getAttribute( "height" );
    }
    
    
    public void setHeight( String height )
    {
        setAttribute( "height", height );
    }

    
    public String getHspace()
    {
        return getAttribute( "hspace" );
    }
    
    
    public void setHspace( String hspace )
    {
        setAttribute( "hspace", hspace );
    }
  
    public String getName()
    {
        return getAttribute( "name" );
    }
    
    
    public void setName( String name )
    {
        setAttribute( "name", name );
    }

    
    public String getStandby()
    {
        return getAttribute( "standby" );
    }
    
    
    public void setStandby( String standby )
    {
        setAttribute( "standby", standby );
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
    
    
    public void setTabIndex( int tabIndex )
    {
        setAttribute( "tabindex", String.valueOf( tabIndex ) );
    }

    
    public String getType()
    {
        return getAttribute( "type" );
    }
    
    
    public void setType( String type )
    {
        setAttribute( "type", type );
    }
    
    
    public String getUseMap()
    {
        return getAttribute( "useMap" );
    }
    
    
    public void setUseMap( String useMap )
    {
        setAttribute( "useMap", useMap );
    }
    
    
    public String getVspace()
    {
        return getAttribute( "vspace" );
    }
    
    
    public void setVspace( String vspace )
    {
        setAttribute( "vspace", vspace );
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
    public HTMLObjectElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}

