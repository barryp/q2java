package q2java.baseq2.spawn;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class weapon_hyperblaster extends GenericBlaster
	{
	// all hyperblaster objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {0};
	private final static int[] FIRE_FRAMES = new int[] {6, 7, 8, 9, 10, 11, 0};			
	
/**
 * Create a hyperblaster for a player to carry.
 */
public weapon_hyperblaster()
	{
	super(NativeEntity.EF_HYPERBLASTER, 20, Engine.MZ_HYPERBLASTER, "hyperblaster");
	}
public weapon_hyperblaster(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	float rotation;
	int weaponSound = Engine.getSoundIndex("weapons/hyprbl1a.wav");

	if ((fPlayer.fButtons & PlayerCmd.BUTTON_ATTACK) == 0)
		incWeaponFrame();
	else
	
		{

		if (!isEnoughAmmo())
			{
//			if (level.time >= ent->pain_debounce_time)
//				{
//				gi.sound(ent, CHAN_VOICE, gi.soundindex("weapons/noammo.wav"), 1, ATTN_NORM, 0);
//				ent->pain_debounce_time = level.time + 1;
//				}
			fPlayer.changeWeapon();
			}
		else
		
			{
			rotation = (float)((getWeaponFrame() - 5) * (Math.PI / 3));
			fBlasterOffset.x = (float)(-4 * Math.sin(rotation));
			fBlasterOffset.y = 0.0F;
			fBlasterOffset.z = (float)(4 * Math.cos(rotation));

			if ((getWeaponFrame() == 6) || (getWeaponFrame() == 9))			
				fEffect = NativeEntity.EF_HYPERBLASTER;
			else
				fEffect = 0;

			super.fire();			
			fPlayer.setAnimation(Player.ANIMATE_ATTACK); // VWep
			fPlayer.setAmmoCount(-1, false);				
			}

		incWeaponFrame();
		if (getWeaponFrame() == 12)
			setWeaponFrame(6);
		}

	if (getWeaponFrame() == 12)
		{
		fEntity.sound(NativeEntity.CHAN_AUTO, Engine.getSoundIndex("weapons/hyprbd1a.wav"), 1, NativeEntity.ATTN_NORM, 0);
//		ent->client->weapon_sound = 0;
		}		
	}
/**
 * Get the name of the type of ammo this weapon uses.
 * @return Name of kind of ammo, may be null if the weapon doesn't use ammo.
 */
public String getAmmoName() 
	{
	return "cells";
	}
/**
 * Get how much ammo this weapon starts off with.
 * @return int
 */
public int getDefaultAmmoCount() 
	{
	return 50;
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "w_hyperblaster";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "HyperBlaster";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/weapons/g_hyperb/tris.md2";	
	}
/**
 * Get the name of the model used to show the weapon from the player's POV.
 * @return java.lang.String
 */
public String getViewModelName() 
	{
	return "models/weapons/v_hyperb/tris.md2";
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fFrameActivateLast	= 5;
	fFrameFireLast 		= 20;
	fFrameIdleLast 		= 49;
	fFrameDeactivateLast 	= 53;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}