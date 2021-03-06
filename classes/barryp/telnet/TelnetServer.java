package barryp.telnet;

import java.io.*;
import java.text.*;
import java.util.*;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;

/**
 * Class to hold the init method, and any server commands implemented
 * by this module.
 * 
 * @author Barry Pederson
 */
public class TelnetServer extends Gamelet
	{
	private static String gLogName;
	private static Vector gServers = new Vector();
	private static SimpleDateFormat gTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
	
/**
 * Initialize a Telnet server module.
 */
public TelnetServer(Document gameletInfo) 
	{
	super(gameletInfo);
	
	gTimestampFormat.setTimeZone(TimeZone.getDefault());

	File sandbox = new File(Engine.getGamePath(), "sandbox");
	File logfile = new File(sandbox, "telnet.log");
	gLogName = logfile.getPath();		


	// look for subtags in the form:
	//    <server port="xxx" [password="yyy"] [chat="n"] [commands="n"] />
	// or
	//    <log file="aaaa"/>
	//
	for (Node n = gameletInfo.getDocumentElement().getFirstChild(); n != null; n = n.getNextSibling())	
		{
		if (n.getNodeType() != Node.ELEMENT_NODE)
			continue;

		Element e = (Element) n;
		String tagName = e.getTagName();

		if (tagName.equals("log"))
			{
			gLogName = e.getAttribute("file");
			continue;
			}
			
		if (!tagName.equals("server"))
			continue;

		int port;
		try
			{
			port = Integer.parseInt(e.getAttribute("port"));
			}
		catch (NumberFormatException nfe)
			{
			port = 0;
			}

		boolean noChat = "n".equalsIgnoreCase(e.getAttribute("chat"));
		boolean noCommands = "n".equalsIgnoreCase(e.getAttribute("commands"));
			
		try
			{
			TelnetListener telnet = new TelnetListener(this, port, e.getAttribute("password"), noCommands, noChat);
			addServer(telnet);
			telnet.start();

			String locale = e.getAttribute("locale");
			if (locale != null)
				telnet.setLocale(locale);
			}
		catch (IOException ioe)
			{
			ioe.printStackTrace();
			}				
		}		
	}
/**
 * Add a line of text to the telnet log.
 * @param s java.lang.String
 */
static synchronized void addLog(String s) 
	{
	if (gLogName != null)
		{		
		try
			{
			FileWriter fw = new FileWriter(gLogName, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(gTimestampFormat.format(new Date()) + " " + s);
			pw.close();
			}
		catch (IOException e)
			{
			}
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param s barryp.telnet.TelnetServer
 */
static void addServer(TelnetListener s) 
	{
	gServers.addElement(s);
	}


	
/**
 * get an enumeration of the current servers.
 * @return Enumeration - enumeration of servers.
 */
public Enumeration getServers()
	{
	return gServers.elements();
	}
/**
 * This method was created by a SmartGuide.
 * @param t barryp.telnet.TelnetServer
 */
static void removeServer(TelnetListener t) 
	{
	gServers.removeElement(t);
	}
/**
 * Display help info to the console.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("Q2Java Telnet Server\n\n");
	Game.dprint("    sv commands:\n");
	Game.dprint("       start <port> [-pass <password>] [-nocmd] [-nochat]\n");
	Game.dprint("       stop <port>\n");
	Game.dprint("       locale <port> <locale-name>\n");
	Game.dprint("\n");
	Game.dprint("    active servers:\n");
	
	if (gServers.size() < 1)
		Game.dprint("       (none)\n");
		
	for (int i = 0; i < gServers.size(); i++)
		{
		TelnetListener t = (TelnetListener) gServers.elementAt(i);
		Game.dprint("       port: " + t.getPort() + " locale: " + t.getLocale() + " connections: " + t.getConnectionCount() + "\n");
		}
	}
/**
 * Run the "sv telnet.locale" command.
 * @param args java.lang.String[]
 */
public void svcmd_locale(String[] args) 
	{
	if (args.length < 4)
		{
		Game.dprint("Usage: locale <port> <new-locale>\n");
		return;
		}
		
	int port = Integer.parseInt(args[2]);
	
	for (int i = 0; i < gServers.size(); i++)
		{
		TelnetListener t = (TelnetListener) gServers.elementAt(i);
		if (t.getPort() == port)
			{
			t.setLocale(args[3]);
			return;
			}
		}
	}
/**
 * Control the Logging option.
 */
public void svcmd_log(String[] args) 
	{
	if (args.length > 2)
		gLogName = args[2];
	else
		gLogName = null;

	Game.dprint("Logging " + (gLogName == null ? "is off" : "to " + gLogName) + "\n");
	}
/**
 * Run the "sv start" command.
 * @param args java.lang.String[]
 */
public void svcmd_start(String[] args) 
	{
	if (args.length < 3)
		{
		Game.dprint("Usage: start <port> [-pass <password>] [-nocmd] [-nochat]\n");
		return;
		}
		
	int port = Integer.parseInt(args[2]);
	String password = null;
	boolean noCmd = false;
	boolean noChat = false;
	
	for (int i = 3; i < args.length; i++)
		{
		if (args[i].equalsIgnoreCase("-pass"))
			{
			password = args[++i];
			continue;
			}
			
		if (args[i].equalsIgnoreCase("-nocmd"))
			{
			noCmd = true;
			continue;
			}
			
		if (args[i].equalsIgnoreCase("-nochat"))
			{
			noChat = true;
			continue;
			}
		}
			
	try
		{	
		TelnetListener t = new TelnetListener(this, port, password, noCmd, noChat);
		t.start();
		addServer(t);
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
/**
 * Run the "sv telnet.stop" command.
 * @param args java.lang.String[]
 */
public void svcmd_stop(String[] args) 
	{
	if (args.length < 3)
		{
		Game.dprint("Usage: stop <port>\n");
		return;
		}
		
	int port = Integer.parseInt(args[2]);
	
	for (int i = 0; i < gServers.size(); i++)
		{
		TelnetListener t = (TelnetListener) gServers.elementAt(i);
		if (t.getPort() == port)
			{
			t.stopServer();
			return;
			}
		}
	}
/**
 * Shutdown all the running TelnetListener objects.
 */
public void unload() 
	{
	Vector v = (Vector) gServers.clone();
	
	Enumeration enum = v.elements();
	while (enum.hasMoreElements())
		{
		TelnetListener t = (TelnetListener) enum.nextElement();
		t.stopServer();
		}		
	}
}