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
 * @see org.w3c.dom.html.HTMLInputElement
 * @see ElementImpl
 */
public final class HTMLInputElementImpl
    extends HTMLElementImpl
    implements HTMLInputElement, HTMLFormControl
{
    
    
    public String getDefaultValue()
    {
        // ! NOT FULLY IMPLEMENTED !
        return getAttribute( "defaultValue" );
    }
    
    
    public void setDefaultValue( String defaultValue )
    {
        // ! NOT FULLY IMPLEMENTED !
        setAttribute( "defaultValue", defaultValue );
    }
    
    
    public boolean getDefaultChecked()
    {
        // ! NOT FULLY IMPLEMENTED !
        return getBinary( "defaultChecked" );
    }
    
    
    public void setDefaultChecked( boolean defaultChecked )
    {
        // ! NOT FULLY IMPLEMENTED !
        setAttribute( "defaultChecked", defaultChecked );
    }
  
    
    public String getAccept()
    {
        return getAttribute( "accept" );
    }
    
    
    public void setAccept( String accept )
    {
        setAttribute( "accept", accept );
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
    
    
    public String getAlign()
    {
        return capitalize( getAttribute( "align" ) );
    }
    
    
    public void setAlign( String align )
    {
        setAttribute( "align", align );
    }
    
    
    public String getAlt()
    {
        return getAttribute( "alt" );
    }
    
    
    public void setAlt( String alt )
    {
        setAttribute( "alt", alt );
    }
    
    
    public boolean getChecked()
    {
        return getBinary( "checked" );
    }
    
    
    public void setChecked( boolean checked )
    {
        setAttribute( "checked", checked );
    }
  
    
    public boolean getDisabled()
    {
        return getBinary( "disabled" );
    }
    
    
    public void setDisabled( boolean disabled )
    {
        setAttribute( "disabled", disabled );
    }
    
    
    public int getMaxLength()
    {
        return getInteger( getAttribute( "maxlength" ) );
    }
    
    
    public void setMaxLength( int maxLength )
    {
        setAttribute( "maxlength", String.valueOf( maxLength ) );
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
    
    
    public String getSize()
    {
        return getAttribute( "size" );
    }
    
    
    public void setSize( String size )
    {
        setAttribute( "size", size );
    }
    
    
    public String getSrc()
    {
        return getAttribute( "src" );
    }
    
    
    public void setSrc( String src )
    {
        setAttribute( "src", src );
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
    
    
    public String getUseMap()
    {
        return getAttribute( "useMap" );
    }
    
    
    public void setUseMap( String useMap )
    {
        setAttribute( "useMap", useMap );
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
    
    
    public void click()
    {
        // No scripting in server-side DOM. This method is moot.
    }

  
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLInputElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}


