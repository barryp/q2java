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

public class item_tech3 extends GenericTech
{
	protected float fNextSoundTime = 0;


	public item_tech3() throws GameException
	{
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
		if ( fOwner == null )
			System.err.println( "Time Accel: playSound() called without owner" );
		else
		{
			float volume = 1f;
			//if (self->owner->client->silencer_shots)
			//	volume = 0.2;

			if ( fNextSoundTime < Game.getGameTime() )
			{
				fNextSoundTime = Game.getGameTime() + 1;
				fOwner.fEntity.sound( NativeEntity.CHAN_VOICE, Engine.getSoundIndex("ctf/tech3.wav"), volume, NativeEntity.ATTN_NORM, 0);
			}
		}
	}
}