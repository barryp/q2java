
package q2jgame.telnet;

import java.io.*;
import java.net.*;
import java.util.*;

import q2java.*;
import q2jgame.*;

/**
 * Thread class that accepts Telnet-type connections,
 * spawns separate threads to handle them, and coordinates
 * communications with the Game.
 * 
 * @author Barry Pederson
 */
public class TelnetServer extends Thread  implements ConsoleListener, PrintListener
	{
	private final static int SOCKET_TIMEOUT = 500; // milliseconds
	private final static String GROUP_NAME = "Telnet Handlers";
	
	private ServerSocket fServerSocket;
	private ThreadGroup fHandlers;
	private Vector fCommandQueue;	
	private boolean fIsRunning;
	private String fPassword;
	
/**
 * TelnetServer constructor comment.
 */
public TelnetServer(int port, String password) throws IOException 
	{
	super("Telnet Server");

	PrintManager.addPrintListener(this);
	
	try
		{
		// setup the socket stuff
		fServerSocket = new ServerSocket(port);
		fServerSocket.setSoTimeout(SOCKET_TIMEOUT);  
		
		// as long as nothing bad happened 
		// (no exception was thrown from the socket setup)
		// setup other stuff
		SecurityManager mgr = System.getSecurityManager();
		if (mgr == null)
			fHandlers = new ThreadGroup(GROUP_NAME);
		else
			fHandlers = new ThreadGroup(mgr.getThreadGroup(), GROUP_NAME);
			
		fCommandQueue = new Vector();
		fPassword = password;
		}
	catch (IOException e)
		{
		fServerSocket = null;
		e.printStackTrace();
		}			
	}
/**
 * This method was created by a SmartGuide.
 * @param flags int
 * @param msg java.lang.String
 */
public void bprint(int flags, String msg) 
	{
	output(msg);
	}
/**
 * This method was created by a SmartGuide.
 * @param b byte[]
 * @param offset int
 * @param len int
 */
public void consoleOutput(byte[] b, int offset, int len) 
	{
	int nClients = fHandlers.activeCount();
	// check if there are any clients connected
	if (nClients < 1)
		return;
			
	TelnetHandler[] handlers = new TelnetHandler[nClients];			
	fHandlers.enumerate(handlers);	
	
	// send the byte array to each client
	for (int i = 0; i < handlers.length; i++)
		{
		try	
			{	
			handlers[i].output(b, offset, len);
			}
		catch (IOException e)
			{
			}
		}	
	}
/**
 * This method was created by a SmartGuide.
 * @param msg java.lang.String
 */
public void dprint(String msg) 
	{
	output(msg);
	}	
/**
 * Get a String from the command queue.
 * @return The next string, or null if the queue is empty.
 */
public String getCommand() 
	{
	if ((fCommandQueue == null) || (fCommandQueue.size() < 1))
		return null;
	else
		{
		String result = (String) fCommandQueue.elementAt(0);
		fCommandQueue.removeElementAt(0);
		return result;
		}
	}
/**
 * Report whether the Telnet Server is still running.
 * @return boolean
 */
public boolean isRunning() 
	{
	return fIsRunning;
	}
/**
 * Send a string to all the connected clients.
 * @param s java.lang.String
 */
private void output(String s) 
	{
	int nClients = fHandlers.activeCount();
	// check if there are any clients connected
	if (nClients < 1)
		return;

	// convert the string to a byte array, and 
	// expand linefeeds to carriage-return/linefeed pairs.
	int strSize = s.length();
	int nChars = strSize;
	for (int i = 0; i < strSize; i++)
		if (s.charAt(i) == '\n')
			nChars++;
			
	byte[] b = new byte[nChars];
	nChars = 0;
	for (int i = 0; i < strSize; i++)
		{
		char ch = s.charAt(i);
		if (ch == '\n')
			b[nChars++] = '\r';
		b[nChars++] = (byte)(ch & 0x00ff);
		}					

	consoleOutput(b, 0, nChars);	
	}
/**
 * This method was created by a SmartGuide.
 * @param s java.lang.String
 */
void pushCommand(String s) 
	{
	if (fCommandQueue != null)
		fCommandQueue.addElement(s);
	}
/**
 * This method was created by a SmartGuide.
 */
public void run()
	{
	if (fServerSocket == null)
		return;

	ConsoleOutputStream.addConsoleListener(this);
			
	try
		{
		fIsRunning = true;	
		while (fIsRunning)
			{			
			try
				{
				Socket s = fServerSocket.accept();			
				if (s != null)
					{
					TelnetHandler t = new TelnetHandler(fHandlers, this, s, fPassword);
					t.start();
					}
				}	
			catch (IOException e)
				{
				}			
			}
		
		fServerSocket.close();		
		}
	catch (IOException e)
		{
		e.printStackTrace();
		}	

		
	ConsoleOutputStream.removeConsoleListener(this);					
	}
/**
 * This method was created by a SmartGuide.
 */
public void shutdown() 
	{
	PrintManager.removePrintListener(this);

	fIsRunning = false;	
	}
}