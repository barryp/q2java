package menno.ctf;


/*
======================================================================================
==                                 Q2JAVA CTF                                       ==
==                                                                                  ==
==                   Author: Menno van Gangelen <menno@element.nl>                  ==
==                                                                                  ==
==            Based on q2java by: Barry Pederson <bpederson@geocities.com>          ==
==                                                                                  ==
== All sources are free for non-commercial use, as long as the licence agreement of ==
== ID software's quake2 is not violated and the names of the authors of q2java and  ==
== q2java-ctf are included.                                                         ==
======================================================================================
*/

import q2java.*;
import q2jgame.*;
import baseq2.InventoryList;
import menno.ctf.spawn.*;

/**
 * Q2Java CTF module.
 * 
 * @author Menno van Gangelen
 */

public class GameModule extends q2jgame.GameModule implements LevelListener
{

	public GameModule(String moduleName)
	{
		super( moduleName );
		
		// ask to be called on level changes
		Game.addLevelListener(this);

		// update player inventory lists to support techs
		InventoryList.addItem("Disruptor Shield");
		InventoryList.addItem("AutoDoc");
		InventoryList.addItem("Time Accel");
		InventoryList.addItem("Power Amplifier");
		
		// turn the players into CTF players.
		java.util.Enumeration players = NativeEntity.enumeratePlayers();
		while (players.hasMoreElements())
		{
			try
			{
				NativeEntity ent = (NativeEntity) players.nextElement();
				
				// get rid of the old player object
				baseq2.Player bp = (baseq2.Player) ent.getReference();
				bp.dispose();

				// create a new CTF player object
				menno.ctf.Player p = new menno.ctf.Player(ent, false);
//				p.playerBegin(false);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * Called when a new map is starting, after entities have been spawned.
	 */
	public void levelEntitiesSpawned() 
	{
		// now it's time to spawn the techs.
		try 
		{
			new item_tech1();
			new item_tech2();
			new item_tech3();
			new item_tech4();
		}
		catch ( Exception e )
		{
			// do nothing here.
			System.out.println( "error in spwaning techs... " + e );
		}	
	}
	/**
	 * Start a new level.
	 */
	public void startLevel(String mapname, String entString, String spawnPoint)
	{
		// overrule the statbar
		Engine.setConfigString (Engine.CS_STATUSBAR, Player.CTF_STATUSBAR);	

	}
	/**
	 * This method was created by a SmartGuide.
	 */
	public void svcmd_help(String[] args) 
	{
		Game.dprint("A Capture The Flag conversion,\n");
		Game.dprint("the popular Q2 mod from Zoid.\n");
		Game.dprint("   no commands available\n");
	}
	/**
	 * Switch players back to being baseq2.Players
	 */
	public void unload() 
	{
		// we no longer want to be notified of level changes
		Game.removeLevelListener(this);
		
		// turn the players into baseq2 players.
		java.util.Enumeration players = NativeEntity.enumeratePlayers();
		while (players.hasMoreElements())
		{
			try
			{
				NativeEntity ent = (NativeEntity) players.nextElement();

				// get rid of any CTF player objects
				Object obj = ent.getReference();
				if (obj instanceof Player)
				{				
					Player p = (Player) obj;
					p.dispose();

					// replace with a baseq2.Player
					baseq2.Player bp = new baseq2.Player(ent, false);
//					bp.playerBegin(false);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}	
	}
}