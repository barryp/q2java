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
 * @see org.w3c.dom.html.HTMLModElement
 * @see ElementImpl
 */
public final class HTMLModElementImpl
    extends HTMLElementImpl
    implements HTMLModElement
{

    
    
    public String getCite()
    {
        return getAttribute( "cite" );
    }
    
    
    public void setCite( String cite )
    {
        setAttribute( "cite", cite );
    }
    
    
      public String getDateTime()
    {
        return getAttribute( "datetime" );
    }
    
    
    public void setDateTime( String dateTime )
    {
        setAttribute( "datetime", dateTime );
    }
    

    /**
     * Constructor requires owner document and tag name.
     * 
     * @param owner The owner HTML document
     */
    public HTMLModElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}

