package barryp.bountyhunters;

import java.util.Vector;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.core.gui.*;
import q2java.gui.*;

/**
 * BountyHunters Game Module.  Inspired by the "Assassin Quake" mod.
 *
 * @author Barry Pederson
 */
public class BountyHunters extends q2java.core.Gamelet implements GameStatusListener
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
public BountyHunters(String moduleName) 
	{
	super(moduleName);

	// ask to be called on level changes
	Game.addGameStatusListener(this);	
	}
/**
 * Add a player to the open contract list.
 * @param p barryp.contract.Player
 */
public static void addVictim(BHPlayer p) 
	{
	if (!gVictimList.contains(p))
		gVictimList.addElement(p);
	}
/**
 * Assign a victim to a player if possible.
 * @param requestor Player asking for an assignment, necessary to
 *  avoid being assigned to kill yourself.
 */
public static void assignVictim(BHPlayer requestor) 
	{
	int nEntries = gVictimList.size();

	if (nEntries < 1)
		return;
		
	// randomize the order in which requests are granted
	if ((GameUtil.randomInt() & 0x007) != 0)
		return;
		
	int playerCount = gVictimList.size();
	for (int i = 0; i < playerCount; i++)
		{
		BHPlayer p = (BHPlayer) gVictimList.elementAt(i);
		if (p != requestor)
			{
			gVictimList.removeElementAt(i);
			requestor.setVictim(p);
			p.setStalker(requestor);
			return;
			}
		}
	}
public void gameStatusChanged(GameStatusEvent e)
	{
	if (e.getState() == GameStatusEvent.GAME_PRESPAWN)
		{
		// set the players HUDs
		Engine.setConfigString (Engine.CS_STATUSBAR, BHPlayer.BOUNTY_STATUSBAR);

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
	return BHPlayer.class;
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
public static void removePlayer(BHPlayer p) 
	{
	gVictimList.removeElement(p);

	// fixup the leaving player's victim
	BHPlayer p2 = p.getVictim();
	if (p2 != null)
		{
		p2.setStalker(null);
		addVictim(p2);
		}

	// fixup the leaving player's stalker
	p2 = p.getStalker();
	if (p2 != null)
		p2.setVictim(null);		
	}
/**
 * Default help svcmd for a GameModule.
 * @param args java.lang.String[]
 */
public void svcmd_help(String[] args) 
	{
	Engine.dprint("No special commands available in BountyHunters\n");
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