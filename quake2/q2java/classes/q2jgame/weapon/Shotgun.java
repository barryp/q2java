
package q2jgame.weapon;

import q2java.*;
import q2jgame.*;

public class Shotgun extends PlayerWeapon
	{
	// all shotgun objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {22, 28, 34, 0};
	private static int[] FIRE_FRAMES = new int[] {8, 9, 0};	
	
public Shotgun()
	{
	super("shells", "models/weapons/v_shotg/tris.md2",
		7, 18, 36, 39, PAUSE_FRAMES, FIRE_FRAMES);
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
	int		damage = 4;
	int		kick = 8;

	if (getWeaponFrame() == 9)
		{
		incWeaponFrame();
		return;
		}

	fOwner.getPlayerViewAngles().angleVectors(forward, right, null);
	fOwner.fKickOrigin.copyFrom(forward).scale(-2);
	fOwner.fKickAngles.x = -2;

	offset = new Vec3(0, 8,  fOwner.fViewHeight-8);
	start = fOwner.projectSource(offset, forward, right);

//	if (is_quad)
//		{
//		damage *= 4;
//		kick *= 4;
//		}

//	if (deathmatch->value)
		Game.fireShotgun(fOwner, start, forward, damage, kick, 500, 500, DEFAULT_DEATHMATCH_SHOTGUN_COUNT);
//	else
//		fireShotgun(fOwner, start, forward, damage, kick, 500, 500, DEFAULT_SHOTGUN_COUNT);

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fOwner.getEntityIndex());
	Engine.writeByte(Engine.MZ_SHOTGUN /*| is_silenced */);
	Engine.multicast(fOwner.getOrigin(), Engine.MULTICAST_PVS);
	
	incWeaponFrame();
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fOwner.alterAmmoCount(-1);
	}
}