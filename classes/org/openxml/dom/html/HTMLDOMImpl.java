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


/**
 * Sep 23, 1999
 * + Added HTMLDOMImplemented outside of HTMLDocument.
 * + Added DOM 2 support with createHTMLDocument().
 **/


package org.openxml.dom.html;
    

import org.w3c.dom.DOMException;
import org.w3c.dom.html.*;
import org.openxml.dom.DOMImpl;


/**
 * Provides number of methods for performing operations that are independent
 * of any particular instance of the document object model. This class is
 * unconstructable, the only way to obtain an instance of a DOM implementation
 * is by calling the static method {@link #getDOMImplementation}.
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:04 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.DOMImplementation
 */
public class HTMLDOMImpl
    extends DOMImpl
    implements HTMLDOMImplementation
{


    /**
     * Create a new HTML document of the specified <TT>TITLE</TT> text.
     *
     * @param title The document title text
     * @return New HTML document
     */
    public final HTMLDocument createHTMLDocument( String title )
        throws DOMException
    {
	HTMLDocument doc;

	if ( title == null )
	    throw new NullPointerException( "Argument 'title' is null." );
	doc = new HTMLDocumentImpl();
	doc.setTitle( title );
	return doc;
    }


    /**
     * Returns an instance of a {@link HTMLDOMImplementation} that can be
     * used to perform operations that are not specific to a particular
     * document instance, e.g. to create a new document.
     *
     * @return Reference to a valid DOM implementation
     */
    public static HTMLDOMImplementation getHTMLDOMImplementation()
    {
	return _instance;
    }


    /**
     * Private constructor assures that an object of this class cannot
     * be created. The only way to obtain an object is by calling {@link
     * #getDOMImplementation}.
     */
    private HTMLDOMImpl()
    {
    }


    /**
     * Holds a reference to the single instance of the DOM implementation.
     * Only one instance is required since this class is multiple entry.
     */
    private static HTMLDOMImplementation _instance = new HTMLDOMImpl();


}
