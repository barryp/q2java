package barryp.paranoia;

import java.util.Vector;

import q2java.*;
import q2java.gui.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.core.gui.*;

/**
 * BountyHunters Game Module.  Inspired by the "Assassin Quake" mod.
 *
 * @author Barry Pederson
 */
public class Paranoia extends q2java.core.Gamelet implements GameStatusListener
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
public Paranoia(String moduleName) 
	{
	super(moduleName);	
	}
/**
 * Add a player to the open contract list.
 * @param p barryp.contract.Player
 */
public static void addPlayer(ParanoiaPlayer p) 
	{
	if (!gVictimList.contains(p))
		gVictimList.addElement(p);
	}
/**
 * Assign a victim to a player if possible.
 * @param requestor Player asking for an assignment, necessary to
 *  avoid being assigned to kill yourself.
 */
public static void assignRole(ParanoiaPlayer requestor) 
	{
	int nEntries = gVictimList.size();

	if (nEntries < 1)
		return;
		
	// randomize the order in which requests are granted
	if ((GameUtil.randomInt() & 0x003f) != 0)
		return; // only fullfill one request out of every 64
		
	int selection = (GameUtil.randomInt() & 0x0fff) % gVictimList.size();	
	ParanoiaPlayer p = (ParanoiaPlayer) gVictimList.elementAt(selection);
	if (p != requestor)
		{
		gVictimList.removeElementAt(selection);
		gVictimList.removeElement(requestor);
		requestor.setVictim(p);
		p.setStalker(requestor);
		return;
		}
	}
public void gameStatusChanged(GameStatusEvent e)
	{
	if (e.getState() == GameStatusEvent.GAME_PRESPAWN)
		{
		// set the players HUDs
		Engine.setConfigString (Engine.CS_STATUSBAR, ParanoiaPlayer.BOUNTY_STATUSBAR);
	
		// precache HUD icons
		DirectionTracker.precacheImages();
		RangeTracker.precacheImages();
		SmartCrosshair.precacheImages();

		// clear the victim list, will be refilled as players respawn
		gVictimList.removeAllElements();		
		}
	}
/**
 * Get which Gamelet classes this Gamelet requires.
 * @return java.lang.Class[]
 */
public String[] getGameletDependencies() 
	{
	String[] result = { "q2java.baseq2.BaseQ2" };
	return result;
	}
/**
 * Get which class (if any) this Gamelet wants to use for a Player class.
 * @return java.lang.Class
 */
public Class getPlayerClass() 
	{
	return ParanoiaPlayer.class;
	}
/**
 * Fire up the Gamelet.
 */
public void init() 
	{
	// ask to be called on level changes
	Game.addGameStatusListener(this);	
	}
/**
 * Since we have our own player class, wait for level changes to load/unload.
 * @return boolean
 */
public boolean isLevelChangeRequired() 
	{
	return true;
	}
/**
 * Remove a Player from the Game.
 * @param p barryp.contract.Player
 */
public static void removePlayer(ParanoiaPlayer p) 
	{
	gVictimList.removeElement(p);
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
	Game.removeGameStatusListener(this);	
	}
}