package q2java.ctf;


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
import q2java.baseq2.GenericWeapon;
import q2java.core.*;
import q2java.core.event.*;
import q2java.ctf.*;


/**
 * A misc_ctf_banner is a giant flag that
 * just sits and flutters in the wind.
 */

public class TimeAccel extends GenericTech implements ServerFrameListener
{
	protected float fNextSoundTime = 0;
	public TimeAccel(int hudStat) throws GameException		
	{
		super(hudStat);
	}
	/**
	 * Get the name of this item's icon.
	 * @return java.lang.String
	 */
	public String getIconName() 
	{
		return "tech3";
	}
	/**
	 * Get the name of this item.
	 * @return java.lang.String
 	*/
	public String getItemName() 
	{
		return "Time Accel";
	}
	/**
	 * Get the name of this item's model.
 	* @return java.lang.String
	 */
	public String getModelName() 
	{
		return "models/ctf/haste/tris.md2";	
	}
	/**
	* This method plays a sound on the owner
	**/
	public void playSound()
	{
		if ( getOwner() == null )
			System.err.println( "Time Accel: playSound() called without owner" );
		else
		{
			float volume = 1f;
			//if (self->owner->client->silencer_shots)
			//	volume = 0.2;

			if ( fNextSoundTime < Game.getGameTime() )
			{
				fNextSoundTime = Game.getGameTime() + 1;
				getOwner().fEntity.sound( NativeEntity.CHAN_VOICE, Engine.getSoundIndex("ctf/tech3.wav"), volume, NativeEntity.ATTN_NORM, 0);
			}
		}
	}
	/**
	 * Play noises when the player is firing his weapon, and
	 * double his firing.
	 * @param phase int
	 */
	public void runFrame(int phase) 
	{
		GenericWeapon gw = getOwner().getCurrentWeapon();
		if ((!(gw instanceof GrappleWeapon)) &&  getOwner().getCurrentWeapon().isFiring() )
		{
			gw.weaponThink();
			playSound();
		}
	}
	/**
	 * Set which player is holding the tech.
	 * @param p menno.ctf.Player
	 */
	public void setOwner(q2java.baseq2.Player p) 
	{
		super.setOwner(p);

		// ask to be or not be called back each server frame so we can play noises
		if (p == null)
			Game.removeServerFrameListener(this, Game.FRAME_BEGINNING);
		else
			Game.addServerFrameListener(this, Game.FRAME_BEGINNING, 0, 0);
		
	}
}