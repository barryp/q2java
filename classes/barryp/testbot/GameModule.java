
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
 * Have the bot execute a player command.
 * @param args java.lang.String[]
 */
public void svcmd_cmd(String[] args) 
	{
	String[] sa = new String[args.length - 2];
	for (int i = 0; i < sa.length; i++)
		sa[i] = args[i+2];

	Class[] paramTypes = new Class[1];
	paramTypes[0] = sa.getClass();
	
	try
		{
		java.lang.reflect.Method meth = fTestBot.getClass().getMethod("cmd_" + sa[0].toLowerCase(), paramTypes);						
		Object[] params = new Object[1];
		params[0] = sa;
		meth.invoke(fTestBot, params);
		}
	catch (java.lang.reflect.InvocationTargetException e2)		
		{
		e2.getTargetException().printStackTrace();
		}
	catch (Exception e3)
		{
		e3.printStackTrace();
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