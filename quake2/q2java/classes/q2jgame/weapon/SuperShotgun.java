
package q2jgame.weapon;

import q2java.*;
import q2jgame.*;

public class SuperShotgun extends PlayerWeapon
	{
	// all shotgun objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {29, 42, 57, 0};
	private final static int[] FIRE_FRAMES = new int[] {7, 0};		
	
public SuperShotgun()
	{
	super("shells", "models/weapons/v_shotg2/tris.md2",
		6, 17, 57, 61, PAUSE_FRAMES, FIRE_FRAMES);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	Vec3		start;
	Vec3		forward = new Vec3();
	Vec3		right = new Vec3();
	Vec3		offset;
	Vec3		v;
	int		damage = 6;
	int		kick = 12;

	v = fOwner.getPlayerViewAngles();
	v.angleVectors(forward, right, null);

	fOwner.fKickOrigin.set(forward).scale(-2);
	fOwner.fKickAngles.x = -2;

	offset = new Vec3(0, 8,  fOwner.fViewHeight - 8);
	start = fOwner.projectSource(offset, forward, right);

/*
	if (is_quad)
		{
		damage *= 4;
		kick *= 4;
		}
*/

	v.y -= 5;
	v.angleVectors(forward, null, null);
	Game.fireShotgun(fOwner, start, forward, damage, kick, DEFAULT_SHOTGUN_HSPREAD, DEFAULT_SHOTGUN_VSPREAD, DEFAULT_SSHOTGUN_COUNT/2);
	v.y += 10;
	v.angleVectors(forward, null, null);
	Game.fireShotgun(fOwner, start, forward, damage, kick, DEFAULT_SHOTGUN_HSPREAD, DEFAULT_SHOTGUN_VSPREAD, DEFAULT_SSHOTGUN_COUNT/2);

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fOwner.getEntityIndex());
	Engine.writeByte(Engine.MZ_SSHOTGUN /*| is_silenced */);
	Engine.multicast(fOwner.getOrigin(), Engine.MULTICAST_PVS);	

	incWeaponFrame();
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fOwner.alterAmmoCount(-2);
	}
/**
 * Override the PlayerWeapon.isEnoughAmmo() method, since 
 * the Super Shotgun requires two shells to fire.
 * @return boolean
 */
public boolean isEnoughAmmo() 
	{
	return (fOwner.getAmmoCount("shells") >= 2);
	}
}