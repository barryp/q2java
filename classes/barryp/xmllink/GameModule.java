package barryp.xmllink;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import org.ml.bp.misc.XMLWriter;

import q2java.*;
import q2java.core.*;

/**
 * Module that communicates with an external process using
 * a stream of XML-tagged data.
 * 
 * @author Barry Pederson
 */
public class GameModule extends q2java.core.Gamelet implements FrameListener, PrintListener, LocaleListener
	{
	protected final static String STREAM_ENCODING = "UTF8";
	protected final static String XML_PROLOG = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	
	private final static int JOIN_TIMEOUT = 5000; // how many seconds to wait for input thread to die before giving up.
	
	protected Socket fSocket;
	protected InputHandler fInputHandler;
	protected Thread fInputHandlerThread;
	protected XMLWriter fXMLWriter;

	protected Vector fJobQueue = new Vector();
	
/**
 * Initialize a Telnet server module.
 */
public GameModule(String moduleName) 
	{
	super(moduleName);
	}
/**
 * A Game broadcast print.
 * @param printlevel int
 * @param msg java.lang.String
 */
public void bprint(int flags, String msg) 
	{
	if (fXMLWriter != null)
		{
		try
			{
			fXMLWriter.startTag("bprint");
			if (msg.endsWith("\n"))
				msg = msg.substring(0, msg.length() - 1);
			fXMLWriter.write(msg);
			fXMLWriter.endTag();
			fXMLWriter.println();
			fXMLWriter.flush();
			}
		catch (IOException ioe)
			{
			}
		}
	}
/**
 * Output text sent to the console from outside the game.
 * @param flags int
 * @param msg java.lang.String
 */
public void consoleOutput(String msg) 
	{
	if (fXMLWriter != null)
		{
		try
			{
			fXMLWriter.startTag("console");
			fXMLWriter.write(msg);
			fXMLWriter.endTag();
			fXMLWriter.println();
			fXMLWriter.flush();
			}
		catch (IOException ioe)
			{
			}
		}
	}
/**
 * Handle Game dprint calls.
 * @param msg java.lang.String
 */
public void dprint(String msg) 
	{
	if (fXMLWriter != null)
		{
		try
			{
			fXMLWriter.startTag("dprint");
			if (msg.endsWith("\n"))
				msg = msg.substring(0, msg.length() - 1);
			fXMLWriter.write(msg);
			fXMLWriter.endTag();
			fXMLWriter.println();
			fXMLWriter.flush();
			}
		catch (IOException ioe)
			{
			}
		}	
	}
/**
 * Called when the listener receives a localized broadcast message.
 * @param printLevel One of the Engine.PRINT_* constants
 * @param msg java.lang.String
 */
public void localecast(Locale loc, int printLevel, String msg)	
	{
	if (fXMLWriter != null)
		{
		try
			{
			fXMLWriter.startTag("localecast");
			fXMLWriter.addTagAttribute("xml:lang", loc.toString().replace('_', '-'));
			if (msg.endsWith("\n"))
				msg = msg.substring(0, msg.length() - 1);
			fXMLWriter.write(msg);
			fXMLWriter.endTag();
			fXMLWriter.println();
			fXMLWriter.flush();
			}
		catch (IOException ioe)
			{
			}
		}
	}
/**
 * Queue up a job that can be run when the main thread comes around.
 * @param r java.lang.Runnable
 */
public void queueJob(XMLJob job) 
	{
	fJobQueue.addElement(job);
	}
/**
 * Called by the q2jgame.Game object every so often.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	// if the input connection closed, finish disconnecting
	if ((fInputHandlerThread != null) && (!fInputHandlerThread.isAlive()))
		{
		svcmd_disconnect(null);
		return;
		}

	// Run any jobs queued for the main thread
	//	
	// shouldn't have to worry about synchronization
	// since this is the only method pulling things
	// out of the vector.
	while (fJobQueue.size() > 0)
		{
		XMLJob job = (XMLJob) fJobQueue.elementAt(0);
		fJobQueue.removeElementAt(0);
		try
			{
			job.run(this);
			}
		catch (Throwable t)
			{
			t.printStackTrace();
			}
		}
	}
/**
 * Run the "sv connect" command.
 * @param args java.lang.String[]
 */
public void svcmd_connect(String[] args) 
	{
	if (args.length < 3)
		{
		Game.dprint("Usage: connect <host:port>\n");
		return;
		}

	if (fSocket != null)
		{
		Game.dprint("Already connected, use \"disconnect\" first\n");
		return;
		}

	int p = args[2].indexOf(':');
	if (p < 1)
		{
		Game.dprint("Invalid parameter, [" + args[2] + "] should be \"host:port\"\n");
		return;
		}

	String host = args[2].substring(0, p);
	int port;
	try
		{
		port = Integer.parseInt(args[2].substring(p+1));
		}
	catch (NumberFormatException nfe)
		{
		Game.dprint("Ports should be specified as an integer value\n");
		return;
		}

	try
		{
		// connect to the external process
		fSocket = new Socket(host, port);

		// start reading the incoming XML stream
		fInputHandler = new InputHandler(this, fSocket);
		fInputHandlerThread = new Thread(fInputHandler);
		fInputHandlerThread.start();
		
		// start our outgoing XML stream
		fXMLWriter = new XMLWriter(new OutputStreamWriter(fSocket.getOutputStream(), STREAM_ENCODING));
		fXMLWriter.writeRawString(XML_PROLOG);
		fXMLWriter.println();
		fXMLWriter.startTag("session");
		
		Game.addFrameListener(this, Game.FRAME_BEGINNING, 0, 0);
		Game.addPrintListener(this);
		Game.addLocaleListener(this);
		}
	catch (IOException ioe)
		{
		ioe.printStackTrace();
		}
	}
/**
 * Run the "sv disconnect" command.
 * @param args java.lang.String[]
 */
public void svcmd_disconnect(String[] args) 
	{
	// stop event calls from the game
	Game.removeFrameListener(this, Game.FRAME_BEGINNING);
	Game.removePrintListener(this);
	Game.removeLocaleListener(this);
	
	// close the output stream
	if (fXMLWriter != null)
		{
		try
			{
			fXMLWriter.close();
			}
		catch (IOException ioe)
			{
			}
			
		fXMLWriter = null;
		}

	// wait for our input thread to die, since we
	// closed our output stream..our peer should
	// hopefully close his and our thread will stop
	// on its own.
	if (fInputHandlerThread != null)
		{
		try
			{
			fInputHandlerThread.join(JOIN_TIMEOUT);
			fInputHandlerThread = null;
			}
		catch (InterruptedException ie)
			{
			}			
		}
		
	// still running...cut it off on our end
	if (fInputHandlerThread != null)
		{
		fInputHandler.shutdown();
		try
			{
			// and give it a little more time to finish
			fInputHandlerThread.join(JOIN_TIMEOUT);
			}
		catch (InterruptedException ie)
			{
			}			
		}

	// that's it, we give up on those guys
	fInputHandlerThread = null;
	fInputHandler = null;
		
	// close the socket		
	if (fSocket != null)
		{
		try
			{
			fSocket.close();
			}
		catch (IOException ioe)
			{
			}
			
		fSocket = null;
		}
	}
/**
 * Display help info to the console.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("Q2Java XML-Link module\n\n");
	Game.dprint("    sv commands:\n");
	Game.dprint("       connect <host[:port]>\n");
	Game.dprint("       disconnect\n");	
	}
/**
 * Called when this game module is being unloaded.
 */
public void unload() 
	{
	svcmd_disconnect(null);
	}
}