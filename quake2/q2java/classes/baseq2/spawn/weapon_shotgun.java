
package baseq2.spawn;

import javax.vecmath.*;
import q2java.*;
import q2jgame.*;
import baseq2.*;

public class weapon_shotgun extends GenericWeapon
	{
	// all shotgun objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {22, 28, 34, 0};
	private final static int[] FIRE_FRAMES = new int[] {8, 9, 0};		
	
/**
 * Construct a shotgun for a player to carry
 */
public weapon_shotgun() 
	{
	}
/** 
 * Create a shotgun for lying around
 */
public weapon_shotgun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	Point3f		start;
	Vector3f		forward = new Vector3f();
	Vector3f		right = new Vector3f();
	Vector3f		offset;
	int		damage = 4;
	int		kick = 8;

	if (getWeaponFrame() == 9)
		{
		incWeaponFrame();
		return;
		}

	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, right, null);
	fPlayer.fKickOrigin.set(forward);
	fPlayer.fKickOrigin.scale(-2);
	fPlayer.fKickAngles.x = -2;

	offset = new Vector3f(0, 8,  fPlayer.fViewHeight-8);
	start = fPlayer.projectSource(offset, forward, right);

//	if (is_quad)
//		{
//		damage *= 4;
//		kick *= 4;
//		}

//	if (deathmatch->value)
		MiscUtil.fireShotgun(fPlayer, start, forward, damage, kick, 500, 500, DEFAULT_DEATHMATCH_SHOTGUN_COUNT);
//	else
//		fireShotgun(fOwner, start, forward, damage, kick, 500, 500, DEFAULT_SHOTGUN_COUNT);

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_SHOTGUN /*| is_silenced */);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);
	
	incWeaponFrame();
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fPlayer.alterAmmoCount(-1);
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fWeaponName = "shotgun";
	fAmmoName = "shells";
	fAmmoCount = 10;
	fEntityModel = "models/weapons/g_shotg/tris.md2";	
	fViewModel = Engine.getModelIndex("models/weapons/v_shotg/tris.md2");
	
	fFrameActivateLast		= 7;
	fFrameFireLast 		= 18;
	fFrameIdleLast 		= 36;
	fFrameDeactivateLast 	= 39;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}