
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class weapon_bfg extends GenericWeapon
	{
	// all bfg objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {39, 45, 50, 55, 0};
	private final static int[] FIRE_FRAMES = new int[] {9, 17, 0};				
	
/**
 * Create a BFG for a player to carry.
 */
public weapon_bfg()
	{
	}
/**
 * Create a BFG to sit on the ground 
 */
public weapon_bfg(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	fEntity.cprint(Engine.PRINT_HIGH, "BFG non-functional");
	incWeaponFrame();
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fWeaponName = "bfg10k";
	fAmmoName = "cells";
	fAmmoCount = 50;
	fEntityModel = "models/weapons/g_bfg/tris.md2";	
	fViewModel = Engine.getModelIndex("models/weapons/v_bfg/tris.md2");
	
	fFrameActivateLast		= 8;
	fFrameFireLast 		= 32;
	fFrameIdleLast 		= 55;
	fFrameDeactivateLast 	= 58;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;			
	}
}