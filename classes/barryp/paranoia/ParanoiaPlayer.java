package barryp.paranoia;

import java.io.*;
import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.core.gui.*;

import q2java.baseq2.*;
import q2java.baseq2.event.*;
import q2java.baseq2.spawn.*;

/**
 * Paranoia player.  
 *
 * @author Barry Pederson
 */

public class ParanoiaPlayer extends q2java.baseq2.Player 
	{
	protected ParanoiaPlayer fVictim;  // who we're after
	protected ParanoiaPlayer fStalker; // who's after us
	
	protected DirectionTracker fTracker;	// HUD widget for tracking victim
	protected RangeTracker     fRange;		// HUD widget for range of victim
	protected SmartCrosshair fCrosshair;	// HUD widget for active crosshair
	
	protected boolean fHasSpawned;
	
	// how many points you get for killing various people
	protected final static int KILL_VICTIM_POINTS	= 5;
	protected final static int KILL_STALKER_POINTS	= 5;
	protected final static int KILL_SELF_POINTS		= -1;
	protected final static int KILL_INNOCENT_POINTS = -2;
	protected final static int KILL_LOOSE_CANNON	= 1;

	protected final static int GUILT_DAMAGE = 50; // damage from feeling guilty about killing innocents
	
	protected final static int STAT_TRACKER     	= 16;
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
public ParanoiaPlayer(NativeEntity ent) throws GameException
	{
	super(ent);

	// create a HUD DirectionTracker 
	fTracker = new DirectionTracker(fEntity, STAT_TRACKER);

	// create a HUD RangeTracker 
	fRange = new RangeTracker(fEntity, STAT_TRACKER);
	fRange.setMinValue(1500); // bargraph stops registering at this distance
	fRange.setMaxValue(100);  // max-out bargraph at this distance

	// create a HUD ActiveCrosshair
	fCrosshair = new SmartCrosshair(fEntity, STAT_CROSSHAIR);
	fCrosshair.setRange(225); // gotta get real close to id a victim, 
							  // about the same distance the victim's
							  // first red bargraph segment lights up
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
		
	if (isLooseCannon())
		// try to get the game to assign us a role
		Paranoia.assignRole(this);
	}
/**
 * Announce this player's death to the world.
 * @param attacker baseq2.GameObject the responsible party
 */
public void broadcastObituary(DamageEvent de) 
	{
	GameObject attacker = de.getAttacker();
	if (!(attacker instanceof Player) || (attacker == this))
		{
		if (de.getObitKey().equals("guilt"))
			// special obitKey used by this mod
			Game.bprint(Engine.PRINT_HIGH, getName() + " died because he felt so bad about killing innocent people\n");
		else
			// use regular obituaries if attacker isn't a player or suicided
			super.broadcastObituary(de);
		}
	else
		{
		ParanoiaPlayer killer = (ParanoiaPlayer) attacker;
		if (killer.fVictim == this)
			Game.bprint(Engine.PRINT_HIGH, killer.getName() + " terminated " + getName() + "\n");
		else if (killer == fVictim)
			Game.bprint(Engine.PRINT_HIGH, killer.getName() + " managed to kill " + getName() + "\n");
		else if (isLooseCannon() || killer.isLooseCannon())
			super.broadcastObituary(de); // use regular obit
		else
			Game.bprint(Engine.PRINT_HIGH, killer.getName() + " killed " + getName() + " for no reason at all!\n");
		}
	}
/**
 * Disassociate this player from the rest of the players.
 */
protected void clearContracts() 
	{
	// we were stalking, have the victim get another assignment
	if (fVictim != null)
		{ 
		fVictim.setStalker(null);
		Paranoia.addPlayer(fVictim);
		}

	// we were being stalked, have the stalker get another assignment
	if (fStalker != null) 
		{
		fStalker.setVictim(null);
		Paranoia.addPlayer(fStalker);
		}

	// we were waiting for an assignment, remove from list
	if (isLooseCannon())
		Paranoia.removePlayer(this);
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
	new ParanoiaPlayer(ent);
	}
/**
 * Handle dying.
 */
protected void die(DamageEvent de)
	{
	super.die(de);

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
	// straighten out victim/stalker/waiting lists
	clearContracts();

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
public ParanoiaPlayer getStalker() 
	{
	return fStalker;
	}
/**
 * Get who this player's after.
 * @return barryp.contract.Player
 */
public ParanoiaPlayer getVictim() 
	{
	return fVictim;
	}
/**
 * Is this player a loose cannon?
 * @return boolean
 */
public boolean isLooseCannon() 
	{
	return (fVictim == null) && (fStalker == null);
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
	else if (isLooseCannon() || ((ParanoiaPlayer)p).isLooseCannon())
		setScore(KILL_LOOSE_CANNON, false);
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
public void setStalker(ParanoiaPlayer stalker) 
	{
	fStalker = stalker;

	// update the HUD
	if (fStalker == null)
		{
		fRange.setTarget(null);
		fRange.setVisible(false);
		}
	else
		{
		fRange.setTarget(stalker.fEntity);
		fRange.setVisible(true);
		fEntity.centerprint("You get the feeling someone is after you...\n");
		}
	}
/**
 * Set who this player is after.
 * @return barryp.contract.Player
 */
public void setVictim(ParanoiaPlayer victim) 
	{
	fVictim = victim;

	// let the player know who to go after
	if (fVictim == null)
		{
		fTracker.setTarget(null);
		fTracker.setVisible(false);
		fCrosshair.setTarget(null);
		fCrosshair.setVisible(false);
		}
	else	
		{
		// track the victim's entity in the world
		fTracker.setTarget(victim.fEntity);
		fTracker.setVisible(true);
		fCrosshair.setTarget(victim.fEntity);
		fCrosshair.setVisible(true);
		fEntity.centerprint("Go forth and kill!\n");		
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
	Paranoia.addPlayer(this);

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

	fEntity.centerprint("Welcome to Paranoia\n");
	}
}