
package baseq2.spawn;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

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
	super(NativeEntity.EF_HYPERBLASTER, 20, Engine.MZ_HYPERBLASTER);
	}
public weapon_hyperblaster(String[] spawnArgs) throws GameException
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
			fPlayer.alterAmmoCount(-1);				
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
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fWeaponName = "hyperblaster";
	fWeaponIconName = "w_hyperblaster";	
	fAmmoName = "cells";
	fAmmoCount = 50;
	fEntityModel = "models/weapons/g_hyperb/tris.md2";	
	fViewModel = "models/weapons/v_hyperb/tris.md2";
	
	fFrameActivateLast		= 5;
	fFrameFireLast 		= 20;
	fFrameIdleLast 		= 49;
	fFrameDeactivateLast 	= 53;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}