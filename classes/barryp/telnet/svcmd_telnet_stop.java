
package barryp.telnet;

import q2jgame.*;

/**
 * Let the user start a Telnet Server from the console.
 * 
 */
public class svcmd_telnet_stop 
	{
	
/**
 * Run the "sv telnet_start" command.
 * @param args java.lang.String[]
 */
public static void run(String[] args) 
	{
	if (args.length < 3)
		{
		Game.dprint("Usage: sv telnet_stop <port>\n");
		return;
		}
		
	int port = Integer.parseInt(args[2]);
	
	for (int i = 0; i < GameModule.gServers.size(); i++)
		{
		TelnetServer t = (TelnetServer) GameModule.gServers.elementAt(i);
		if (t.getPort() == port)
			{
			t.shutdown();
			return;
			}
		}
	}
}