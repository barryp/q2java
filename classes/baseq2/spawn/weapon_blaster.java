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
	super(NativeEntity.EF_BLASTER, 10, Engine.MZ_BLASTER, "blaster");
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
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "w_blaster";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Blaster";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/weapons/g_blast/tris.md2";
	}
/**
 * Get the name of the model used to show the weapon from the player's POV.
 * @return java.lang.String
 */
public String getViewModelName() 
	{
	return "models/weapons/v_blast/tris.md2";
	}
/**
 * Plain hand-blaster can't be dropped.
 * @return boolean
 */
public boolean isDroppable() 
	{
	return false;
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fFrameActivateLast		= 4;
	fFrameFireLast 		= 8;
	fFrameIdleLast 		= 52;
	fFrameDeactivateLast 	= 55;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}