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


import q2java.*;
import q2jgame.*;
//import baseq2.*;
import javax.vecmath.*;
import menno.ctf.*;


/**
 * A misc_ctf_banner is a giant flag that
 * just sits and flutters in the wind.
 */

public class AutoDoc extends GenericTech
{
	protected float fNextSoundTime = 0;
	protected float fNextHealTime  = 0;


	public AutoDoc(int hudStat) throws GameException
	{
		super(hudStat);
	}
	/**
	 * Get the name of this item's icon.
	 * @return java.lang.String
	 */
	public String getIconName() 
	{
		return "tech4";
	}
	/**
	 * Get the name of this item.
	 * @return java.lang.String
 	*/
	public String getItemName() 
	{
		return "AutoDoc";
	}
	/**
	 * Get the name of this item's model.
 	* @return java.lang.String
	 */
	public String getModelName() 
	{
		return "models/ctf/regeneration/tris.md2";	
	}
	/**
	* This method plays a sound on the owner
	**/
	public void playSound()
	{
		float volume = 1f;
		//if (self->owner->client->silencer_shots)
		//	volume = 0.2;

		if ( fNextSoundTime < Game.getGameTime() )
		{
			fNextSoundTime = Game.getGameTime() + 1;
			getOwner().fEntity.sound( NativeEntity.CHAN_VOICE, Engine.getSoundIndex("ctf/tech4.wav"), volume, NativeEntity.ATTN_NORM, 0);
		}
	}
	/**
	 * Heal the player that's holding this tech.
	 * @param phase int
	 */
	public void runFrame(int phase) 
	{
		super.runFrame(phase);
		baseq2.Player p = getOwner();
		
		if ((phase == Game.FRAME_BEGINNING) && (p != null))
		{
			if ( fNextHealTime < Game.getGameTime() )
			{
				baseq2.ArmorDamageFilter adf = p.getArmor();
				boolean noise  = false;
				int     health = p.getHealth();
				int     armor  = adf.getArmorCount();

				fNextHealTime = Game.getGameTime();
				if ( health < 150) 
				{
					p.setHealthMax( 150 );
					p.heal( 5, false );
					fNextHealTime += 0.5f;
					noise = true;
				}
				if ( (armor > 0) && (armor < 150) )
				{
					adf.setArmorMaxCount( 150 );
					adf.setArmorCount( Math.min(150, armor+5) );
					fNextHealTime += 0.5f;
					noise = true;
				}
				if ( noise )
					playSound();
			}
		}
	}
	/**
	 * Set which player is holding the tech.
	 * @param p menno.ctf.Player
	 */
	public void setOwner(baseq2.Player p) 
	{
		super.setOwner(p);

		// ask to be or not be called back each server frame so we can heal the player
		if (p == null)
			Game.removeFrameListener(this, Game.FRAME_BEGINNING);
		else
			Game.addFrameListener(this, Game.FRAME_BEGINNING, 0, 0);
	}
}