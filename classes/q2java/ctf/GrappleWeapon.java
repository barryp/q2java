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

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;
import q2java.ctf.*;

public class GrappleWeapon extends GenericWeapon implements PlayerMoveListener
{
	// all Grapple objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {10, 18, 27, 0};
	private final static int[] FIRE_FRAMES  = new int[] {6, 9, 0};

	protected GrappleHook fHook = null;
	

	public GrappleWeapon()
	{
		setDroppable(false);
	}
	public GrappleWeapon(Element spawnArgs) throws GameException
	{
		super(spawnArgs);
		setDroppable(false);
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

		Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_WEAPON, Engine.getSoundIndex("weapons/grapple/grfire.wav"), volume, NativeEntity.ATTN_NORM, 0);
		fPlayer.addPlayerMoveListener(this);
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
	 * Get the name of the model used to show the weapon from the player's POV.
	 * @return java.lang.String
	 */
	public String getViewModelName() 
	{
		return "models/weapons/grapple/tris.md2";
	}
	public void playerMoved(PlayerMoveEvent pme)
	{
		if ( fHook != null )
		{ 
			if ( fHook.getState() == GrappleHook.CTF_GRAPPLE_STATE_PULLING 
			  || fHook.getState() == GrappleHook.CTF_GRAPPLE_STATE_HANGING )
				 fHook.pull();
		}	
	}
	/**
	 * Called when a player dies, disconnects, or teleports.
	 * @param wasDisconnected true on disconnects, false on normal deaths.
	 */
	public void playerStateChanged(PlayerStateEvent pse)
	{
		switch (pse.getStateChanged())	
		{
		case PlayerStateEvent.STATE_DEAD:
		case PlayerStateEvent.STATE_INVALID:
		case PlayerStateEvent.STATE_SUSPENDEDSTART:
		case PlayerStateEvent.STATE_TELEPORTED:
			reset();
			super.playerStateChanged(pse);
			break;
		}	
	}
	public void reset()
	{
		Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_WEAPON, Engine.getSoundIndex("weapons/grapple/grreset.wav"), 1, NativeEntity.ATTN_NORM, 0);
		if ( fHook != null )
		{
			fHook.dispose();
			fHook = null;
			getOwner().removePlayerMoveListener(this);
		}
	}
	/**
	 * Fill in the info specific to this type of weapon.
	 **/
	protected void setFields() 
	{
		fFrameActivateLast   = 5;
		fFrameFireLast       = 9;
		fFrameIdleLast       = 31;
		fFrameDeactivateLast = 36;

		fPauseFrames = PAUSE_FRAMES;
		fFireFrames  = FIRE_FRAMES;					
	}
}