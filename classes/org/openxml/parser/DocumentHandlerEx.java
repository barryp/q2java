package org.openxml.parser;


import org.w3c.dom.*;
import org.xml.sax.*;


public interface DocumentHandlerEx
    extends DocumentHandler
{

    
    public void characters( String text )
        throws SAXException;


    public void comment( String text )
        throws SAXException;

    
    public void cdataSection( String text )
        throws SAXException;

    
    public void entityReference( String text )
        throws SAXException;


    public void setDocumentType( String rootTagName, String systemId, String publicId )
        throws SAXException;


}
