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
 * Copy the source node into (inside) the destination element -
 * the two may (should) be in different documents (if they were
 * in the same document - there are much easier ways to do this).
 *
 * @param source org.w3c.dom.Element
 * @param dest org.w3c.dom.Element
 * @param skipRootElement - if the source is an Element, we may skip copying
 *  the source element itself, and only copy its children into the source.
 *  If the source is a Document, we may choose to skip the document's root element.
 */
public static void copy(Node source, Element dest, boolean skipSourceRootElement) 
	{
	switch (source.getNodeType())
		{
		case Node.ELEMENT_NODE:
			Element newElement;
			if (skipSourceRootElement)
				newElement = dest;
			else
				{
				newElement = ((Document)dest.getOwnerDocument()).createElement(((Element)source).getTagName());

				// copy attributes
				NamedNodeMap nnm = source.getAttributes();
				for (int i = 0; i < nnm.getLength(); i++)
					{
					Node n = nnm.item(i);
					newElement.setAttribute(n.getNodeName(), n.getNodeValue());
					}

				// put new element under the destination
				dest.appendChild(newElement);
				}

			// copy children
			NodeList nl = source.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++)
				{
				Node n = nl.item(i);
				copy(n, newElement, false);
				}
			break;

		case Node.TEXT_NODE:
			Text newText = ((Document)dest.getOwnerDocument()).createTextNode(source.getNodeValue());
			dest.appendChild(newText);			
			break;
			
		case Node.CDATA_SECTION_NODE:
			CDATASection newCData = ((Document)dest.getOwnerDocument()).createCDATASection(source.getNodeValue());
			dest.appendChild(newCData);			
			break;
			
		case Node.PROCESSING_INSTRUCTION_NODE:
			ProcessingInstruction newPI = ((Document)dest.getOwnerDocument()).createProcessingInstruction(((ProcessingInstruction)source).getTarget(), ((ProcessingInstruction)source).getData());
			dest.appendChild(newPI);			
			break;
			
		case Node.COMMENT_NODE:
			Comment newComment = ((Document)dest.getOwnerDocument()).createComment(source.getNodeValue());
			dest.appendChild(newComment);			
			break;

		case Node.DOCUMENT_NODE:
			copy(((Document)source).getDocumentElement(), dest, skipSourceRootElement);
			break;
		}			
	}
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