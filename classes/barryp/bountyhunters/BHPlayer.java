package barryp.bountyhunters;

import java.io.*;
import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.core.gui.*;

import q2java.baseq2.*;
import q2java.baseq2.spawn.*;

/**
 * BountyHunter player.  
 *
 * @author Barry Pederson
 */

public class BHPlayer extends q2java.baseq2.Player 
	{
	protected BHPlayer fVictim;  // who we're after
	protected BHPlayer fStalker; // who's after us
	
	protected DirectionTracker fTracker;	// HUD widget for tracking victim
	protected RangeTracker     fRange;		// HUD widget for range of victim
	protected SmartCrosshair fCrosshair;	// HUD widget for active crosshair
	
	protected boolean fHasSpawned;
	
	// how many points you get for killing various people
	protected final static int KILL_VICTIM_POINTS	= 5;
	protected final static int KILL_STALKER_POINTS	= 2;
	protected final static int KILL_SELF_POINTS		= -1;
	protected final static int KILL_INNOCENT_POINTS = -2;

	protected final static int GUILT_DAMAGE = 50; // damage from feeling guilty about killing innocents
	
	protected final static int STAT_TRACKER     	= 16;
	protected final static int STAT_RANGE			= 17;
	protected final static int STAT_CROSSHAIR		= 18;
	
	public final static String BOUNTY_STATUSBAR = 
		"yb	-24 " +

		// health
		"xv	0 " +
		"hnum " +
		"xv	50 " +
		"pic 0 " +

		// ammo
		"if 2 " +
		"	xv	100 " +
		"	anum " +
		"	xv	150 " +
		"	pic 2 " +
		"endif " +

		// armor
		"if 4 " +
		"	xv	200 " +
		"	rnum " +
		"	xv	250 " +
		"	pic 4 " +
		"endif " +

		// selected item
		"if 6 " +
		"	xv	296 " +
		"	pic 6 " +
		"endif " +

		"yb	-50 " +

		// picked up item
		"if 7 " +
		"	xv	0 " +
		"	pic 7 " +
		"	xv	26 " +
		"	yb	-42 " +
		"	stat_string 8 " +
		"	yb	-50 " +
		"endif " +

		// timer
		"if 9 " +
		  "xv 246 " +
		  "num 2 10 " +
		  "xv 296 " +
		  "pic 9 " +
		"endif " +

		//  help / weapon icon 
		"if 11 " +
		  "xv 148 " +
		  "pic 11 " +
		"endif " +

		//  frags
		"xr	-50 " +
		"yt 2 " +
		"num 3 14 " +

		"if " + STAT_TRACKER + " " +
			// compass
			"yt 37 " +
			"pic  " + STAT_TRACKER + " " +
		"endif " +
		
		"if " + STAT_RANGE + " " +
			// range
			"yt 85 " +
			"pic " + STAT_RANGE + " " +
		"endif " +

		// target icon
		"if " + STAT_CROSSHAIR + " " +
			"xv 128 " +
			"yv 86 " +
			"pic " + STAT_CROSSHAIR + " " +
		"endif";
	
/**
 * Create a new Player Game object, and associate it with a Player
 * native entity.
 */
public BHPlayer(NativeEntity ent) throws GameException
	{
	super(ent);

	// create a HUD DirectionTracker 
	fTracker = new DirectionTracker(fEntity, STAT_TRACKER);
	fTracker.setVisible(true);

	// create a HUD RangeTracker 
	fRange = new RangeTracker(fEntity, STAT_RANGE);
	fRange.setMinValue(1500);
	fRange.setMaxValue(100);
	fRange.setVisible(true);

	// create a HUD ActiveCrosshair
	fCrosshair = new SmartCrosshair(fEntity, STAT_CROSSHAIR);
	fCrosshair.setVisible(true);
	}
/**
 * Handle the beginning of a server frame.
 */
protected void beginServerFrame()
	{
	// let the superclass do its thing
	super.beginServerFrame();

	// bail if we're not really in the game
	if (fInIntermission || fIsDead || !fHasSpawned)
		return;
		
	if (fVictim == null)
		// try to get the game to assign us a victim
		BountyHunters.assignVictim(this);
	}
/**
 * Announce this player's death to the world.
 * @param attacker baseq2.GameObject the responsible party
 */
public void broadcastObituary(GameObject attacker, String obitKey) 
	{
	if (!(attacker instanceof Player) || (attacker == this))
		{
		if (obitKey.equals("guilt"))
			// special obitKey used by this mod
			Game.bprint(Engine.PRINT_HIGH, getName() + " died because he felt so bad about killing innocent people\n");
		else
			// use regular obituaries if attacker isn't a player or suicided
			super.broadcastObituary(attacker, obitKey);
		}
	else
		{
		BHPlayer killer = (BHPlayer) attacker;
		if (killer.fVictim == this)
			Game.bprint(Engine.PRINT_HIGH, killer.getName() + " terminated " + getName() + "\n");
		else if (killer == fVictim)
			Game.bprint(Engine.PRINT_HIGH, killer.getName() + " managed to kill " + getName() + "\n");
		else
			Game.bprint(Engine.PRINT_HIGH, killer.getName() + " killed " + getName() + " for no reason at all!\n");
		}
	}
/**
 * Disassociate this player from the rest of the players.
 */
protected void clearContracts() 
	{
	// let our victim know who's stalking them now.
	if (fVictim != null)
		{ 
		if (fVictim == fStalker)
			{
			fVictim.setStalker(null);
			BountyHunters.addVictim(fVictim);
			}
		else
			fVictim.setStalker(fStalker); 
		}

	// pass our assignment off to our stalker
	if (fStalker != null) 
		{
		if (fStalker == fVictim)
			fStalker.setVictim(null);
		else
			fStalker.setVictim(fVictim); 
		}
	}
/**
 * Clear the player's settings so they are a fresh 
 * new BountyHunter.
 */
protected void clearSettings( ) 
	{
	super.clearSettings();
	
	setStalker(null);
	setVictim(null);	
	}
/**
 * Handle a new connection by just creating a new Player object 
 * and associating it with the player entity.
 * @param ent q2java.NativeEntity
 * @param playerInfo java.lang.String
 * @param loadgame boolean
 */
public static void connect(NativeEntity ent) throws GameException
	{
	new BHPlayer(ent);
	}
/**
 * Handle dying.
 */
protected void die(GameObject inflictor, GameObject attacker, int damage, Point3f point, String obitKey)
	{
	super.die(inflictor, attacker, damage, point, obitKey);

	// disable stuff on HUD
	fTracker.setTarget(null);
	fCrosshair.setTarget(null);
	fRange.setTarget(null);
	
	// straighten out assignments
	clearContracts();	
	}
/**
 * Disassociate the player from the game.  Commonly called when
 * the player disconnects, or game modules are switched.
 */
public void dispose()
	{
	// let the game module know not to put out contracts on us
	BountyHunters.removePlayer(this);

	// remove HUD Widgets
	fTracker.dispose();
	fCrosshair.dispose();
	fRange.dispose();

	super.dispose();
	}
/**
 * Get who's after this player.
 * @return barryp.contract.Player
 */
public BHPlayer getStalker() 
	{
	return fStalker;
	}
/**
 * Get who this player's after.
 * @return barryp.contract.Player
 */
public BHPlayer getVictim() 
	{
	return fVictim;
	}
/**
 * Override baseq2.Player.registerKill() to use different scoring scheme.
 * @param p baseq2.Player this player's victim, may be the player himself or null.
 */
protected void registerKill(q2java.baseq2.Player p) 
	{
	// killed self
	if ((p == this) || (p == null))
		setScore(KILL_SELF_POINTS, false);
	else if (p == getVictim())
		setScore(KILL_VICTIM_POINTS, false);
	else if (p == getStalker())
		setScore(KILL_STALKER_POINTS, false);
	else
		{
		// killed innocent person, take away points and hurt yourself
		setScore(KILL_INNOCENT_POINTS, false);
		damage(null, null, new Vector3f(0, 0, 1), fEntity.getOrigin(), new Vector3f(0, 0, 0), GUILT_DAMAGE, 0, 0, Engine.TE_NONE, "guilt");		
		}
	}	
/**
 * Set who's after this player.
 * @return barryp.contract.Player
 */
public void setStalker(BHPlayer stalker) 
	{
	fStalker = stalker;
	}
/**
 * Set who this player is after.
 * @return barryp.contract.Player
 */
public void setVictim(BHPlayer victim) 
	{
	fVictim = victim;

	// let the player know who to go after
	if (fVictim == null)
		{
		fTracker.setTarget(null);
		fRange.setTarget(null); // nothing indicated
		fCrosshair.setTarget(null);
		}
	else	
		{
		// track the victim's entity in the world
		fTracker.setTarget(victim.fEntity);
		fCrosshair.setTarget(victim.fEntity);
		fRange.setTarget(victim.fEntity);
		fEntity.centerprint("Kill " + victim.getName());		
		}
	}
/**
 * Place the player in the game.
 */
protected void spawn() 
	{
	// place in game
	super.spawn();

	// make ourselves available for assignment
	BountyHunters.addVictim(this);

	fHasSpawned = true;
	}
/**
 * Switch the player into intermission mode.  Their view should be from
 * a specified intermission spot, their movement should be frozen, and the 
 * scoreboard displayed.
 *
 * @param intermissionSpot The spot the player should be moved do.
 */
public void startIntermission() 
	{
	// clear victims and stalkers..so the hud doesn't
	// show strange stuff during intermission
	setVictim(null);
	setStalker(null);

	super.startIntermission();
	}
/**
 * Welcome the player to the game.
 */
public void welcome() 
	{
	// send effect
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_LOGIN);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

	Object[] args = {getName()};
	Game.localecast("q2java.baseq2.Messages", "entered", args, Engine.PRINT_HIGH);

	if (fVictim == null)
		fEntity.centerprint("Welcome to BountyHunters\n(Inspired by Assassin Quake)\n");
	else
		fEntity.centerprint("Welcome to BountyHunters\n(Inspired by Assassin Quake)\n\nYour first assignment: kill " + fVictim.getName() + "\n");
	}
}