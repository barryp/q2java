package q2java.core;

import java.io.*;

import org.w3c.dom.Document;

import org.openxml.DOMFactory;
import org.openxml.parser.XMLParser;
import org.openxml.x3p.*;

/**
 * Use OpenXML to provide DOM/XML services.
 *
 * @author Barry Pederson
 */
public class OpenXMLFactory implements XMLFactory 
	{
	
/**
 * Create a blank DOM XML Document.
 */
public org.w3c.dom.Document createXMLDocument() 
	{
	return DOMFactory.createXMLDocument();
	}
/**
 * Read an XML file into a DOM document.
 */
public Document readXMLDocument(Reader r, String sourceName) throws IOException
	{
	XMLParser p = new XMLParser(r, sourceName);
	return p.parseDocument();
	}
/**
 * write a DOM document to an XML stream.
 */
public void writeXMLDocument(Document doc, Writer w) throws IOException
	{
	Publisher pub = PublisherFactory.createPublisher(w, StreamFormat.XML_PRETTY );
	pub.publish(doc);
	pub.close();	
	}
}