
package barryp.rocketmania.spawn;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * This replacement for the hand blaster fires
 * rockets that fly slower and do less damage than 
 * a standard Q2 RL, but you never run out of ammo.
 * But be careful not to run while firing..you may run into your own rocket.
 */

public class weapon_blaster extends GenericWeapon
	{
	// all blaster objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {19, 32, 0};
	private final static int[] FIRE_FRAMES = new int[] {5, 0};	
	
/**
 * Create a blaster for a player to carry.
 */
public weapon_blaster()
	{
	}
public weapon_blaster(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	Vector3f forward = new Vector3f();
	Vector3f right = new Vector3f();
	Vector3f offset = new Vector3f(24, 8, fPlayer.fViewHeight - 8);
		
	int		damage = 80 + (int)(MiscUtil.randomFloat() * 20.0);
	int		radiusDamage = 60;
	float	damageRadius = 60;	
/*
	if (is_quad)
		damage *= 4;
*/		
	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, right, null);
	Point3f start = fPlayer.projectSource(offset, forward, right);
	
	fPlayer.fKickOrigin.set(forward);
	fPlayer.fKickOrigin.scale(-2);	
	fPlayer.fKickAngles.x = -1;

	try
		{
		new Rocket(fPlayer, start, forward, damage, 350, damageRadius, radiusDamage);
		}
	catch (GameException e)
		{
		Game.dprint("Can't create Blaster Rocket " + e);
		}		

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_ROCKET);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

//	PlayerNoise(ent, start, PNOISE_WEAPON);	
	incWeaponFrame();
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fWeaponName = "blaster";
	fAmmoName = null;
	fAmmoCount = 0;
	fEntityModel = "models/weapons/g_blast/tris.md2";	
	fViewModel = "models/weapons/v_blast/tris.md2";
	
	fFrameActivateLast		= 4;
	fFrameFireLast 		= 8;
	fFrameIdleLast 		= 52;
	fFrameDeactivateLast 	= 55;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}