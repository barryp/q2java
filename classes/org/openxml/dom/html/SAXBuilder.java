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


import org.w3c.dom.DocumentType;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.DocumentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import org.xml.sax.AttributeList;

import org.openxml.dom.DocumentImpl;


/**
 * This is a SAX document handler that is used to build an HTMLL document.
 * It can build a document from any SAX parser, but is specifically tuned
 * for working with the OpenXML XML parser.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:05 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 */
public class SAXBuilder
    extends org.openxml.dom.SAXBuilder
    implements DocumentHandler, DTDHandler
{


    protected DocumentImpl createDocument( String namespaceURI, String tagName, DocumentType docType )
    {
	return new HTMLDocumentImpl();
    }


    public void startElement( String tagName, AttributeList attrList )
        throws SAXException
    {
	super.startElement( tagName.toUpperCase(), attrList );
    }


    public void endElement( String tagName )
        throws SAXException
    {
	super.endElement( tagName.toUpperCase() );
    }


}
      
