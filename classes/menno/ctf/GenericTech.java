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


import java.util.*;
import q2java.*;
import q2jgame.*;
//import baseq2.*;
import javax.vecmath.*;

/**
 * A tech is a special powerup, used in CTF games.
 * Techs must be spawned manually, because they do not exist in maps...
 */

public abstract class GenericTech extends baseq2.GenericItem
{

	public final static int CTF_TECH_TIMEOUT       = 60;  // seconds before techs spawn again
	public final static int STAT_CTF_TECH          = 26;

	protected Player  fOwner;			// The Player that's carrying us.
	protected GenericTech() throws GameException
	{
		super( null );

		// Don't spawn tech if not in deathmatch
		// This causes nullpointer-exceptions, cause deathmatch-spawnpoints,
		// which are needed by techs, are not spawned.
		if ( !GameModule.gIsDeathmatch )
		{
			throw new InhibitedException( "Techs not spawned in non-deathmatch." );
		}

		// cause a timeout to be triggered right away so the tech
		// gets to reposition itself.
		setDropTimeout(0);
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
 * Set which player is holding the tech.
 * @param p menno.ctf.Player
 */
public void setOwner(Player p) 
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
}