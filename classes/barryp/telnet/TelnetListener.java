package barryp.telnet;

import java.io.*;
import java.net.*;
import java.util.*;

import q2java.*;
import q2java.core.CrossLevel;
import q2java.core.Game;
import q2java.core.ResourceGroup;
import q2java.core.event.*;

/**
 * Thread class that accepts Telnet-type connections,
 * spawns separate threads to handle them, and coordinates
 * communications with the Game.
 * 
 * @author Barry Pederson
 */
class TelnetListener extends Thread implements PrintListener, CrossLevel
	{
	private final static int SOCKET_TIMEOUT = 500; // milliseconds
	private final static String GROUP_NAME = "Telnet Handlers";
	private final static byte[] COLON = {(byte)':', (byte)' '}; // convenient for player chats
	private final static byte[] CRLF = {(byte)'\r', (byte)'\n'};
	private final static int PRINT_CHANNELS = PrintEvent.PRINT_JAVA + PrintEvent.PRINT_SERVER_CONSOLE + PrintEvent.PRINT_ANNOUNCE + PrintEvent.PRINT_TALK;
	
	private int fPort;
	private ServerSocket fServerSocket;
	private ThreadGroup fHandlers;
	private boolean fIsRunning;
	private String fPassword;
	private boolean fNoCmd;
	private boolean fNoChat;
	protected String fLocaleName;
	protected ResourceGroup fResourceGroup;
	protected TelnetServer fGameModule;
	
/**
 * TelnetServer constructor comment.
 */
public TelnetListener(TelnetServer gm, int port, String password, boolean noCmd, boolean noChat) throws IOException 
	{
	super("Telnet Server");

	// remember our GameModule
	fGameModule = gm;

	try
		{
		// setup the socket stuff
		fPort = port;
		fServerSocket = new ServerSocket(port);
		fServerSocket.setSoTimeout(SOCKET_TIMEOUT);  
		
		// as long as nothing bad happened 
		// (no exception was thrown from the socket setup)
		// setup other stuff
		SecurityManager mgr = System.getSecurityManager();
		if (mgr == null)
			fHandlers = new ThreadGroup(GROUP_NAME + " on " + fPort);
		else
			fHandlers = new ThreadGroup(mgr.getThreadGroup(), GROUP_NAME + " on " + fPort);
			
		fPassword = password;
		fNoCmd = noCmd;
		fNoChat = noChat;
		
		// call us when certain stuff is being printed
		fResourceGroup = Game.getResourceGroup(Locale.getDefault());		
		Game.getPrintSupport().addPrintListener(this, PRINT_CHANNELS, false);
		}
	catch (IOException e)
		{
		fServerSocket = null;
		e.printStackTrace();
		}			
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getConnectionCount() 
	{
	return fHandlers.activeCount();
	}
/**
 * Get a reference to the telnet server game module.
 * @return barryp.telnet.GameModule
 */
public TelnetServer getGameModule() 
	{
	return fGameModule;
	}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getLocale() 
	{
	return fLocaleName;
	}
/**
 * Fetch the port this TelnetServer is listening to.
 * @return int
 */
public int getPort() 
	{
	return fPort;
	}
/**
 * This method was created by a SmartGuide.
 * @return java.util.Locale
 */
public ResourceGroup getResourceGroup() 
	{
	return fResourceGroup;
	}
/**
 * get an array of telnet handlers to current sessions.
 * @returns TelnetHandler[] - array of current sessions.
 */
protected TelnetHandler[] getTelnetHandlers()
	{
	int nClients = fHandlers.activeCount();
	// check if there are any clients connected
	if (nClients < 1)
		return null;

	TelnetHandler[] handlers = new TelnetHandler[nClients];			
	fHandlers.enumerate(handlers);	

	return handlers;
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
 * Called when a PrintEvent is fired.
 * @param pe q2java.core.event.PrintEvent
 */
public void print(PrintEvent pe)
	{
	int nClients = fHandlers.activeCount();
	// check if there are any clients connected
	if (nClients < 1)
		return;

	byte[] b;

	// prefix player chats with their name
	String sourceName = pe.getSourceName();
	if ((pe.getPrintChannel() == PrintEvent.PRINT_TALK) && (sourceName != null))
		{
		b = TelnetHandler.getBytes(sourceName);		
		telnetOutput(b, 0, b.length);
		telnetOutput(COLON, 0, COLON.length);
		}
		
	// use the TelnetHandler's common conversion utility.
	b = TelnetHandler.getBytes(pe.getMessage());
	telnetOutput(b, 0, b.length);

	// force a new line
	if (b[b.length-1] != '\n')
		telnetOutput(CRLF, 0, CRLF.length);
	}
/**
 * This method was created by a SmartGuide.
 */
public void run()
	{
	if (fServerSocket == null)
		return;
	
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
					TelnetHandler t = new TelnetHandler(fHandlers, this, s, fPassword, fNoCmd, fNoChat);
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
		

	Game.getPrintSupport().removePrintListener(this);
	}
/**
 * Change the server's locale.
 * @param localeName java.lang.String
 */
public void setLocale(String localeName) 
	{
	fLocaleName = localeName;
	fResourceGroup = Game.getResourceGroup(localeName);
	PrintSupport ps = Game.getPrintSupport();
	ps.removePrintListener(this);
	ps.addPrintListener(this, PRINT_CHANNELS, fResourceGroup.getLocale(), false);
	}
/**
 * Called when it's time to shut the server down.
 */
public void stopServer() 
	{
	fIsRunning = false;		
	TelnetServer.removeServer(this);
	}
/**
 * Send data to the connected telnet clients.
 * @param b byte[]
 * @param offset int
 * @param len int
 */
public void telnetOutput(byte[] b, int offset, int len) 
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
}