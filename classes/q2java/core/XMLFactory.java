package q2java.core;

import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Interface for classes providing XML/DOM services.
 *
 * @author Barry Pederson
 */
public interface XMLFactory 
	{
	
/**
 * Create a blank DOM document.
 * @param rootNodeName name of the document element, which is automatically
 *  created.
 * @return org.w3c.dom.Document
 */
Document createXMLDocument(String rootNodeName);
/**
 * Read an XML Document.
 * @return org.w3c.dom.Document
 * @param source java.io.Reader
 * @param sourceName java.lang.String
 */
Document readXMLDocument(Reader source, String sourceName) throws IOException;
/**
 * Write a document to an output stream.
 * @param doc org.w3c.dom.Document
 * @param w java.io.Writer
 * @param style a hint about whether we want pretty or compact XML
 *   (one of the XMLTools.OUTPUT_* constants)
 */
void writeXMLDocument(Document doc, Writer w, int style) throws IOException;
}