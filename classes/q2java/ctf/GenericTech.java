package menno.ctftech;


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


import java.util.*;
import q2java.*;
import q2jgame.*;
import javax.vecmath.*;

/**
 * A tech is a special powerup, used in CTF games.
 * Techs must be spawned manually, because they do not exist in maps...
 */

public abstract class GenericTech extends baseq2.GenericItem implements baseq2.PlayerStateListener
{

	public final static int CTF_TECH_TIMEOUT       = 60;  // seconds before techs spawn again
	public final static int NO_HUD_ICON 		   = -1;  // value to use if we don't want the techs to put their icons on the HUD

	private baseq2.Player  fOwner;			// The Player that's carrying us.
	private int fHUDStat;  // HUD Stat to display icon on.
	/**
	 * Create a CTF Tech
	 *
	 *@param hudStat stat to set to display this items icon when picked up, use zero to avoid showing icon.
	 */
	protected GenericTech(int hudStat) throws GameException
	{
		super( null );

		// cause a timeout to be triggered right away so the tech
		// gets to reposition itself.
		setDropTimeout(0);
		fHUDStat = hudStat;
	}
/**
 * Override baseq2.GameObject.becomeExplosion() to prevent techs from being destroyed.
 *
 * @param tempEntity effect to display, usually Engine.TE_EXPLOSION1 or
 *  Engine.TE_EXPLOSION2
 */
public void becomeExplosion(int tempEntity) 
	{
	}
	/**
	 * Drops the item on the ground
	 *
	 * @param dropper the Player tossing the item, so we can avoid colliding with it.
	 * @param timeout number of seconds before the item should do something with itself,
	 *   use zero for no action.  An item will finish falling to the ground before
	 *   it considers what to do, so very small values might not work as accurately.
	 */
	public void drop(baseq2.Player dropper, float timeout)
	{
		// ignore the supplied timeout and use the Tech timeout
		super.drop(dropper, CTF_TECH_TIMEOUT);

		// disassociate the tech from the player
		dropper.fEntity.setPlayerStat( fHUDStat, (short)0 );
		dropper.removePlayerStateListener(this);
		setOwner(null);
	}
	/**
	 * If nobody's touched us in a while, drop in a new spot
	 */
	protected void dropTimeout()
	{
		Point3f point;
		
		// pick a random spot
		point    = baseq2.MiscUtil.getSpawnpointRandom().getOrigin();
		point.z += 16;

		// pick a random direction (pitch -30..-80, yaw 0..360, leave roll at zero)
		Angle3f ang = new Angle3f((Game.randomFloat() * -50) - 30, Game.randomFloat() * 360, 0);

		// fling it with a random speed 300..800
		drop(point, ang, (Game.randomFloat() * 500) + 300, GenericTech.CTF_TECH_TIMEOUT);
	}
	/**
	 * Get the player that's holding the tech.
	 * @return menno.ctf.Player - may be null if not currently held.
	 */
	public baseq2.Player getOwner() 
	{
		return fOwner;
	}
	/**
	 * Do we want this player touching the flag?.
	 * @return boolean
	 * @param p baseq2.Player
	 */
	public boolean isTouchable(baseq2.Player bp) 
	{
/*	
		// make sure player is a CTF player
		if (!(bp instanceof Player))
		{
			bp.fEntity.centerprint(bp.getResourceGroup().getRandomString("menno.ctf.CTFMessages", "not_CTF"));
			return false;
		}
			
		Player p = (Player)bp;

		// make sure CTF Player has joined a team
		Team t = p.getTeam();
		if ( t == null )
		{
			p.fEntity.centerprint(p.getResourceGroup().getRandomString("menno.ctf.CTFMessages", "no_team"));
			return false;
		}
*/
		// make sure player isn't already carrying a tech
		if (bp.isCarrying("tech"))
		{
			bp.fEntity.centerprint(bp.getResourceGroup().getRandomString("menno.ctf.CTFMessages", "have_tech"));		
			return false;
		}
			
		// let superclass have final say
		return super.isTouchable(bp);
	}
	/**
	 * Called when a player dies or disconnects.
	 * @param wasDisconnected true on disconnects, false on normal deaths.
	 */
	public void playerStateChanged(baseq2.Player p, int changeEvent)
	{
		if (changeEvent != baseq2.PlayerStateListener.PLAYER_LEVELCHANGE)
			drop(p, CTF_TECH_TIMEOUT); // will handle removing listener
		else
			p.removePlayerStateListener(this); // just remove the listener
			
		p.removeInventory("tech");
		if (fOwner != null)
			setOwner(null);
	}
	/**
	 * Set which player is holding the tech.
	 * @param p menno.ctf.Player
	 */
	public void setOwner(baseq2.Player p) 
	{
		fOwner = p;
	}
	/**
	 * Setup this item's NativeEntity.
	 */
	public void setupEntity() 
	{
		super.setupEntity();
		fEntity.setEffects(NativeEntity.EF_ROTATE); // all techs rotate
	}
	/**
	 * Called if item was actually taken.
	 * @param p	The Player that took this item.
	 */
	protected void touchFinish(baseq2.Player p, baseq2.GenericItem itemTaken) 
	{
		super.touchFinish(p, itemTaken);

		// add tech to player's inventory and update their hud if playing genuine CTF
		int icon = Engine.getImageIndex(getIconName());
		if (fHUDStat != NO_HUD_ICON)
			p.fEntity.setPlayerStat( fHUDStat, (short)icon );
		p.putInventory( "tech", this );
		setOwner(p);

		// make sure we find out if the player dies
		p.addPlayerStateListener(this);
	}
}