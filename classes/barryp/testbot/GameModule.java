package barryp.testbot;

import org.w3c.dom.Document;

import q2java.*;
import q2java.core.*;

/**
 * Test simple bot support.
 * 
 */
public class GameModule extends q2java.core.Gamelet 
	{
	protected TestBot fTestBot;
	
/**
 * Test the bot-enabling features
 */
public GameModule(Document gameletInfo) 
	{
	super(gameletInfo);
	
	try
		{
		NativeEntity botEnt = new NativeEntity(NativeEntity.ENTITY_PLAYER);
		fTestBot = new TestBot(botEnt, Game.getGameletManager().getGameletName(this));
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
	StringBuffer sb = new StringBuffer(args[2]);
	for (int i = 3; i < args.length; i++)
		{
		sb.append(' ');
		sb.append(args[i]);
		}
		
	fTestBot.playerCommand(sb.toString());
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
 * Set the bot's locale
 */
public void svcmd_locale(String[] args)
	{
	if (args.length > 2)
		fTestBot.setLocale(args[2]);
	else
		{
		Game.dprint("Usage: sv locale [<locale>]\nCurrent locale: " + fTestBot.getLocale() + "\n");
		}
	}
/**
 * Force the bot to respawn now.
 * @param args java.lang.String[]
 */
public void svcmd_respawn(String[] args)
	{
	fTestBot.doRespawn();
	}
/**
 * Set the bot's skin.
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
 * Clean up the module by removing the bot.
 */
public void unload()
	{
	fTestBot.playerDisconnect();
	}
}