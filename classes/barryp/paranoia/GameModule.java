package barryp.paranoia;

import java.util.Vector;

import q2java.*;
import q2java.gui.*;
import q2jgame.*;

/**
 * BountyHunters Game Module.  Inspired by the "Assassin Quake" mod.
 *
 * @author Barry Pederson
 */
public class GameModule extends q2jgame.GameModule implements LevelListener
	{
	protected static Vector gVictimList = new Vector();

	// handles to keep classes from being GC'ed
	private static Class gTrackerClass = DirectionTracker.class; 
	private static Class gCrosshairClass = SmartCrosshair.class;
	private static Class gBarGraphClass = BarGraph.class;
	
/**
 * Constructor for BountyHunter game module.
 * @param moduleName java.lang.String
 */
public GameModule(String moduleName) 
	{
	super(moduleName);

	// ask to be called on level changes
	Game.addLevelListener(this);	
	}
/**
 * Add a player to the open contract list.
 * @param p barryp.contract.Player
 */
public static void addPlayer(Player p) 
	{
	if (!gVictimList.contains(p))
		gVictimList.addElement(p);
	}
/**
 * Assign a victim to a player if possible.
 * @param requestor Player asking for an assignment, necessary to
 *  avoid being assigned to kill yourself.
 */
public static void assignRole(Player requestor) 
	{
	// randomize the order in which requests are granted
	if ((Game.randomInt() & 0x003f) != 0)
		return; // only fullfill one request out of every 64
		
	int selection = (Game.randomInt() & 0x0fff) % gVictimList.size();	
	Player p = (Player) gVictimList.elementAt(selection);
	if (p != requestor)
		{
		gVictimList.removeElementAt(selection);
		gVictimList.removeElement(requestor);
		requestor.setVictim(p);
		p.setStalker(requestor);
		return;
		}
	}
/**
 * Called when a new level is starting, after entities have 
 * been spawned.
 */
public void levelEntitiesSpawned()
	{
	// do nothing
	}
/**
 * Remove a Player from the Game.
 * @param p barryp.contract.Player
 */
public static void removePlayer(Player p) 
	{
	gVictimList.removeElement(p);
	}
/**
 * Called when a new level starts.
 */
public void startLevel(String mapname, String entString, String spawnPoint)
	{
	// set the players HUDs
	Engine.setConfigString (Engine.CS_STATUSBAR, Player.BOUNTY_STATUSBAR);

	// precache HUD icons
	DirectionTracker.precacheImages();
	RangeTracker.precacheImages();
	SmartCrosshair.precacheImages();

	// clear the victim list, will be refilled as players respawn
	gVictimList.removeAllElements();
	}
/**
 * Default help svcmd for a GameModule.
 * @param args java.lang.String[]
 */
public void svcmd_help(String[] args) 
	{
	Engine.dprint("No special commands available in Paranoian");
	}
/**
 * Called when module is unloaded.
 */
public void unload() 
	{
	// we no longer want to be notified of level changes
	Game.removeLevelListener(this);	
	}
}