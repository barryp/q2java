package menno.ctf.spawn;


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

public class item_tech4 extends GenericTech
{
	protected float fNextSoundTime = 0;
	protected float fNextHealTime  = 0;


	public item_tech4() throws GameException
	{
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
	public void heal()
	{
		if ( fOwner == null )
			System.err.println( "AutoDoc: heal() called without owner" );
		else
		{
			if ( fNextHealTime < Game.getGameTime() )
			{
				boolean noise  = false;
				int     health = fOwner.getHealth();
				int     armor  = fOwner.getArmorCount();

				fNextHealTime = Game.getGameTime();
				if ( health < 150) 
				{
					fOwner.setHealthMax( 150 );
					fOwner.heal( 5, false );
					fNextHealTime += 0.5f;
					noise = true;
				}
				if ( (armor > 0) && (armor < 150) )
				{
					fOwner.setArmorMaxCount( 150 );
					fOwner.setArmorCount( Math.min(150, armor+5) );
					fNextHealTime += 0.5f;
					noise = true;
				}
				if ( noise )
					playSound();
			}
		}
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
			fOwner.fEntity.sound( NativeEntity.CHAN_VOICE, Engine.getSoundIndex("ctf/tech4.wav"), volume, NativeEntity.ATTN_NORM, 0);
		}
	}
}