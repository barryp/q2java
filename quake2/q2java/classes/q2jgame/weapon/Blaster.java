
package q2jgame.weapon;

import q2java.*;

public class Blaster extends GenericBlaster
	{
	// all blaster objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {19, 32, 0};
	private final static int[] FIRE_FRAMES = new int[] {5, 0};	
	
public Blaster()
	{
	super(null, "models/weapons/v_blast/tris.md2",
		4, 8, 52, 55, PAUSE_FRAMES, FIRE_FRAMES, 
		NativeEntity.EF_BLASTER, 10, Engine.MZ_BLASTER);
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