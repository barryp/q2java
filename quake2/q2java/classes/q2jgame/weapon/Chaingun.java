
package q2jgame.weapon;

import q2java.*;
import q2jgame.*;

public class Chaingun extends PlayerWeapon
	{
	// all chaingun objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {38, 43, 51, 61, 0};
	private static int[] FIRE_FRAMES = new int[] {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 0};			
	
public Chaingun() throws GameException
	{
	super("bullets", "models/weapons/v_chain/tris.md2",
		4, 31, 61, 64, PAUSE_FRAMES, FIRE_FRAMES);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	int			i;
	int			shots;
	Vec3			start;
	Vec3			forward = new Vec3();
	Vec3			right = new Vec3();
	Vec3			up = new Vec3();
	float		r, u;
	Vec3			offset;
	int			damage;
	int			kick = 2;


//	if (deathmatch->value)
		damage = 6;
//	else
//		damage = 8;


	if (getWeaponFrame() == 5)
		fOwner.sound(NativeEntity.CHAN_AUTO, Engine.soundIndex("weapons/chngnu1a.wav"), 1, NativeEntity.ATTN_IDLE, 0);

	if ((getWeaponFrame() == 14) && !fOwner.isAttacking())
		{
		setWeaponFrame(32);
//		ent->client->weapon_sound = 0;
		return;
		}
	else if ((getWeaponFrame() == 21) && fOwner.isAttacking()
/*		&& ent->client->pers.inventory[ent->client->ammo_index]*/)
		{
		setWeaponFrame(15);
		}
	else
		{
		incWeaponFrame();
		}

	if (getWeaponFrame() == 22)
		{
//		ent->client->weapon_sound = 0;
		fOwner.sound(NativeEntity.CHAN_AUTO, Engine.soundIndex("weapons/chngnd1a.wav"), 1, NativeEntity.ATTN_IDLE, 0);
		}
	else
		{
//		ent->client->weapon_sound = gi.soundindex("weapons/chngnl1a.wav");
		}

	if (getWeaponFrame() <= 9)
		shots = 1;
	else 
		{
		if (getWeaponFrame() <= 14)
			{
			if (fOwner.isAttacking())
				shots = 2;
			else
				shots = 1;
			}
		else
			shots = 3;
		}
				
	shots = Math.min(shots, fOwner.getAmmoCount("bullets"));

	if (shots == 0)
		{
//		if (level.time >= ent->pain_debounce_time)
			{
			fOwner.sound(NativeEntity.CHAN_VOICE, Engine.soundIndex("weapons/noammo.wav"), 1, NativeEntity.ATTN_NORM, 0);
//			ent->pain_debounce_time = level.time + 1;
			}
		fOwner.changeWeapon();			
		return;
		}

/*
	if (is_quad)
		{
		damage *= 4;
		kick *= 4;
		}
*/
	fOwner.fKickOrigin.set(Game.cRandom() * 0.35, Game.cRandom() * 0.35, Game.cRandom() * 0.35);
	fOwner.fKickAngles.set(Game.cRandom() * 0.7,  Game.cRandom() * 0.7,  Game.cRandom() * 0.7);

	for (i=0 ; i<shots ; i++)
		{
		// get start / end positions
		fOwner.getViewAngles().angleVectors(forward, right, up);
		r = (float)(7 + Game.cRandom() * 4);
		u = (float)(Game.cRandom() * 4);
//		start[2] += ent->viewheight-8;
		offset = new Vec3(0, r, u + fOwner.fViewHeight - 8);
		start = fOwner.projectSource(offset, forward, right);
		fireLead(fOwner, start, forward, damage, kick, Engine.TE_GUNSHOT, DEFAULT_BULLET_HSPREAD, DEFAULT_BULLET_VSPREAD);
		}

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fOwner.getEntityIndex());
	Engine.writeByte(Engine.MZ_CHAINGUN1 + shots - 1 /*| is_silenced */);
	Engine.multicast(fOwner.getOrigin(), Engine.MULTICAST_PVS);
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fOwner.alterAmmoCount(-shots);
	}
}