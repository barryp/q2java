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


import javax.vecmath.*;
import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Rockets that have been fired, and are flying through the air.
 */
  
public class GrappleHook extends GameObject implements FrameListener
{
	private int        fDamage;
	private GameObject fOwner;

	public static final int CTF_GRAPPLE_SPEED      = 650; // speed of grapple in flight
	public static final int CTF_GRAPPLE_PULL_SPEED = 650;// speed player is pulled at

	public final static int CTF_GRAPPLE_STATE_PULLING  = 0;
	public final static int CTF_GRAPPLE_STATE_FLYING   = 1;
	public final static int CTF_GRAPPLE_STATE_HANGING  = 2;
	public final static int CTF_GRAPPLE_STATE_DISPOSED = 3;

	protected int     fState;
	protected Point3f fOwnersHangPosition;
	
	/**
	 * BlasterBolt constructor comment.
	 * @exception q2java.GameException The exception description.
	 */
	public GrappleHook(GameObject owner, Point3f start, Vector3f dir, int damage, int speed) throws q2java.GameException 
	{
		fEntity = new NativeEntity();
		fEntity.setReference(this);
		
		fEntity.setOrigin( start );
		fEntity.setAngles( new Angle3f(dir) );
		dir.scale(speed); // this seems wrong...I would think the direction should be normalized first, like the blaster is.
		fEntity.setVelocity( dir );
		fEntity.setClipmask( Engine.MASK_SOLID /*Engine.MASK_SHOT*/ );
		fEntity.setSolid( NativeEntity.SOLID_BBOX );
		fEntity.setModelIndex( Engine.getModelIndex("models/weapons/grapple/hook/tris.md2"));
		fOwner = owner;
		fDamage = damage;
		fState = CTF_GRAPPLE_STATE_FLYING;
		fEntity.linkEntity();
	/*
		if (self->client)
			check_dodge (self, bolt->s.origin, dir, speed);
	*/

		Game.addFrameListener(this, 0, 0);
	}
	/**
	 * This method was created by a SmartGuide.
	 */
	public void dispose() 
	{
		Game.removeFrameListener(this);
		fState = CTF_GRAPPLE_STATE_DISPOSED;

		if ( fOwner != null )
		{
			fOwner.fEntity.sound( NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_WEAPON, 0, 0, NativeEntity.ATTN_NORM, 0);
			fOwner.fEntity.setPlayerPMFlags( (byte)(fOwner.fEntity.getPlayerPMFlags() & ~NativeEntity.PMF_NO_PREDICTION) );
		}
		fOwner = null;
		super.dispose();
	}
	protected void drawCable()
	{
		float	 distance;
		Point3f  start, end;
		Vector3f offset, dir;
		Vector3f forward = new Vector3f();
		Vector3f right = new Vector3f();
		
		Player player = (Player)fOwner;

		Angle3f ang = player.fEntity.getPlayerViewAngles();
		ang.getVectors(forward, right, null);
		offset = new Vector3f( 16, 16, player.fViewHeight-8);//-6 );
		start  = player.projectSource( offset, forward, right );

		offset.sub( start, player.fEntity.getOrigin() );

		dir = new Vector3f( start );
		dir.sub( fEntity.getOrigin() );
		distance = dir.length();

		// don't draw cable if close
		if (distance < 64)
			return;

		// adjust start for beam origin being in middle of a segment
		//VectorMA (start, 8, f, start);

		end = new Point3f( fEntity.getOrigin() );
		// adjust end z for end spot since the monster is currently dead
		//end[2] = self->absmin[2] + self->size[2] / 2;

		Engine.writeByte(Engine.SVC_TEMP_ENTITY);
		//#if 1 //def USE_GRAPPLE_CABLE
			Engine.writeByte( Engine.TE_GRAPPLE_CABLE );
			Engine.writeShort( player.fEntity.getEntityIndex() );
			Engine.writePosition( player.fEntity.getOrigin() );
			Engine.writePosition( end );
			Engine.writePosition( new Point3f(offset) );
		/*#else
			gi.WriteByte (TE_MEDIC_CABLE_ATTACK);
			gi.WriteShort (self - g_edicts);
			gi.WritePosition (end);
			gi.WritePosition (start);
		#endif
		*/
		Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);
	}
	public int getState()
	{
		return fState;
	}
	/**
	* Pulls the fOwner towards us.
	* Called by fOwners playerThink() funtion...
	**/
	protected void pull()
	{
		Vector3f forward, up, hookdir;
		Angle3f  ang;
		float    len;

		drawCable();

		// pull player toward grapple
		// this causes icky stuff with prediction, we need to extend
		// the prediction layer to include two new fields in the player
		// move stuff: a point and a velocity.  The client should add
		// that velociy in the direction of the point
		forward = new Vector3f();
		up      = new Vector3f();

		Player player = (Player)fOwner;

		ang = player.fEntity.getPlayerViewAngles();
		ang.getVectors(forward, null, up);
		hookdir = new Vector3f( player.fEntity.getOrigin() );
		hookdir.z += player.fViewHeight;
		hookdir.sub( fEntity.getOrigin(), hookdir );

		len = hookdir.length();

		if ( (len < 64) && (fState == CTF_GRAPPLE_STATE_PULLING) ) 
		{
			float volume = 1f;

			//if (self->owner->client->silencer_shots)
			//	volume = 0.2;

			player.fEntity.sound( NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_WEAPON, Engine.getSoundIndex("weapons/grapple/grhang.wav"), volume, NativeEntity.ATTN_NORM, 0);
			fState = CTF_GRAPPLE_STATE_HANGING;
			fOwner.fEntity.setPlayerPMFlags( (byte)(fOwner.fEntity.getPlayerPMFlags() | NativeEntity.PMF_NO_PREDICTION) );
		}

		hookdir.normalize();
		hookdir.scale( CTF_GRAPPLE_PULL_SPEED );
		player.fEntity.setVelocity( hookdir );
		player.applyGravity();
	}
	/**
	* The runFrame method lets the hook fly through the air.
	* After touching anything solid, the fOwners playerThink() funtion
	* calls our pull() function...
	**/
	public void runFrame(int phase) 
	{
		TraceResults tr = fEntity.traceMove(Engine.MASK_SHOT, 1.0F);
		
		if (tr.fFraction == 1)
		{
			drawCable();
			return;		// moved the entire distance
		}

		// 'scuse me while I kiss the sky...
		if ((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0))
		{
			dispose();
			return;
		}

		// OK, we hit something, let's pull...
		float volume = 1f;

		//if (self->owner->client->silencer_shots)
		//	volume = 0.2;

		if (tr.fEntity.getReference() instanceof Player )
		{
			Player victim = (Player) tr.fEntity.getReference();
			victim.damage(this, fOwner, fEntity.getVelocity(), fEntity.getOrigin(), tr.fPlaneNormal, fDamage, 1, 0, 0, "grapple");
			//fEntity.sound( NativeEntity.CHAN_WEAPON, Engine.getSoundIndex("weapons/grapple/grhurt.wav"), volume, NativeEntity.ATTN_NORM, 0);
			dispose();
			return;
		}

		fOwner.fEntity.sound( NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_WEAPON, Engine.getSoundIndex("weapons/grapple/grpull.wav"), volume, NativeEntity.ATTN_NORM, 0);
		fEntity.sound( NativeEntity.CHAN_WEAPON, Engine.getSoundIndex("weapons/grapple/grhit.wav"), volume, NativeEntity.ATTN_NORM, 0);
		fEntity.setSolid( NativeEntity.SOLID_NOT );
		fState = CTF_GRAPPLE_STATE_PULLING;

		Engine.writeByte( Engine.SVC_TEMP_ENTITY );
		Engine.writeByte( Engine.TE_SPARKS );
		Engine.writePosition( fEntity.getOrigin() );
		Engine.writeDir( tr.fPlaneNormal );
		Engine.multicast( fEntity.getOrigin(), Engine.MULTICAST_PVS );

		// Tricky: don't listen to serverframes anymore,
		// but let Player.playerThink() call our pull() function...
		Game.removeFrameListener( this );
	}
}	