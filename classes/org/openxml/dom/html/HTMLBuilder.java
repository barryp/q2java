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


import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.xml.sax.*;
import org.openxml.dom.*;


/**
 * This is a SAX document handler that is used to build an HTML document.
 * It can build a document from any SAX parser, but is specifically tuned
 * for working with the OpenXML HTML parser.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:04 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 */
public class HTMLBuilder
    extends SAXBuilder
    implements DocumentHandler, DTDHandler
{
    
    
    public synchronized void startElement( String tagName, AttributeList attrList )
        throws SAXException
    {
        ElementImpl elem;
        int         i;
        
	if ( tagName == null )
	    throw new SAXException( "Argument 'tagName' is null." );

	// If this is the root element, this is the time to create a new document,
	// because only know we know the document element name and namespace URI.
	if ( _document == null )
	{
	    // No need to create the element explicitly.
	    _document = new HTMLDocumentImpl();
	    elem = (ElementImpl) _document.getDocumentElement();
	    _current = elem;
	    if ( _current == null )
		throw new SAXException( "State error: Document.getDocumentElement returns null." );

	    // Insert nodes (comment and PI) that appear before the root element.
	    if ( _preRootNodes != null )
	    {
		for ( i = _preRootNodes.size() ; i-- > 0 ; )
		    _document.insertBefore( (Node) _preRootNodes.elementAt( i ), elem );
		_preRootNodes = null;
	    }
	     
	}
	else
	{
	    // This is a state error, indicates that document has been parsed in full,
	    // or that there are two root elements.
	    if ( _current == null )
		throw new SAXException( "State error: startElement called after end of document element." );
	    elem = (ElementImpl) _document.createElement( tagName );
	    _current.appendChild( elem );
	    _current = elem;
	}

	// Add the attributes (specified and not-specified) to this element.
        if ( attrList != null )
        {
            for ( i = 0 ; i < attrList.getLength() ; ++ i )
                elem.setAttribute( attrList.getName( i ), attrList.getValue( i ) );
        }
	/*
        if ( _locatorInDOM && _locator != null )
            elem.setLocator( new LocatorImpl( _locator ) );
	*/
    }


    public synchronized void cdataSection( String text )
        throws SAXException
    {
        throw new SAXException( "Not supported in HTML." );
    }

    
    public synchronized void entityReference( String text )
        throws SAXException
    {
        throw new SAXException( "Not supported in HTML." );
    }

    
    public synchronized void setDocumentType( String qualifiedName, String publicID, String systemID )
        throws SAXException
    {
        throw new SAXException( "Not supported in HTML." );
    }


    public synchronized void notationDecl( String name, String publicID, String systemID )
        throws SAXException
    {
        throw new SAXException( "Not supported in HTML." );
    }


    public synchronized void unparsedEntityDecl( String name, String publicID,
						 String systemID, String notation )
        throws SAXException
    {
        throw new SAXException( "Not supported in HTML." );
    }


    public HTMLDocument getHTMLDocument()
    {
        return (HTMLDocument) _document;
    }


    public HTMLBuilder()
    {
	super();
    }


}
