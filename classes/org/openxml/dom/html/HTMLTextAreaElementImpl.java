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
 * @see org.w3c.dom.html.HTMLTextAreaElement
 * @see ElementImpl
 */
public final class HTMLTextAreaElementImpl
    extends HTMLElementImpl
    implements HTMLTextAreaElement, HTMLFormControl
{
    
    
    public String getDefaultValue()
    {
        // ! NOT FULLY IMPLEMENTED !
        return getAttribute( "default-value" );
    }
    
    
    public void setDefaultValue( String defaultValue )
    {
        // ! NOT FULLY IMPLEMENTED !
        setAttribute( "default-value", defaultValue );
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
    
    
    public void setAccessKey( String accessKey )
    {
        // Make sure that the access key is a single character.
        if ( accessKey != null && accessKey.length() > 1 )
            accessKey = accessKey.substring( 0, 1 );
        setAttribute( "accesskey", accessKey );
    }

    
    public int getCols()
    {
        return getInteger( getAttribute( "cols" ) );
    }
    
    
    public void setCols( int cols )
    {
        setAttribute( "cols", String.valueOf( cols ) );
    }
  
  
    public boolean getDisabled()
    {
        return getBinary( "disabled" );
    }
    
    
    public void setDisabled( boolean disabled )
    {
        setAttribute( "disabled", disabled );
    }

    
    public String getName()
    {
        return getAttribute( "name" );
    }
    
    
    public void setName( String name )
    {
        setAttribute( "name", name );
    }

    
    public boolean getReadOnly()
    {
        return getBinary( "readonly" );
    }
    
    
    public void setReadOnly( boolean readOnly )
    {
        setAttribute( "readonly", readOnly );
    }

    
       public int getRows()
    {
        return getInteger( getAttribute( "rows" ) );
    }
    
    
    public void setRows( int rows )
    {
        setAttribute( "rows", String.valueOf( rows ) );
    }

  
       public int getTabIndex()
    {
        return getInteger( getAttribute( "tabindex" ) );
    }
    
    
    public void setTabIndex( int tabIndex )
    {
        setAttribute( "tabindex", String.valueOf( tabIndex ) );
    }

  
    public String getType()
    {
        return getAttribute( "type" );
    }

    
      public String getValue()
    {
        return getAttribute( "value" );
    }
    
    
    public void setValue( String value )
    {
        setAttribute( "value", value );
    }

    
    public void blur()
    {
        // No scripting in server-side DOM. This method is moot.
    }
    
    
    public void focus()
    {
        // No scripting in server-side DOM. This method is moot.
    }
    
    
    public void select()
    {
        // No scripting in server-side DOM. This method is moot.
    }
    
      
      /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLTextAreaElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }

  
}

