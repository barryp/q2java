
package q2jgame.weapon;

import q2java.*;

public class Machinegun extends PlayerWeapon
	{
	// all machinegun objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {23, 45, 0};
	private static int[] FIRE_FRAMES = new int[] {4, 5, 0};			
	
public Machinegun() throws GameException
	{
	super("bullets", "models/weapons/v_machn/tris.md2",
		3, 5, 45, 49, PAUSE_FRAMES, FIRE_FRAMES);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	Vec3 offset	= new Vec3(0, 8, fOwner.fViewHeight - 8);
	Vec3 forward	= new Vec3();
	Vec3 right	= new Vec3();
	Vec3 angles;
	Vec3	 start;
	int kick = 2;
	int damage = 8;
	
	if (!fOwner.isAttacking())
		{
//		ent->client->machinegun_shots = 0;
		incWeaponFrame();
		return;
		}
		
	if (getWeaponFrame() == 5)
		setWeaponFrame(4);
	else
		setWeaponFrame(5);			
						
/*
	if (ent->client->pers.inventory[ent->client->ammo_index] < 1)
		{
		ent->client->ps.gunframe = 6;
		if (level.time >= ent->pain_debounce_time)
			{
			gi.sound(ent, CHAN_VOICE, gi.soundindex("weapons/noammo.wav"), 1, ATTN_NORM, 0);
			ent->pain_debounce_time = level.time + 1;
			}
		NoAmmoWeaponChange (ent);
		return;
		}

	if (is_quad)
		{
		damage *= 4;
		kick *= 4;
		}

	for (i=1 ; i<3 ; i++)
		{
		ent->client->kick_origin[i] = crandom() * 0.35;
		ent->client->kick_angles[i] = crandom() * 0.7;
		}
	ent->client->kick_origin[0] = crandom() * 0.35;
	ent->client->kick_angles[0] = ent->client->machinegun_shots * -1.5;

	// raise the gun as it is firing
	if (!deathmatch->value)
		{
		ent->client->machinegun_shots++;
		if (ent->client->machinegun_shots > 9)
			ent->client->machinegun_shots = 9;
		}
*/

	// get start / end positions
	angles = new Vec3(fOwner.getViewAngles());
//	VectorAdd (ent->client->v_angle, ent->client->kick_angles, angles);
	angles.angleVectors(forward, right, null);
	start = fOwner.projectSource(offset, forward, right);
	fireLead(fOwner, start, forward, damage, kick, Engine.TE_GUNSHOT, DEFAULT_BULLET_HSPREAD, DEFAULT_BULLET_VSPREAD);
	
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fOwner.getEntityIndex());
	Engine.writeByte(Engine.MZ_MACHINEGUN /*| is_silenced */);
	Engine.multicast(fOwner.getOrigin(), Engine.MULTICAST_PVS);

//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fOwner.alterAmmoCount(-1);
	}
}