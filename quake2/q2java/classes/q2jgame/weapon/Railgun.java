
package q2jgame.weapon;

import q2java.*;

public class Railgun extends PlayerWeapon
	{
	// all machinegun objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {56, 0};
	private static int[] FIRE_FRAMES = new int[] {4, 0};			
	
public Railgun() throws GameException
	{
	super("slugs", "models/weapons/v_rail/tris.md2",
		3, 18, 56, 61, PAUSE_FRAMES, FIRE_FRAMES);
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
	int		damage;
	int		kick;

/*
	if (deathmatch->value)
	{	// normal damage is too extreme in dm
		damage = 100;
		kick = 200;
	}
	else
*/	{
		damage = 150;
		kick = 250;
	}

/*
	if (is_quad)
	{
		damage *= 4;
		kick *= 4;
	}
*/

	fOwner.getViewAngles().angleVectors(forward, right, null);

//	VectorScale (forward, -3, ent->client->kick_origin);
//	ent->client->kick_angles[0] = -3;

	offset = new Vec3(0, 7,  fOwner.fViewHeight - 8);
	start = fOwner.projectSource(offset, forward, right);
	fireRail(fOwner, start, forward, damage, kick);

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fOwner.getEntityIndex());
	Engine.writeByte(Engine.MZ_RAILGUN /*| is_silenced */);
	Engine.multicast(fOwner.getOrigin(), Engine.MULTICAST_PVS);
	
	incWeaponFrame();
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
//	ent->client->pers.inventory[ent->client->ammo_index] -= ent->client->pers.weapon->quantity;		
	}
}