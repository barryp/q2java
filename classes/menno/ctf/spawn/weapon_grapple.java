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


import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;
import menno.ctf.*;

public class weapon_grapple extends GenericWeapon
{
	// all Grapple objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {10, 18, 27, 0};
	private final static int[] FIRE_FRAMES  = new int[] {6, 9, 0};

	protected GrappleHook fHook = null;
	

	public weapon_grapple()
	{
	}
	public weapon_grapple(String[] spawnArgs) throws GameException
	{
		super(spawnArgs);
	}
	/**
	 * This method was created by a SmartGuide.
	 */
	public void fire() 
	{
		Point3f	start;
		Vector3f	forward = new Vector3f();
		Vector3f	right = new Vector3f();
		Vector3f	offset;
		int		damage = 10;
		int		radiusDamage = 120;
		float	damageRadius = 120;

		// if the the attack button is still down, stay in the firing frame
		if ( ((fPlayer.fButtons & PlayerCmd.BUTTON_ATTACK) != 0) && (fGunFrame == 9))
		{
			// return only when hook still alive..
			if ( fHook != null && fHook.getState() != GrappleHook.CTF_GRAPPLE_STATE_DISPOSED )
				return;
			else
			{
				reset();
				incWeaponFrame();
				return;
			}
		}
		else if ( (fGunFrame == 9) )
		{
			// The last frame is hit, so player has released the attack-button...
			reset();
			incWeaponFrame();
			return;
		}

		incWeaponFrame();

		Angle3f ang = fEntity.getPlayerViewAngles();
		ang.getVectors(forward, right, null);
		fPlayer.fKickOrigin.set(forward);
		fPlayer.fKickOrigin.scale(-2);
		fPlayer.fKickAngles.x = -1;

		offset = new Vector3f(8, 8,  fPlayer.fViewHeight-8);//+2);
		start = fPlayer.projectSource(offset, forward, right);

		float volume = 1.0f;
		//if (ent->client->silencer_shots)
		//	volume = 0.2;

		fEntity.sound(NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_WEAPON, Engine.getSoundIndex("weapons/grapple/grfire.wav"), volume, NativeEntity.ATTN_NORM, 0);

		try
		{
			fHook = new GrappleHook(fPlayer, start, forward, damage, GrappleHook.CTF_GRAPPLE_SPEED );
		}
		catch (GameException e)
		{
			Game.dprint("Can't create GrappleHook: " + e);
		}
		
		//PlayerNoise(ent, start, PNOISE_WEAPON);
	}
	public GrappleHook getHook()
	{
		return fHook;
	}
	/**
 	* Get the name of this item's icon.
 	* @return java.lang.String
 	*/
	public String getIconName() 
	{
		return "w_grapple";
	}
	/**
	 * Get the name of this item.
	 * @return java.lang.String
 	*/
	public String getItemName() 
	{
		return "Grapple";
	}
	/**
	 * Get the name of this item's model.
 	* @return java.lang.String
	 */
	public String getModelName() 
	{
		return null;	
	}
	/**
	* Maybe this method should be in GenericWeapon.java...
	**/
	public baseq2.Player getOwner()
	{
		return fPlayer;
	}
	public void reset()
	{
		fEntity.sound(NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_WEAPON, Engine.getSoundIndex("weapons/grapple/grreset.wav"), 1, NativeEntity.ATTN_NORM, 0);
		if ( fHook != null )
		{
			fHook.dispose();
			fHook = null;
		}
	}
	/**
	 * Fill in the info specific to this type of weapon.
	 **/
	protected void setFields() 
	{
		fAmmoName       = null;
		fAmmoCount      = 0;
		fViewModel      = "models/weapons/grapple/tris.md2";
		
		fFrameActivateLast   = 5;
		fFrameFireLast       = 9;
		fFrameIdleLast       = 31;
		fFrameDeactivateLast = 36;

		fPauseFrames = PAUSE_FRAMES;
		fFireFrames  = FIRE_FRAMES;					
	}
}