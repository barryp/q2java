
package baseq2.spawn;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class weapon_rocketlauncher extends GenericWeapon
	{
	// all rocketlauncher objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {25, 33, 42, 50, 0};
	private final static int[] FIRE_FRAMES = new int[] {5, 0};		
	
/**
 * Construct a rocket launcher for a player to carry.
 */
public weapon_rocketlauncher() 
	{
	}
public weapon_rocketlauncher(String[] spawnArgs) throws GameException
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
	int		damage = 100 + (int)(Game.randomFloat() * 20.0);
	int		radiusDamage = 120;
	float	damageRadius = 120;

	damage *= fPlayer.getDamageMultiplier();
	radiusDamage *= fPlayer.getDamageMultiplier();

	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, right, null);
	fPlayer.fKickOrigin.set(forward);
	fPlayer.fKickOrigin.scale(-2);
	fPlayer.fKickAngles.x = -1;

	offset = new Vector3f(8, 8,  fPlayer.fViewHeight-8);
	start = fPlayer.projectSource(offset, forward, right);

	try
		{
		new Rocket(fPlayer, start, forward, damage, 650, damageRadius, radiusDamage);
		}
	catch (GameException e)
		{
		Game.dprint("Can't create Rocket " + e);
		}		
	
	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_ROCKET /*| is_silenced */);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);
	
	incWeaponFrame();
	
	fPlayer.setAmmoCount(-1, false);
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "w_rlauncher";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Rocket Launcher";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/weapons/g_rocket/tris.md2";	
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fAmmoName = "rockets";
	fAmmoCount = 10;
	fViewModel = "models/weapons/v_rocket/tris.md2";
	
	fFrameActivateLast		= 4;
	fFrameFireLast 		= 12;
	fFrameIdleLast 		= 50;
	fFrameDeactivateLast 	= 54;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;					
	}
}