
package barryp.testbot;

import q2java.*;
import q2jgame.*;

/**
 * Test simple bot support.
 * 
 */
public class GameModule extends q2jgame.GameModule 
	{
	protected TestBot fTestBot;
	
/**
 * Test the bot-enabling features
 */
public GameModule(String moduleName) 
	{
	super(moduleName);
	try
		{
		NativeEntity botEnt = new NativeEntity(NativeEntity.ENTITY_PLAYER);
		fTestBot = new TestBot(botEnt, moduleName);
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param args java.lang.String[]
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("Bot health: " + fTestBot.getHealth() + "\n");
	}
/**
 * This method was created by a SmartGuide.
 * @param args java.lang.String[]
 */
public void svcmd_respawn(String[] args) 
	{
	fTestBot.doRespawn();
	}
/**
 * This method was created by a SmartGuide.
 * @param args java.lang.String[]
 */
public void svcmd_skin(String[] args) 
	{
	if (args.length > 2)
		fTestBot.setSkin(args[2]);
	else
		Game.dprint("Usage: sv skin <skin-name> (example: male/grunt)\n");
	}
/**
 * This method was created by a SmartGuide.
 */
public void unload() 
	{
	fTestBot.playerDisconnect();	
	}
}