
package q2jgame.weapon;

import q2java.*;
import q2jgame.*;

public class GenericBlaster extends PlayerWeapon
	{
	protected Vec3 fBlasterOffset;
	protected int fEffect;  // the hyperblaster changes this as it fires
	private int fDamage;
	private int fMuzzleFlash;
	
/**
 * This method was created by a SmartGuide.
 */
public GenericBlaster(String ammoType, String weaponModelName, int lastActivate, int lastFire, int lastIdle, int lastDeactivate, int[] pauseFrames, int[] fireFrames, int blasterEffect, int blasterDamage, int blasterMuzzleFlash)
	{
	super(ammoType, weaponModelName, lastActivate, lastFire, lastIdle, lastDeactivate, pauseFrames, fireFrames);

	fEffect = blasterEffect;
	fDamage = blasterDamage;
	fMuzzleFlash = blasterMuzzleFlash;	
	fBlasterOffset = new Vec3();
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	Vec3 forward = new Vec3();
	Vec3 right = new Vec3();
	Vec3 offset = new Vec3(24, 8, fOwner.fViewHeight - 8);
/*
	if (is_quad)
		damage *= 4;
*/		
	fOwner.getPlayerViewAngles().angleVectors(forward, right, null);
	offset.add(fBlasterOffset);
	Vec3 start = fOwner.projectSource(offset, forward, right);
	
	fOwner.fKickOrigin.copyFrom(forward).scale(-2);	
	fOwner.fKickAngles.x = -1;

	try
		{
		new BlasterBolt(fOwner, start, forward, fDamage, 1000, fEffect);
		}
	catch (GameException e)
		{
		Engine.dprint("Can't create BlasterBolt " + e);
		}		

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fOwner.getEntityIndex());
	Engine.writeByte(fMuzzleFlash);
	Engine.multicast(fOwner.getOrigin(), Engine.MULTICAST_PVS);

//	PlayerNoise(ent, start, PNOISE_WEAPON);	
	}
}