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


public abstract class GenericFlag extends baseq2.GenericItem implements baseq2.GameTarget
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
	protected Point3f fBaseOrigin;	// the origin when spawned...
	protected Integer fFlagIndex; // index of this flag, 1=Red 2=Blue        
	private   boolean fIconFlash;
	private   int     fIconIndex;
	
	static final float STOP_EPSILON = 0.1f;

	protected GenericFlag( String[] spawnArgs, int flagIndex ) throws GameException
	{
		super( spawnArgs );

		// pretend it's a dropped item
		fIsDropped = true;
		
		// these static indices should actually be udated at the beginning of each level.
		fCaptureSoundIndex = Engine.getSoundIndex( "ctf/flagcap.wav" );
		fReturnSoundIndex  = Engine.getSoundIndex( "ctf/flagret.wav" );
		fIconIndex = Engine.getImageIndex(getIconName());
		fFlagIndex = new Integer(flagIndex);
				
		fIconFlash  = true;
		getTeam().setFlag( this ); // link the team and flag

		fEntity.setModelIndex( Engine.getModelIndex(getModelName()) );
		fEntity.setEffects( getFlagEffects() );
		fEntity.setMins( -15, -15, -15);
		fEntity.setMaxs(  15,  15,  15);

		
		// Make sure that flag doesn't start in a solid object
		Point3f dest = fEntity.getOrigin();
		dest.z -= 128;

		TraceResults tr = Engine.trace( fEntity.getOrigin(), fEntity.getMins(), fEntity.getMaxs(), dest, fEntity, Engine.MASK_SOLID );
		if ( tr.fStartSolid )
		{
			Engine.dprint( getItemName() + " startsolid at " + fEntity.getOrigin() + "\n" );
			dispose();
			return;
		}
		
		// save the origin..
		fBaseOrigin = tr.fEndPos;

		reset();
	}
	public void drop(Player dropper)
	{
		super.drop(dropper, CTF_FLAG_AUTO_RETURN_TIME);
		fState = CTF_FLAG_STATE_DROPPED;

		Object[] args = {fCarrier.getName(), fFlagIndex};
		Game.localecast("menno.ctf.CTFMessages", "lost_flag", args, Engine.PRINT_HIGH);	

		// forget about who was carrying the flag
		fCarrier = null;
		
		//update all stats (also from spectators) that our flag is dropped
		updateAllStats(getIconName() + "d");
	}
	/**
	 * The flag has been dropped, but nobody picked it up.
	 */
	protected void dropTimeout() 
	{
		reset();
		Object[] args = {fFlagIndex};
		Game.localecast("menno.ctf.CTFMessages", "reset_flag", args, Engine.PRINT_HIGH);	
	}
	public Point3f getBaseOrigin()
	{
		return fBaseOrigin;
	}
	public Player getCarrier()
	{
		return fCarrier;
	}
	/**
	 * Get the effects this flag causes on the player.
	 * @return int
	 */
	public abstract int getFlagEffects(); 
	/**
	 * Get the name of the sound to play when a flag is picked up.
	 * @return java.lang.String
	 */
	public String getPickupSound() 
	{
		return "ctf/flagtk.wav";
	}
	/**
	 * Get the name of this flag's small icon.
	 * @return java.lang.String
	 */
	public abstract String getSmallIconName();
	public int getState()
	{
		return fState;
	}
	/**
	 * Get which team this flag belongs to.
	 * @return menno.ctf.Team
	 */
	public abstract Team getTeam();
	/**
	 * Do we want this player touching the flag?.
	 * @return boolean
	 * @param p baseq2.Player
	 */
	public boolean isTouchable(baseq2.Player bp) 
	{
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
		
		// let superclass have final say
		return super.isTouchable(bp);
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
		fCarrier.addFlag( this );

		Object[] args = {fCarrier.getName(), fFlagIndex};
		Game.localecast("menno.ctf.CTFMessages", "got_flag", args, Engine.PRINT_HIGH);	
		
		// setup the listener so, that it's called every 0.8 seconds to flash the carriers flag-icon
		Game.addFrameListener( this, 0, 0.8F );

		//update all stats (also from spectators) that our flag is being carried
		updateAllStats(getIconName() + "t");
	}
	/**
	 * Player the flag capture sound.
	 */
	public void playCaptureSound() 
	{
		fEntity.sound(NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_NO_PHS_ADD+NativeEntity.CHAN_VOICE, fCaptureSoundIndex, 1, NativeEntity.ATTN_NONE, 0);	
	}
	/**
	 * Player the flag return sound.
	 */
	public void playReturnSound() 
	{
		fEntity.sound(NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_NO_PHS_ADD+NativeEntity.CHAN_VOICE, fReturnSoundIndex, 1, NativeEntity.ATTN_NONE, 0);
	}
	/**
	 * Reset the flag to its base position.
	 * Called by dropTimeout() if the flag sits on the ground
	 * too long, and touchFinish() if a player returns their flag.
	 */
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
		fEntity.setEffects( getFlagEffects() );

		fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_NOCLIENT);
		fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
		fEntity.setEvent(NativeEntity.EV_ITEM_RESPAWN);
		fEntity.linkEntity();

		// ask to be called back each server frame to animate the wave
		Game.addFrameListener(this, 0, 0);

		//update all stats (also from spectators) that our flag is at base
		updateAllStats(getIconName());
	}
	/**
	 * Animate the banner.
	 * @param phase int
	 */
	public void runFrame(int phase) 
	{
		if ( fState == CTF_FLAG_STATE_STANDING )
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
			super.runFrame(phase);
	}
	/**
	 * update all stats (also from spectators) to show flag status.
	 */
	protected void updateAllStats(String iconName) 
	{		
		int index  = ( getTeam() == Team.TEAM1 ? Team.STAT_CTF_TEAM1_PIC         : Team.STAT_CTF_TEAM2_PIC         );
		int picnum = Engine.getImageIndex(iconName);
		Enumeration enum = NativeEntity.enumeratePlayers();
		while ( enum.hasMoreElements() )
		{
			NativeEntity ent = (NativeEntity)enum.nextElement();
			ent.setPlayerStat( index, (short)picnum );
		}	
	}
}