
package q2jgame;

import q2java.*;

public class weapon_blaster extends GenericBlaster
	{
	
public weapon_blaster(GenericCharacter mob) throws GameException
	{
	super(null);
	fFrameActivateLast = 4;
	fFrameFireLast = 8;
	fFrameIdleLast = 52;
	fFrameDeactivateLast = 55;
	fPauseFrames = new int[] {19, 32, 0};
	fFireFrames = new int[] {5, 0};

	fEffect = EF_BLASTER;
	fDamage = 10;
	fBlasterOffset = new Vec3();
	fMuzzleFlash = Engine.MZ_BLASTER;
	
	setOwner(mob);
	}
/**
 * This method was created by a SmartGuide.
 */
public void activate() 
	{
	super.activate();
	((Player)getOwner()).setGunIndex(Engine.modelIndex("models/weapons/v_blast/tris.md2"));
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	super.fire();
	((Player)getOwner()).setGunFrame(++fGunFrame);
	}
}