
package q2jgame;

import q2java.*;

public class weapon_blaster extends GenericBlaster
	{
	// all blaster objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {19, 32, 0};
	private static int[] FIRE_FRAMES = new int[] {5, 0};	
	
public weapon_blaster() throws GameException
	{
	super(Engine.modelIndex("models/weapons/v_blast/tris.md2"),
		4, 8, 52, 55, PAUSE_FRAMES, FIRE_FRAMES, 
		EF_BLASTER, 10, Engine.MZ_BLASTER);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	super.fire();
	incWeaponFrame();
	}
}