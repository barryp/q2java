package menno.ctftech;

import q2java.*;
import q2jgame.*;
import baseq2.InventoryList;

/**
 * Q2Java CTF Techs
 * 
 * @author Barry Pederson
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
		
	}
/**
 * Called when a new map is starting, after entities have been spawned.
 */
public void levelEntitiesSpawned() 
	{
	// now it's time to spawn the techs.
	try 
		{
		new AutoDoc(GenericTech.NO_HUD_ICON);
		new DisruptorShield(GenericTech.NO_HUD_ICON);
		new PowerAmplifier(GenericTech.NO_HUD_ICON);
		new TimeAccel(GenericTech.NO_HUD_ICON);
		}
	catch ( Exception e )
		{
		// do nothing here.
		System.out.println( "error in spawning techs... " + e );
		}	
	}
/**
 * Start a new level.
 */
public void startLevel(String mapname, String entString, String spawnPoint)
	{
	}
/**
 * Help for this module.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("Adds CTF techs to any Q2Java game\n");
	Game.dprint("   no commands available\n");
	}
/**
 * Unload the tech module
 */
public void unload() 
	{
	// we no longer want to be notified of level changes
	Game.removeLevelListener(this);
	}
}