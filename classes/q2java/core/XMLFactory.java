package q2java.core;

import java.io.*;
import org.w3c.dom.*;

/**
 * Interface for classes providing XML/DOM services.
 *
 * @author Barry Pederson
 */
public interface XMLFactory 
	{
	
/**
 * Create a blank DOM document.
 * @return org.w3c.dom.Document
 */
Document createXMLDocument();
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