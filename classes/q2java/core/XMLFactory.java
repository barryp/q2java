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
 * This method was created in VisualAge.
 * @return org.w3c.dom.Document
 * @param source InputStream
 * @param sourceName java.lang.String
 */
Document readXMLDocument(InputStream source, String sourceName) throws IOException;
/**
 * Write a document to an output stream.
 * @param doc org.w3c.dom.Document
 * @param os java.io.OutputStream
 */
void writeXMLDocument(Document doc, OutputStream os) throws IOException;
}