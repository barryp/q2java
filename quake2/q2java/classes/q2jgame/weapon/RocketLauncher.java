
package q2jgame.weapon;

import q2java.*;
import q2jgame.*;

public class RocketLauncher extends PlayerWeapon
	{
	// all rocketlauncher objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {25, 33, 42, 50, 0};
	private final static int[] FIRE_FRAMES = new int[] {5, 0};	
	
public RocketLauncher()
	{
	super("rockets", "models/weapons/v_rocket/tris.md2",
		4, 12, 50, 54, PAUSE_FRAMES, FIRE_FRAMES);
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
	int		damage = 100 + (int)(Game.randomFloat() * 20.0);
	int		radiusDamage = 120;
	float	damageRadius = 120;

//	if (is_quad)
//		{
//		damage *= 4;
//		radius_damage *= 4;
//		}


	fOwner.getPlayerViewAngles().angleVectors(forward, right, null);
	fOwner.fKickOrigin.set(forward).scale(-2);
	fOwner.fKickAngles.x = -1;

	offset = new Vec3(8, 8,  fOwner.fViewHeight-8);
	start = fOwner.projectSource(offset, forward, right);

	try
		{
		new Rocket(fOwner, start, forward, damage, 550, damageRadius, radiusDamage);
		}
	catch (GameException e)
		{
		Engine.dprint("Can't create Rocket " + e);
		}		
	
	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fOwner.getEntityIndex());
	Engine.writeByte(Engine.MZ_ROCKET /*| is_silenced */);
	Engine.multicast(fOwner.getOrigin(), Engine.MULTICAST_PVS);
	
	incWeaponFrame();
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fOwner.alterAmmoCount(-1);
	}
}