
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
class TelnetServer extends Thread  implements PrintListener, FrameListener, GameStatusListener, CrossLevel
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
	
/**
 * TelnetServer constructor comment.
 */
public TelnetServer(int port, String password, boolean noCmd, boolean noChat) throws IOException 
	{
	super("Telnet Server");
	
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
 * Fetch the port this TelnetServer is listening to.
 * @return int
 */
public int getPort() 
	{
	return fPort;
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
 * N/A to this mod.
 * @param filename java.lang.String
 */
public void readGame(String filename) 
	{
	}
/**
 * N/A to this mod.
 * @param filename java.lang.String
 */
public void readLevel(String filename) 
	{
	}
/**
 * This method was created by a SmartGuide.
 */
public void run()
	{
	if (fServerSocket == null)
		return;

	// call us when stuff is being printed
	Game.addPrintListener(this);	
	// call us so we can pass chats and commands back to the game
	Game.addFrameListener(this, Game.FRAME_BEGINNING, 0, 0);	
	// call us so we know when the game is shutting down
	Game.addGameStatusListener(this);
	
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
		

	Game.removePrintListener(this);
	Game.removeFrameListener(this, Game.FRAME_BEGINNING);	
	Game.removeGameStatusListener(this);
	}
/**
 * Relay input from the telnet clients.
 */
public void runFrame(int phase) 
	{
	String s;
	while ((s = getCommand()) != null)
		{
		if ((s.length() > 0) && (s.charAt(0) == '+'))
			Engine.addCommandString(s.substring(1));
		else
			Game.bprint(Engine.PRINT_CHAT, s + "\n");
		}
	}
/**
 * Called when it's time to shut the server down.
 */
public void shutdown() 
	{	
	fIsRunning = false;		
	GameModule.removeServer(this);
	}
/**
 * N/A to this mod.
 * @param filename java.lang.String
 */
public void writeGame(String filename) 
	{
	}
/**
 * N/A to this mod.
 * @param filename java.lang.String
 */
public void writeLevel(String filename) 
	{
	}
}