
package baseq2.spawn;

import javax.vecmath.*;
import q2java.*;
import q2jgame.*;
import baseq2.*;

public class weapon_supershotgun extends GenericWeapon
	{
	// all shotgun objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {29, 42, 57, 0};
	private final static int[] FIRE_FRAMES = new int[] {7, 0};			
	
/**
 * Create a super shotgun for the player to carry.
 */
public weapon_supershotgun() 
	{
	}
/**
 * Create a super shotgun to sit on the ground.
 */
public weapon_supershotgun(String[] spawnArgs) throws GameException
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
	int		damage = 6;
	int		kick = 12;

	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, right, null);

	fPlayer.fKickOrigin.set(forward);
	fPlayer.fKickOrigin.scale(-2);
	fPlayer.fKickAngles.x = -2;

	offset = new Vector3f(0, 8,  fPlayer.fViewHeight - 8);
	start = fPlayer.projectSource(offset, forward, right);

/*
	if (is_quad)
		{
		damage *= 4;
		kick *= 4;
		}
*/

	ang.y -= 5;
	ang.getVectors(forward, null, null);
	MiscUtil.fireShotgun(fPlayer, start, forward, damage, kick, DEFAULT_SHOTGUN_HSPREAD, DEFAULT_SHOTGUN_VSPREAD, DEFAULT_SSHOTGUN_COUNT/2);
	ang.y += 10;
	ang.getVectors(forward, null, null);
	MiscUtil.fireShotgun(fPlayer, start, forward, damage, kick, DEFAULT_SHOTGUN_HSPREAD, DEFAULT_SHOTGUN_VSPREAD, DEFAULT_SSHOTGUN_COUNT/2);

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_SSHOTGUN /*| is_silenced */);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);	

	incWeaponFrame();
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fPlayer.alterAmmoCount(-2);
	}
/**
 * Override the PlayerWeapon.isEnoughAmmo() method, since 
 * the Super Shotgun requires two shells to fire.
 * @return boolean
 */
public boolean isEnoughAmmo() 
	{
	return (fPlayer.getAmmoCount("shells") >= 2);
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fWeaponName = "super shotgun";
	fAmmoName = "shells";
	fAmmoCount = 10;
	fEntityModel = "models/weapons/g_shotg2/tris.md2";	
	fViewModel = Engine.getModelIndex("models/weapons/v_shotg2/tris.md2");
	
	fFrameActivateLast		= 6;
	fFrameFireLast 		= 17;
	fFrameIdleLast 		= 57;
	fFrameDeactivateLast 	= 61;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}