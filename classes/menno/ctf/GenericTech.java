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
	protected float   fChangeTime;

	static final float STOP_EPSILON = 0.1f;


	protected GenericTech() throws GameException
	{
		super( null );
		
		// Don't spawn tech if not in deathmatch
		// This causes nullpointer-exceptions, cause deathmatch-spawnpoints,
		// which are needed by techs, are not spawned.
		if ( !GameModule.gIsDeathmatch )
		{
			fEntity.freeEntity();
			throw new InhibitedException( "Techs not spawned in non-deathmatch." );
		}

		fEntity.setModel( getModelName() );
		fEntity.setEffects(NativeEntity.EF_ROTATE); // all techs rotate
		fEntity.linkEntity();

		// set this field very low, so it changes position on the first frame...
		fOwner      = null;
		fChangeTime = Float.MIN_VALUE;
		Game.addFrameListener(this, 0, 0);
	}
	protected void changePosition()
	{
		Point3f point;
		
		// pick a random spot
		point    = baseq2.MiscUtil.getSpawnpointRandom().getOrigin();
		point.z += 16;
		fEntity.setOrigin( point );
		//Engine.debugLog( fPickupName + " changed position to " + point );

		// hack the velocity to make it bounce random
		float x = (Game.randomFloat() * 600) - 300;
		float y = (Game.randomFloat() * 600) - 300;
		float z =  Game.randomFloat() * 300;
		fEntity.setVelocity( new Vector3f(x, y, z) );
	}
	// Maybe we should make a generic class "BouncingObject" or "TossingObject"
	int clipVelocity (Vector3f normal, float overbounce)
	{
		float	 backoff;
		int		 i, blocked;
		Vector3f v;
		
		blocked = 0;
		if (normal.z > 0)
			blocked |= 1;		// floor
		if (normal.z == 0)
			blocked |= 2;		// step
		
		v       = fEntity.getVelocity();
		backoff = v.dot(normal) * overbounce;

		v.x -= normal.x * backoff;
		v.y -= normal.y * backoff;
		v.z -= normal.z * backoff;

		if (v.x > -STOP_EPSILON && v.x < STOP_EPSILON)
			v.x = 0;
		if (v.y > -STOP_EPSILON && v.y < STOP_EPSILON)
			v.y = 0;
		if (v.z > -STOP_EPSILON && v.z < STOP_EPSILON)
			v.z = 0;

		fEntity.setVelocity(v);

		return blocked;
	}
	/**
	 * Clean a few things up before calling NativeEntity.freeEntity().
	 */
	public void dispose() 
	{
		// if we registred as FrameListener, then unregister..
		Game.removeFrameListener( this );

		super.dispose();
	}
	public void drop()
	{
		TraceResults tr;
		fChangeTime  = Game.getGameTime() + CTF_TECH_TIMEOUT;

		// Make sure we won't be touched by our tosser
		Angle3f  angle   = fOwner.fEntity.getAngles();
		Vector3f forward = new Vector3f();
		Vector3f right   = new Vector3f();
		Vector3f offset  = new Vector3f( 40, 0, 0 );
		angle.getVectors( forward, right, null );
		Point3f  point = fOwner.projectSource( offset, forward, right );
		tr = Engine.trace( fOwner.fEntity.getOrigin(), fEntity.getMins(), fEntity.getMaxs(), point, fOwner.fEntity, Engine.CONTENTS_SOLID );
		fEntity.setOrigin( tr.fEndPos );

		// hack the velocity to make it bounce away from the tosser
		forward.x *= 100;
		forward.y *= 100;
		forward.z  = 100;
		fEntity.setVelocity( forward );

		fOwner = null;

		// make the item appear...
		fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
		fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_NOCLIENT);
		fEntity.linkEntity();
	
		// ask to be called back every frame
		Game.addFrameListener(this, 0, 0);
	}
	protected void dropToFloor()
	{
		applyGravity();

		TraceResults tr = fEntity.traceMove(Engine.MASK_SOLID, 1.0F);
		
		if (tr.fFraction == 1)
			return;		// moved the entire distance without hitting anything

		// what to do when hitting the sky...??
		if ((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0))
		{
			changePosition();
			return;
		}
		
		if ( (clipVelocity(tr.fPlaneNormal, 1f) & 1) != 0 )
		{
			// laying on the ground...
			fEntity.setVelocity( new Vector3f(0, 0, 0) );
		}
		
	}
	/**
	 * Checks if CTF_TECH_TIMEOUT has elapsed.
	 * Also does the toss movement...
	 */
	public void runFrame(int phase) 
	{
		if ( Game.getGameTime() > fChangeTime )
		{
			changePosition();
			fChangeTime  = Game.getGameTime() + CTF_TECH_TIMEOUT;
		}

		// drop to floor
		dropToFloor();
		fEntity.linkEntity();
	}
	public void touch( baseq2.Player bp )
	{
		// make sure player is a CTF player
		if (!(bp instanceof Player))
		{
			bp.fEntity.centerprint("You're not a CTF player");
			return;
		}
				
		Player p = (Player)bp;

		if ( p.addTech(this, Engine.getImageIndex(getIconName()) ) )
		{
			// remove us from scene and play pickupsound...
			super.touch( p );
			Game.removeFrameListener( this );
			fOwner = p;
		}
	}
}