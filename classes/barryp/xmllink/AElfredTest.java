package barryp.xmllink;

import java.io.*;

import org.xml.sax.*;
import com.microstar.xml.*;

/**
 * Test the AELfred XML Parser.
 */
public class AElfredTest extends org.xml.sax.HandlerBase
	{
	
/**
 * Receive notification of character data inside an element.
 *
 * <p>By default, do nothing.  Application writers may override this
 * method to take specific actions for each chunk of character data
 * (such as adding the data to a node or buffer, or printing it to
 * a file).</p>
 *
 * @param ch The characters.
 * @param start The start position in the character array.
 * @param length The number of characters to use from the
 *               character array.
 * @exception org.xml.sax.SAXException Any SAX exception, possibly
 *            wrapping another exception.
 * @see org.xml.sax.DocumentHandler#characters
 */
public void characters (char ch[], int start, int length) throws SAXException
	{
	String s = new String(ch, start, length);
	System.out.print(s);
	}    
/**
 * Receive notification of the end of an element.
 *
 * <p>By default, do nothing.  Application writers may override this
 * method in a subclass to take specific actions at the end of
 * each element (such as finalising a tree node or writing
 * output to a file).</p>
 *
 * @param name The element type name.
 * @param attributes The specified or defaulted attributes.
 * @exception org.xml.sax.SAXException Any SAX exception, possibly
 *            wrapping another exception.
 * @see org.xml.sax.DocumentHandler#endElement
 */
public void endElement(String name) throws SAXException
	{
	System.out.println("End: " + name);
	}    
/**
 * Receive notification of a recoverable parser error.
 *
 * <p>The default implementation does nothing.  Application writers
 * may override this method in a subclass to take specific actions
 * for each error, such as inserting the message in a log file or
 * printing it to the console.</p>
 *
 * @param e The warning information encoded as an exception.
 * @exception org.xml.sax.SAXException Any SAX exception, possibly
 *            wrapping another exception.
 * @see org.xml.sax.ErrorHandler#warning
 * @see org.xml.sax.SAXParseException
 */
public void error (SAXParseException e) throws SAXException
	{
	e.printStackTrace();
	}    
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) throws Exception
	{
	Parser p = new SAXDriver();
	org.xml.sax.HandlerBase base = new AElfredTest();

	p.setDocumentHandler(base);
	p.setDTDHandler(base);
	p.setEntityResolver(base);
	p.setErrorHandler(base);

	FileReader fr = new FileReader("c:\\test.xml");
	InputSource is = new InputSource(fr);

	p.parse(is);
	
	fr.close();
	}
/**
 * Receive notification of the start of an element.
 *
 * <p>By default, do nothing.  Application writers may override this
 * method in a subclass to take specific actions at the start of
 * each element (such as allocating a new tree node or writing
 * output to a file).</p>
 *
 * @param name The element type name.
 * @param attributes The specified or defaulted attributes.
 * @exception org.xml.sax.SAXException Any SAX exception, possibly
 *            wrapping another exception.
 * @see org.xml.sax.DocumentHandler#startElement
 */
public void startElement (String name, AttributeList attributes) throws SAXException
	{
	int n = attributes.getLength();
	System.out.println("Start: " + name + " (" + n + " attributes)");
	for (int i = 0; i < attributes.getLength(); i++)
	  	System.out.println(" " + i + " " + attributes.getName(i) + "=" + attributes.getValue(i));

	System.out.println("Locale: " + attributes.getValue("locale"));	  	
	}    
}