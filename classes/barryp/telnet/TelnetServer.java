package barryp.telnet;

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
class TelnetServer extends Thread  implements PrintListener, LocaleListener, FrameListener, CrossLevel
	{
	private final static int SOCKET_TIMEOUT = 500; // milliseconds
	private final static String GROUP_NAME = "Telnet Handlers";
	
	private int fPort;
	private ServerSocket fServerSocket;
	private ThreadGroup fHandlers;
	private Vector fCommandQueue;	
	private boolean fIsRunning;
	private String fPassword;
	private boolean fNoCmd;
	private boolean fNoChat;
	protected ResourceGroup fResourceGroup;
	protected GameModule fGameModule;
	
/**
 * TelnetServer constructor comment.
 */
public TelnetServer(GameModule gm, int port, String password, boolean noCmd, boolean noChat) throws IOException 
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
			
		fCommandQueue = new Vector();
		fPassword = password;
		fNoCmd = noCmd;
		fNoChat = noChat;
		
		// call us when stuff is being printed
		Game.addPrintListener(this);	
	
		// call us with localized messages using the default locale
		fResourceGroup = Game.addLocaleListener(this);
	
		// call us so we can pass chats and commands back to the game
		Game.addFrameListener(this, Game.FRAME_BEGINNING, 0, 0);		
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
 * Output text sent to the console from outside the game.
 * @param flags int
 * @param msg java.lang.String
 */
public void consoleOutput(String msg) 
	{
	output(msg);
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
public GameModule getGameModule() 
	{
	return fGameModule;
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
 * Called when the listener receives a localized broadcast message.
 * @param printLevel One of the Engine.PRINT_* constants
 * @param msg java.lang.String
 */
public void localecast(int printLevel, String msg)	
	{
	output(msg);
	}
/**
 * Called when the listener receives a localized broadcast message.
 * @param printLevel One of the Engine.PRINT_* constants
 * @param msg java.lang.String
 */
public void localecast(Locale loc, int printLevel, String msg)	
	{
	output(msg);
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

	// use the TelnetHandler's common conversion utility.
	byte[] b = TelnetHandler.getBytes(s);

	consoleOutput(b, 0, b.length);	
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
		

	Game.removeLocaleListener(this);
	Game.removePrintListener(this);
	Game.removeFrameListener(this, Game.FRAME_BEGINNING);	
	}
/**
 * Relay input from the telnet clients.
 */
public void runFrame(int phase) 
	{
	String s;
	while ((s = getCommand()) != null)
		{
		if ((s.length() > 0) && (s.charAt(0) == '/'))
			Engine.addCommandString(s.substring(1) + "\n");
		else
			Game.bprint(Engine.PRINT_CHAT, s + "\n");
		}
	}
/**
 * Change the server's locale.
 * @param localeName java.lang.String
 */
public void setLocale(String localeName) 
	{
	// remove ourselves from the previous locale
	if (fResourceGroup != null)
		fResourceGroup.removeLocaleListener(this);
		
	fResourceGroup = Game.addLocaleListener(this, localeName);
	}
/**
 * Called when it's time to shut the server down.
 */
public void stopServer() 
	{
	fIsRunning = false;		
	GameModule.removeServer(this);
	}
}