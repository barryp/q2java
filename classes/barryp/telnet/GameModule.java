package barryp.telnet;

import java.io.*;
import java.text.*;
import java.util.*;

import q2java.*;
import q2java.core.*;

/**
 * Class to hold the init method, and any server commands implemented
 * by this module.
 * 
 * @author Barry Pederson
 */
public class GameModule extends q2java.core.Gamelet
	{
	private static Vector gServers;
	private static boolean gIsLogging;
	private static String gLogName;
	private static SimpleDateFormat gTimestampFormat;
	
/**
 * Initialize a Telnet server module.
 */
public GameModule(String moduleName) 
	{
	super(moduleName);
	
	gServers = new Vector();

	File sandbox = new File(Engine.getGamePath(), "sandbox");
	File logfile = new File(sandbox, "telnet.log");
	gLogName = logfile.getPath();		

	gTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
	gTimestampFormat.setTimeZone(TimeZone.getDefault());

	// check if we specified any telnet cvars on the command line
	int port = (int) ((new CVar("telnet_port", "0", CVar.CVAR_NOSET)).getFloat());
	if (port > 0)
		{
		String password = (new CVar("telnet_password", "", CVar.CVAR_NOSET)).getString();
		try
			{
			TelnetServer telnet = new TelnetServer(this, port, password, false, false);
			addServer(telnet);
			telnet.start();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}		
		}	
	}
/**
 * Add a line of text to the telnet log.
 * @param s java.lang.String
 */
static synchronized void addLog(String s) 
	{
	if (!gIsLogging)
		return;
		
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
/**
 * This method was created by a SmartGuide.
 * @param s barryp.telnet.TelnetServer
 */
static void addServer(TelnetServer s) 
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
static void removeServer(TelnetServer t) 
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
		TelnetServer t = (TelnetServer) gServers.elementAt(i);
		Game.dprint("       port: " + t.getPort() + " connections: " + t.getConnectionCount() + "\n");
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
		TelnetServer t = (TelnetServer) gServers.elementAt(i);
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
		{
		if (args[2].equalsIgnoreCase("on"))
			gIsLogging = true;		
		else if (args[2].equalsIgnoreCase("off"))
			gIsLogging = false;
		else
			Game.dprint("Usage: sv log [on | off]\n");
		}

	Game.dprint("Logging is " + (gIsLogging ? "on" : "off") + "\n");
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
		TelnetServer t = new TelnetServer(this, port, password, noCmd, noChat);
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
		TelnetServer t = (TelnetServer) gServers.elementAt(i);
		if (t.getPort() == port)
			{
			t.stopServer();
			return;
			}
		}
	}
/**
 * Shutdown all the running TelnetServer objects.
 */
public void unload() 
	{
	Vector v = (Vector) gServers.clone();
	
	Enumeration enum = v.elements();
	while (enum.hasMoreElements())
		{
		TelnetServer t = (TelnetServer) enum.nextElement();
		t.stopServer();
		}		
	}
}