package q2java.core;

import java.io.*;
import org.w3c.dom.*;

/**
 * Class for accessing basic XML/DOM methods.
 */
public class XMLTools 
	{
	// Object that actually provides the XML/DOM services
	private static XMLFactory gXMLFactory;	
	
/**
 * Create a blank DOM XML document.
 * @return org.w3c.dom.Document
 */
public static Document createXMLDocument() 
	{
	if (gXMLFactory == null)
		init();
		
	return gXMLFactory.createXMLDocument();
	}
/**
 * Make sure the XMLTools actually has an object to do its work.
 */
private static void init()
	{
	try
		{
		// see if the admin has specified a particular XML Factory
		// in the q2java.properties file
		String className = System.getProperty("q2java.xmlfactory", "q2java.core.OpenXMLFactory");
		gXMLFactory = (XMLFactory) Class.forName(className).newInstance();
		}
	catch (Exception e)
		{
		e.printStackTrace();
		
		// try falling back on OpenXML - if this fails then you're hosed.
		gXMLFactory = new OpenXMLFactory();
		}
	}
/**
 * Read an XML file into a DOM document.
 */
public static Document readXMLDocument(Reader r, String sourceName) throws IOException
	{
	if (gXMLFactory == null)
		init();
	
	return gXMLFactory.readXMLDocument(r, sourceName);
	}
/**
 * Write a DOM document as an XML stream.
 */
public static void writeXMLDocument(Document doc, Writer w) throws IOException
	{
	if (gXMLFactory == null)
		init();
	
	gXMLFactory.writeXMLDocument(doc, w);
	}
}