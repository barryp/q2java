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
import q2java.core.*;
import q2java.ctf.*;


/**
 * A misc_ctf_banner is a giant flag that
 * just sits and flutters in the wind.
 */

public class PowerAmplifier extends GenericTech
{
	protected final static float DAMAGE_MULTIPLIER = 2f;

	protected float fNextSoundTime = 0;


	public PowerAmplifier(int hudStat) throws GameException
	{
		super(hudStat);
	}
	/**
	* This method returns the damage-multiplier (>1)
	**/
	public float getDamageMultiplier()
	{
		return DAMAGE_MULTIPLIER;
	}
	/**
	 * Get the name of this item's icon.
	 * @return java.lang.String
	 */
	public String getIconName() 
	{
		return "tech2";
	}
	/**
	 * Get the name of this item.
	 * @return java.lang.String
 	*/
	public String getItemName() 
	{
		return "Power Amplifier";
	}
	/**
	 * Get the name of this item's model.
 	* @return java.lang.String
	 */
	public String getModelName() 
	{
		return "models/ctf/strength/tris.md2";	
	}
	/**
	* This method plays a sound on the owner
	**/
	public void playSound()
	{
		if ( getOwner() == null )
			System.err.println( "Power Amplifier: playSound() called without owner" );
		else
		{
			float volume = 1f;
			//if (self->owner->client->silencer_shots)
			//	volume = 0.2;

			if ( fNextSoundTime < Game.getGameTime() )
			{
				fNextSoundTime = Game.getGameTime() + 1;
				//if (ent->client->quad_framenum > level.framenum)
				//	gi.sound(ent, CHAN_VOICE, gi.soundindex("ctf/tech2x.wav"), volume, ATTN_NORM, 0);
				//else
				//	gi.sound(ent, CHAN_VOICE, gi.soundindex("ctf/tech2.wav"), volume, ATTN_NORM, 0);
				getOwner().fEntity.sound( NativeEntity.CHAN_VOICE, Engine.getSoundIndex("ctf/tech2.wav"), volume, NativeEntity.ATTN_NORM, 0);
			}
		}
	}
	/**
	 * Play noises when the player is firing his weapon.
	 * @param phase int
	 */
	public void runFrame(int phase) 
	{
		super.runFrame(phase);

		if ((phase == Game.FRAME_BEGINNING) && (getOwner() != null))
		{
			q2java.baseq2.GenericWeapon gw = getOwner().getCurrentWeapon();
			if ( (!(gw instanceof q2java.ctf.spawn.weapon_grapple)) &&  getOwner().getCurrentWeapon().isFiring() )
				playSound();		
		}
	}
	/**
	 * Set which player is holding the tech.
	 * @param p menno.ctf.Player
	 */
	public void setOwner(q2java.baseq2.Player p) 
	{
		// Adjust the Player's damage-multiplier
		if (p != null)
			// player taking posession of tech..boost his power
			p.setDamageMultiplier( getDamageMultiplier() * p.getDamageMultiplier() );
		else
			{
			if (getOwner() != null)
				// player giving up tech..decrease his power
				getOwner().setDamageMultiplier( getOwner().getDamageMultiplier() / getDamageMultiplier() );
			}

		super.setOwner(p);

		// ask to be or not be called back each server frame so we can play noises
		if (p == null)
			Game.removeServerFrameListener(this, Game.FRAME_BEGINNING);
		else
			Game.addServerFrameListener(this, Game.FRAME_BEGINNING, 0, 0);
		
	}
}