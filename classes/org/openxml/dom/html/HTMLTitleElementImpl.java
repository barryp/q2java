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
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLTitleElement
 * @see ElementImpl
 */
public final class HTMLTitleElementImpl
    extends HTMLElementImpl
    implements HTMLTitleElement
{
 
    
    public String getText()
    {
        Node    child;
        String    text;
        
        // Find the Text nodes contained within this element and return their
        // concatenated value. Required to go around comments, entities, etc.
        child = getFirstChild();
        text = "";
        while ( child != null )
        {
            if ( child instanceof Text )
                text = text + ( (Text) child ).getData();
            child = child.getNextSibling();
        }
        return text;
    }
    
    
    public void setText( String text )
    {
        Node    child;
        Node    next;
        
        // Delete all the nodes and replace them with a single Text node.
        // This is the only approach that can handle comments and other nodes.
        child = getFirstChild();
        while ( child != null )
        {
            next = child.getNextSibling();
            removeChild( child );
            child = next;
        }
        insertBefore( getOwnerDocument().createTextNode( text ), getFirstChild() );
    }

        
      /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLTitleElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }

  
}

