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
 * @see org.w3c.dom.html.HTMLBodyElement
 * @see ElementImpl
 */
public final class HTMLBodyElementImpl
    extends HTMLElementImpl
    implements HTMLBodyElement
{
    
    public String getALink()
    {
        return getAttribute( "alink" );
    }

    
    public void setALink(String aLink)
    {
        setAttribute( "alink", aLink );
    }
    
  
    public String getBackground()
    {
        return getAttribute( "background" );
    }
    
  
    public void setBackground( String background )
    {
        setAttribute( "background", background );
    }
    
  
    public String getBgColor()
    {
        return getAttribute( "bgcolor" );
    }
    
    
    public void setBgColor(String bgColor)
    {
        setAttribute( "bgcolor", bgColor );
    }
    
  
    public String getLink()
    {
        return getAttribute( "link" );
    }
  
    
    public void setLink(String link)
    {
        setAttribute( "link", link );
    }
    
  
    public String getText()
    {
        return getAttribute( "text" );
    }
    
  
    public void setText(String text)
    {
        setAttribute( "text", text );
    }
    
  
    public String getVLink()
    {
        return getAttribute( "vlink" );
    }
  
    
    public void  setVLink(String vLink)
    {
        setAttribute( "vlink", vLink );
    }
  
    
      /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLBodyElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }

  
}

