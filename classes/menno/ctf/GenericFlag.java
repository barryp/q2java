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


public abstract class GenericFlag extends baseq2.GameObject implements FrameListener, baseq2.GameTarget
{
	public static final int CTF_FLAG_STATE_STANDING = 0;
	public static final int CTF_FLAG_STATE_DROPPED  = 1;
	public static final int CTF_FLAG_STATE_CARRIED  = 2;

	public static final int STAT_CTF_FLAG_PIC         = 21;

	public static final int CTF_FLAG_AUTO_RETURN_TIME	 = 30;	// seconds until auto return

	protected static int fPickupSoundIndex;
	protected static int fCaptureSoundIndex;
	protected static int fReturnSoundIndex;

	protected int     fCurrentFrame;
	protected Player  fCarrier;			// The Player that's carrying us.
	protected int     fState;
	protected float   fReturnTime;
	protected float   fDroppedTime;
	protected Point3f fBaseOrigin;			// the origin when spawned...
	protected int     fModelIndex;
	protected int     fIconIndex;
	protected int     fEffects;
	protected String  fName;
	protected String  fSmallIconName;
	protected Team    fTeam;

	private   boolean fIconFlash;
	
	static final float STOP_EPSILON = 0.1f;

	protected GenericFlag( String[] spawnArgs ) throws GameException
	{
		super( spawnArgs );

		setGenericFields();
		setFields();
				
		fIconFlash  = true;
		fTeam.setFlag( this );

		fEntity.setModelIndex( fModelIndex );
		fEntity.setEffects( fEffects );
		fEntity.setMins( -15, -15, -15);
		fEntity.setMaxs(  15,  15,  15);

		
		// Make sure that flag doesn't start in a solid object
		Point3f dest = fEntity.getOrigin();
		dest.z -= 128;

		TraceResults tr = Engine.trace( fEntity.getOrigin(), fEntity.getMins(), fEntity.getMaxs(), dest, fEntity, Engine.MASK_SOLID );
		if ( tr.fStartSolid )
		{
			Engine.dprint( fName + " startsolid at " + fEntity.getOrigin() + "\n" );
			dispose();
			return;
		}
		
		// save the origin..
		fBaseOrigin = tr.fEndPos;

		reset();
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
	public void drop()
	{

		fState = CTF_FLAG_STATE_DROPPED;

		// Make sure we won't be touched by our tosser
		Angle3f  angle     = fCarrier.fEntity.getAngles();
		Vector3f forward   = new Vector3f();
		Vector3f right     = new Vector3f();
		Vector3f offset    = new Vector3f( 40, 0, 0 );
		angle.getVectors( forward, right, null );
		Point3f      point = fCarrier.projectSource( offset, forward, right );
		TraceResults tr    = Engine.trace( fCarrier.fEntity.getOrigin(), fEntity.getMins(), fEntity.getMaxs(), point, fCarrier.fEntity, Engine.CONTENTS_SOLID );
		fEntity.setOrigin( tr.fEndPos );

		// hack the velocity to make it bounce away from the tosser
		forward.x *= 100;
		forward.y *= 100;
		forward.z  = 100;
		fEntity.setVelocity( forward );

		// don't reset the carrier yet, he cannot touch us for two seconds..
		fDroppedTime = Game.getGameTime();
		fReturnTime  = fDroppedTime + CTF_FLAG_AUTO_RETURN_TIME;

		fCarrier.fEntity.setEffects( fCarrier.fEntity.getEffects() & ~fEffects );
		fCarrier.fEntity.setModelIndex3( 0 );
		fCarrier.fEntity.setPlayerStat( STAT_CTF_FLAG_PIC, (short)0 );

		Game.bprint( Engine.PRINT_HIGH, fCarrier.getName() + " lost the " + fName + "\n" );
	
		//update all stats (also from spectators) that our flag is dropped
		int index  = ( fTeam == Team.TEAM1 ? Team.STAT_CTF_TEAM1_PIC         : Team.STAT_CTF_TEAM2_PIC         );
		int picnum = ( fTeam == Team.TEAM1 ? Engine.getImageIndex("i_ctf1d") : Engine.getImageIndex("i_ctf2d") );
		Enumeration enum = NativeEntity.enumeratePlayers();
		while ( enum.hasMoreElements() )
		{
			NativeEntity ent = (NativeEntity)enum.nextElement();
			ent.setPlayerStat( index, (short)picnum );
		}

		// make the item appear...
		fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
		fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_NOCLIENT);
		fEntity.setFrame( 0 );
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
			reset();
			Game.bprint( Engine.PRINT_HIGH, "The " + fName + " has returned!\n" );
			return;
		}
		
		if ( (clipVelocity(tr.fPlaneNormal, 1f) & 1) != 0 )
		{
			// laying on the ground...
			fEntity.setVelocity( new Vector3f(0, 0, 0) );
			fEntity.linkEntity();
		}
		
	}
	public Point3f getBaseOrigin()
	{
		return fBaseOrigin;
	}
	public Player getCarrier()
	{
		if ( fState == CTF_FLAG_STATE_CARRIED )
			return fCarrier;
		else
			return null;
	}
	public String getSmallIconName()
	{
		return fSmallIconName;
	}
	public int getState()
	{
		return fState;
	}
	protected void pickupBy( Player p )
	{
		fState = CTF_FLAG_STATE_CARRIED;
		// make the item disappear
		fEntity.setSolid( NativeEntity.SOLID_NOT );
		fEntity.setSVFlags( fEntity.getSVFlags() | NativeEntity.SVF_NOCLIENT );
		fEntity.linkEntity();

		// set the carrier
		fCarrier = p;
		fCarrier.addFlag( this, fIconIndex, fModelIndex, fEffects );
		Game.bprint( Engine.PRINT_HIGH, fCarrier.getName() + " got the " + fName + "\n" );

		// setup the listener so, that it's called every 0.8 seconds to flash the carriers flag-icon
		Game.addFrameListener( this, 0, 0.8F );

		//update all stats (also from spectators) that our flag is being carried
		int index  = ( fTeam == Team.TEAM1 ? Team.STAT_CTF_TEAM1_PIC         : Team.STAT_CTF_TEAM2_PIC         );
		int picnum = ( fTeam == Team.TEAM1 ? Engine.getImageIndex("i_ctf1t") : Engine.getImageIndex("i_ctf2t") );
		Enumeration enum = NativeEntity.enumeratePlayers();
		while ( enum.hasMoreElements() )
		{
			NativeEntity ent = (NativeEntity)enum.nextElement();
			ent.setPlayerStat( index, (short)picnum );
		}
	}
	protected void reset()
	{
		fState        = CTF_FLAG_STATE_STANDING;
		fCurrentFrame = 173;					// first standing frame
		fCarrier      = null;

		// switch off the effects, because a returning flag leaves some glitters...
		fEntity.setEffects( 0 );
		fEntity.setOrigin( fBaseOrigin );
		fEntity.linkEntity();

		// and switch them back on...
		fEntity.setEffects( fEffects );

		fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_NOCLIENT);
		fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
		fEntity.setEvent(NativeEntity.EV_ITEM_RESPAWN);
		fEntity.linkEntity();

		// ask to be called back each server frame
		Game.addFrameListener(this, 0, 0);

		//update all stats (also from spectators) that our flag is at base
		int index  = ( fTeam == Team.TEAM1 ? Team.STAT_CTF_TEAM1_PIC        : Team.STAT_CTF_TEAM2_PIC        );
		int picnum = ( fTeam == Team.TEAM1 ? Engine.getImageIndex("i_ctf1") : Engine.getImageIndex("i_ctf2") );
		Enumeration enum = NativeEntity.enumeratePlayers();
		while ( enum.hasMoreElements() )
		{
			NativeEntity p = (NativeEntity)enum.nextElement();
			p.setPlayerStat( index, (short)picnum );
		}
	}
	/**
	 * Animate the banner.
	 * @param phase int
	 */
	public void runFrame(int phase) 
	{
		if ( fState == CTF_FLAG_STATE_DROPPED )
		{
			// auto return the flag
			if ( Game.getGameTime() > fReturnTime )
			{
				reset();
				Game.bprint( Engine.PRINT_HIGH, "The " + fName + " has returned!\n" );
			}
			else
			{
				// drop to floor
				dropToFloor();
			}
		}
		else if ( fState == CTF_FLAG_STATE_STANDING )
		{
			fCurrentFrame = 173 + (((fCurrentFrame - 173) + 1) % 16);
			fEntity.setFrame(fCurrentFrame);
		}
		else if ( fState == CTF_FLAG_STATE_CARRIED )
		{
			// called every 0.8 seconds, let's flash the carriers flag-icon...
			if ( fIconFlash = !fIconFlash )
				fCarrier.fEntity.setPlayerStat( STAT_CTF_FLAG_PIC, (short)0 );
			else
				fCarrier.fEntity.setPlayerStat( STAT_CTF_FLAG_PIC, (short)fIconIndex );
		}
		else
			Engine.debugLog( "GenericFlag.runFrame(): " + fName + " in non-valid state..." );
	}
	protected abstract void setFields();
	private void setGenericFields()
	{
		// these static indices should actually be udated at the beginning of each level.
		fPickupSoundIndex  = Engine.getSoundIndex( "ctf/flagtk.wav"  );
		fCaptureSoundIndex = Engine.getSoundIndex( "ctf/flagcap.wav" );
		fReturnSoundIndex  = Engine.getSoundIndex( "ctf/flagret.wav" );

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

		if ( p.getTeam() == null )
		{
			p.fEntity.centerprint( "You didn't join a team yet...\n\n" +
									"Type 'team red' or 'team blue'\n" +
									"to join a team.");
			return;
		}

		switch ( fState )
		{
		case CTF_FLAG_STATE_DROPPED:	touchDropped( p );
										break;
		case CTF_FLAG_STATE_STANDING:	touchStanding( p );
										break;
		default:						// Hmm, this state should NOT happen...
										Engine.debugLog( "GenericFlag.runFrame(): " + fName + " in non-valid state..." );
		}

	}
	protected void touchDropped( Player p )
	{
		if ( fTeam == p.getTeam() )
		{
			// on same team, so return the flag
			p.setScore( p.getScore() + Player.CTF_RECOVERY_BONUS );
			reset();
			Game.bprint( Engine.PRINT_HIGH, p.getName() + " has returned the " + fName + "!\n" );
			fEntity.sound(NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_NO_PHS_ADD+NativeEntity.CHAN_VOICE, fReturnSoundIndex, 1, NativeEntity.ATTN_NONE, 0);
			return;
		}
		else
		{
			// not on same team
			if ( fCarrier == p && Game.getGameTime() < (fDroppedTime + 2) )
			{
				p.fEntity.centerprint( "you cannot touch the flag\nwithin 2 seconds of dropping it\n" );
				return;		// a player cannot touch us within 2 seconds that he dropped us...
			}
			else
				pickupBy( p );
		}
	}
	protected void touchStanding( Player p )
	{
		if ( fTeam == p.getTeam() )
		{
			GenericFlag otherFlag = (GenericFlag)p.getInventory("flag");
			if ( otherFlag != null )
			{
				// WE HAVE A CAPTURE !!!!!
				Game.bprint( Engine.PRINT_HIGH, p.getName() + " captured the " + otherFlag.fName + "!\n" );
				fEntity.sound(NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_NO_PHS_ADD+NativeEntity.CHAN_VOICE, fCaptureSoundIndex, 1, NativeEntity.ATTN_NONE, 0);


				// reset the other flag...
				otherFlag.reset();
				p.fEntity.setEffects( p.fEntity.getEffects() & ~otherFlag.fEffects );
				p.fEntity.setModelIndex3( 0 );
				p.fEntity.setPlayerStat( STAT_CTF_FLAG_PIC, (short)0 );
				p.removeInventory( "flag" );

				// Add the bonuses...
				fTeam.addCapture( p );
			}
		}
		else
		{
			// player is from enemy team, so pick it up			
			fEntity.sound(NativeEntity.CHAN_ITEM, fPickupSoundIndex, 1, NativeEntity.ATTN_NONE, 0);
			pickupBy( p );
		}
	}
}