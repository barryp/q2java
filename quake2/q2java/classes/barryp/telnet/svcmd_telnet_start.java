
package barryp.telnet;

import q2jgame.*;

/**
 * Let the user start a Telnet Server from the console.
 * 
 */
public class svcmd_telnet_start 
	{
	
/**
 * Run the "sv telnet_start" command.
 * @param args java.lang.String[]
 */
public static void run(String[] args) 
	{
	if (args.length < 3)
		{
		Game.dprint("Usage: sv telnet_start <port> [-pass <password>] [-nocmd] [-nochat]\n");
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
		TelnetServer t = new TelnetServer(port, password, noCmd, noChat);
		t.start();
		GameModule.addServer(t);
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
}