
package barryp.telnet;

import java.io.*;
import java.util.*;

import q2java.*;
import q2jgame.*;

/**
 * Class to hold the init method, and any server commands implemented
 * by this module.
 * 
 */
public class GameModule 
	{
	static Vector gServers;
	
/**
 * This method was created by a SmartGuide.
 * @param s barryp.telnet.TelnetServer
 */
static void addServer(TelnetServer s) 
	{
	gServers.addElement(s);
	}
/**
 * Display help info to the console.
 */
public static void help() 
	{
	Game.dprint("Q2Java Telnet Server\n\n");
	Game.dprint("    commands:\n");
	Game.dprint("       sv telnet_start <port> [-pass <password>] [-nocmd] [-nochat]\n");
	Game.dprint("       sv telnet_stop <port>\n");
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
 * This method was created by a SmartGuide.
 */
public static void load() 
	{
	gServers = new Vector();
	
	// check if we specified any telnet cvars on the command line
	int port = (int) ((new CVar("telnet_port", "0", CVar.CVAR_NOSET)).getFloat());
	if (port > 0)
		{
		String password = (new CVar("telnet_password", "", CVar.CVAR_NOSET)).getString();
		try
			{
			TelnetServer telnet = new TelnetServer(port, password, false, false);
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
 * This method was created by a SmartGuide.
 * @param t barryp.telnet.TelnetServer
 */
static void removeServer(TelnetServer t) 
	{
	gServers.removeElement(t);
	}
/**
 * Shutdown all the running TelnetServer objects.
 */
public static void unload() 
	{
	Vector v = (Vector) gServers.clone();
	
	Enumeration enum = v.elements();
	while (enum.hasMoreElements())
		{
		TelnetServer t = (TelnetServer) enum.nextElement();
		t.shutdown();
		}		
	}
}