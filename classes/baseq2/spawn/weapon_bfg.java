package baseq2.spawn;


import javax.vecmath.*;
import q2java.*;
import q2jgame.*;
import baseq2.*;

public class weapon_bfg extends GenericWeapon
	{
	// all bfg objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {39, 45, 50, 55, 0};
	private final static int[] FIRE_FRAMES = new int[] {9, 17, 0};				
	
/**
 * Create a BFG for a player to carry.
 */
public weapon_bfg()
	{
	}
/**
 * Create a BFG to sit on the ground 
 */
public weapon_bfg(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	Point3f	start;
	Vector3f	forward = new Vector3f();
	Vector3f	right = new Vector3f();
	Vector3f	offset;
	int		damage = ( baseq2.GameModule.gIsDeathmatch ? 200 : 500 );
	float	damageRadius = 1000;

	damage *= fPlayer.getDamageMultiplier();

	if (getWeaponFrame() == 9)
		{
		// send muzzle flash
		Engine.writeByte(Engine.SVC_MUZZLEFLASH);
		Engine.writeShort(fEntity.getEntityIndex());
		Engine.writeByte(Engine.MZ_BFG /*| is_silenced */);
		Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

		incWeaponFrame();
		//PlayerNoise(ent, start, PNOISE_WEAPON);
		return;
		}

	// cells can go down during windup (from power armor hits), so
	// check again and abort firing if we don't have enough now
	if (fPlayer.getAmmoCount(fAmmoName) < 50)
	{
		incWeaponFrame();
		return;
	}

	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, right, null);

	fPlayer.fKickOrigin.set(forward);
	fPlayer.fKickOrigin.scale(-2);

	fPlayer.fKickAngles.set(-40, 0, Game.cRandom()*8);
	// make a big pitch kick with an inverse fall
	//ent->client->v_dmg_pitch = -40;
	//ent->client->v_dmg_roll = crandom()*8;
	//ent->client->v_dmg_time = level.time + DAMAGE_TIME;

	offset = new Vector3f(8, 8,  fPlayer.fViewHeight-8);
	start = fPlayer.projectSource(offset, forward, right);
	try
		{
		new BfgBlast(fPlayer, start, forward, damage, 400, damageRadius);
		}
	catch (GameException e)
		{
		Game.dprint("Can't create BfgBlast " + e);
		}		

	incWeaponFrame();
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	//if (! ( (int)dmflags->value & DF_INFINITE_AMMO ) )
		fPlayer.setAmmoCount(-50, false);

	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "w_bfg";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "BFG10K";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/weapons/g_bfg/tris.md2";
	}
/**
 * Replace the sound
 */
public String getWeaponSound() 
	{
	return "weapons/bfg_hum.wav";
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fAmmoName = "cells";
	fAmmoCount = 50;
	fViewModel = "models/weapons/v_bfg/tris.md2";
	
	fFrameActivateLast		= 8;
	fFrameFireLast 		= 32;
	fFrameIdleLast 		= 55;
	fFrameDeactivateLast 	= 58;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;			
	}
}