package barryp.xmllink;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.AttributeListImpl;

/**
 * Handle input from an XML-Link connection.
 * 
 * @author Barry Pederson
 */
class InputHandler extends HandlerBase implements Runnable
	{
	private final static Class PARSER_CLASS = com.microstar.xml.SAXDriver.class;
	
	private final static int CLIENT_TIMEOUT = 500; // how often to unblock from reading from the client to check if we're quitting.
	private GameModule fParent;	
	private Socket fSocket;

	private boolean fShutdown;
	private InterruptableReader fReader;

	private StringBuffer fBuffer = new StringBuffer();
	private String fTagName;
	private AttributeList fAttributeList;
	
/**
 * Create an InputHandler thread.
 * @param group java.lang.ThreadGroup
 * @param name java.lang.String
 */
InputHandler(GameModule parent, Socket socket) 
	{
	super();

	fParent = parent;
	fSocket = socket;
	}
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
	fBuffer.append(ch, start, length);
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
	try
		{
		Class cls = Class.forName("barryp.xmllink.xml_" + name);
		XMLJob job = (XMLJob) cls.newInstance();
		
		String s;
		if (fBuffer.length() > 0)
			s = fBuffer.toString();
		else
			s = null;
			
		job.setParams(fAttributeList, s);
		fParent.queueJob(job);
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
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
	XMLJob chat = new xml_chat();
	chat.setParams(null, e.getMessage());
	fParent.queueJob(chat);
	}    
/**
 * Talk with the client.
 */
public void run() 
	{
	try
		{
		fSocket.setSoTimeout(CLIENT_TIMEOUT);
		}
	catch (SocketException se)
		{
		}

	try
		{
		fReader = new InterruptableReader(new InputStreamReader(fSocket.getInputStream(), GameModule.STREAM_ENCODING));
		InputSource is = new InputSource(fReader);

		try
			{
			Parser p = (Parser) PARSER_CLASS.newInstance();
			p.setDocumentHandler(this);
			p.setDTDHandler(this);
			p.setEntityResolver(this);
			p.setErrorHandler(this);
		
			p.parse(is);
			}
		catch (Exception e)
			{
			}
			
		fReader.close();
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
/**
 * Called to signal that this thread should shutdown.
 */
void shutdown() 
	{
	if (fReader != null)
		fReader.shutdown();
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
	  fTagName = name;
	  fAttributeList = new AttributeListImpl(attributes);
	  fBuffer.setLength(0);
	  }    
}