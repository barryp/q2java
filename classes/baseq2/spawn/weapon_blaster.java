
package baseq2.spawn;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class weapon_blaster extends GenericBlaster
	{
	// all blaster objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {19, 32, 0};
	private final static int[] FIRE_FRAMES = new int[] {5, 0};	
	
/**
 * Create a blaster for a player to carry.
 */
public weapon_blaster()
	{
	super(NativeEntity.EF_BLASTER, 10, Engine.MZ_BLASTER);
	}
public weapon_blaster(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	super.fire();
	incWeaponFrame();
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fWeaponName = "blaster";
	fAmmoName = null;
	fAmmoCount = 0;
	fEntityModel = "models/weapons/g_blast/tris.md2";	
	fViewModel = Engine.getModelIndex("models/weapons/v_blast/tris.md2");
	
	fFrameActivateLast		= 4;
	fFrameFireLast 		= 8;
	fFrameIdleLast 		= 52;
	fFrameDeactivateLast 	= 55;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}