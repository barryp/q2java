package org.openxml.dom;

import org.w3c.dom.*;
import org.xml.sax.*;


public class DOMBuilder
	implements DocumentHandler
{
	
	
	public Document     _document;
	
	
	public Node         _current;
	
	
	public void characters( char[] text, int start, int legth )
	{
		Node    node;
		
		node = _document.createTextNode( new String( text, start, legth ) );
		_current.appendChild( node );
	}
	public void endDocument()
	{
	}
	public void endElement( String tagName )
	{
		_current = _current.getParentNode();
	}
	public Document getDocument()
	{
		return _document;
	}
	public void ignorableWhitespace( char[] text, int start, int legth )
	{
	}
	public void processingInstruction( String target, String instruction )
	{
		Node    node;
		
		node = _document.createProcessingInstruction( target, instruction );
		_current.appendChild( node );
	}
	public void setDocumentLocator( Locator locator )
	{
	}
	public void startDocument()
	{
		_document = new DocumentImpl();
		_current = _document;
	}
	public void startElement( String tagName, AttributeList attrList )
	{
		Element elem;
		int     i;
		
		elem = _document.createElement( tagName );
		for ( i = 0 ; i < attrList.getLength() ; ++ i )
			elem.setAttribute( attrList.getName( i ), attrList.getValue( i ) );
		_current.appendChild( elem );
		_current = elem;
	}
}