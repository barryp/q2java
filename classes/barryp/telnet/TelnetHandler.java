
package barryp.telnet;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.Date;
import q2jgame.*;

/**
 * Handle communication with an individual Telnet client.
 * 
 * @author Barry Pederson
 */
class TelnetHandler extends Thread
	{
	private final static int CLIENT_TIMEOUT = 500; // how often to unblock from reading from the client to check if we're quitting.
	private final static int PASSWORD_TIMEOUT = 30000; 
	private final static int LINEBUFFER_SIZE = 128;
	
	private TelnetServer fServer;
	private Socket fSocket;
	private OutputStream fOS;
	private InputStream fIS;
	
	private String fPassword;
	private boolean fNoCmd;
	private boolean fNoChat;
	
	private String fNickname;
	
	private boolean fSeenCR;
//	private ByteArrayOutputStream fLineBuffer;
	
	private byte[] fLineBuffer;
	private int fLineBufferPtr;
	
/**
 * TelnetHandler constructor comment.
 * @param group java.lang.ThreadGroup
 * @param name java.lang.String
 */
TelnetHandler(ThreadGroup grp, TelnetServer srv, Socket s, String password, boolean noCmd, boolean noChat) 
	{
	super(grp, "TelnetHandler to: " + s.getInetAddress() + " on port: " + s.getLocalPort());
	fServer = srv;
	fSocket = s;
	fPassword = password;
	fNoCmd = noCmd;
	fNoChat = noChat;
	fLineBuffer = new byte[LINEBUFFER_SIZE];
	fLineBufferPtr = 0;
	}
/**
 * Make the client type the right password, then welcome them.
 */
private void logon() throws IOException
	{
	// keep the output stream hidden til after the client is logged on.
	OutputStream os = fSocket.getOutputStream();

	ResourceGroup rg = fServer.getResourceGroup();	
	if ((fPassword != null) && (fPassword.length() > 0))
		{
		String prompt = rg.getRandomString("barryp.telnet.Messages", "passprompt") + " ";
		os.write(prompt.getBytes());
		String pass = readLine(PASSWORD_TIMEOUT);
		if (!fPassword.equals(pass))
			{
			String response = rg.getRandomString("barryp.telnet.Messages", "badpass") + "\r\n";
			os.write(response.getBytes());

			fIS.close();
			fIS = null;
			os.close();
			fSocket.close();
			fSocket = null;
			return;
			}
		}

	if (!fNoChat)
		{
		String prompt = "\r\n" + rg.getRandomString("barryp.telnet.Messages", "nickprompt") + " ";
		os.write(prompt.getBytes());
		fNickname = readLine(0);		
		}
		
	String welcome = rg.getRandomString("barryp.telnet.Messages", "welcome") + "\r\n";
	os.write(welcome.getBytes());		

	try
		{
		Date d = new Date();
		// we -should- be able to put the date formatting right in the MessageFormat template, but
		// that feature seems to be botched.
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG, rg.getLocale());
		welcome = rg.getRandomString("barryp.telnet.Messages", "clockprefix") + " " + df.format(d) + "\r\n\r\n";
		os.write(welcome.getBytes());		
		}
	catch (ExceptionInInitializerError eiie)
		{
		eiie.getException().printStackTrace();
		}
					
	if (!fNoCmd)
		{
		welcome = "    " + rg.getRandomString("barryp.telnet.Messages", "cmdhint") + "\r\n";	
		os.write(welcome.getBytes());		
		}

	// ok, now the game can send messages to the client
	fOS = os;		
	}
/**
 * Send an array of bytes to the client.
 * @param b array of bytes
 */
void output(byte[] b, int off, int len) throws IOException
	{
	if (fOS != null)
		fOS.write(b, off, len);
	}
/**
 * Read one line of input from the Telnet client.
 *
 * One improvement that could be made would be to handle
 * backspace characters properly.
 *
 * @param timeoutMillis give up waiting for line after these many milliseconds.
 *  use zero if you don't want to timeout.
 * @return The line read, without any CR or LF characters, null
 *  we're quitting, timed out, disconnected, or other exception.
 */
private String readLine(long timeoutMillis) 
	{	
	long deadline;
	
	if (timeoutMillis <= 0)
		deadline = 0;
	else
		deadline = System.currentTimeMillis() + timeoutMillis;
		
	while (true)
		{
		int ch = 0;
		
		try
			{
			ch = fIS.read();
			}
		catch (InterruptedIOException iie)
			{
			if (!fServer.isRunning())
				return null;
				
			if ((deadline > 0) && (System.currentTimeMillis() > deadline))
				return null;
				
			continue;
			}			
		catch (IOException e)
			{
			return null;  // not sure what other kind of exceptions there might be.
			}			

		if (ch < 0)
			return null; // connection closed

		// ignore linefeeds that come
		// right after carriage returns					
		if (fSeenCR && (ch == '\n'))
			continue;

		// remember if the last char was a carriage-return					
		fSeenCR = (ch == '\r');						

		switch (ch)
			{
			case '\r':
			case '\n':						
				String result = new String(fLineBuffer, 0, fLineBufferPtr);
				fLineBufferPtr = 0;
				return result;	
				
			case '\b'	:			
				if (fLineBufferPtr > 0)
					fLineBufferPtr--;
				break;
				
			default:
				if (fLineBufferPtr < LINEBUFFER_SIZE)
					fLineBuffer[fLineBufferPtr++] = (byte)(ch & 0x00ff);
				break;								
			}
		}
	}
/**
 * Talk with the client.
 */
public void run() 
	{
	try
		{
		fSocket.setSoTimeout(CLIENT_TIMEOUT);
		fIS = fSocket.getInputStream();
		
		logon();
		if (fSocket == null)
			return; // logon must have failed

		fServer.pushCommand("<Telnet>: " + fNickname + " connected");
		
		String clientAddr = fSocket.getInetAddress().getHostAddress() + ":" + fSocket.getLocalPort();
		barryp.telnet.GameModule.addLog(clientAddr + " " + fNickname + " connected");			
		
		while (fServer.isRunning())
			{
			String s = readLine(0);
			if (s == null)
				break; // client disconnected

			if (s.length() < 1)
				continue;
				
			if (s.charAt(0) == '+')
				{
				if (fNoCmd)
					continue;
				fServer.pushCommand(s);
				}
			else
				{
				if (fNoChat)
					continue;
				fServer.pushCommand("<Telnet-" + fNickname + ">: " + s);
				}
			}

		fServer.pushCommand("<Telnet>: " + fNickname + " disconnected");
		barryp.telnet.GameModule.addLog(clientAddr + " " + fNickname + " disconnected");			
			
		fOS.close();
		fIS.close();							
		fSocket.close();		
		}
	catch (IOException e)
		{
//		e.printStackTrace();
		}						
	}
}