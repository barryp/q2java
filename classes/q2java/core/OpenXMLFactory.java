package q2java.core;

import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xml.serialize.*;
import org.openxml.dom.DOMImpl;
import org.openxml.parser.XMLParser;

/**
 * Use OpenXML to provide DOM/XML services.
 *
 * @author Barry Pederson
 */
public class OpenXMLFactory implements XMLFactory 
	{
	private DOMImplementation fDOMImpl = DOMImpl.getDOMImplementation();
	
/**
 * Create a blank DOM XML Document.
 */
public Document createXMLDocument(String rootNodeName) 
	{
	return fDOMImpl.createDocument(null, rootNodeName, null);
	}
/**
 * Read an XML file into a DOM document.
 */
public Document readXMLDocument(Reader r, String sourceName) throws IOException
	{
	InputSource is = new InputSource(r);
	XMLParser p = new XMLParser();
	try
		{
		p.parse(is);
		}
	catch (SAXException se)
		{
		throw new IOException("SAX: " + se.getMessage());
		}
	return p.getDocument();
	}
/**
 * write a DOM document to an XML stream.
 */
public void writeXMLDocument(Document doc, Writer w, int outputStyle) throws IOException
	{
	OutputFormat of = new OutputFormat();

	switch (outputStyle)
		{
		case XMLTools.OUTPUT_PRETTY:
			of.setIndenting(true);
			break;
			
		case XMLTools.OUTPUT_COMPACT:
			of.setIndenting(false);
			break;			
		}
	
	XMLSerializer xs = new XMLSerializer(w, of);
	xs.serialize(doc);
	}
}