
package barryp.rocketmania.spawn;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * This is a souped-up rocketlauncher that 
 * fires rockets that fly faster and do more damage
 */

public class weapon_rocketlauncher extends baseq2.spawn.weapon_rocketlauncher
	{
	
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
	int		damage = 150 + (int)(MiscUtil.randomFloat() * 20.0);
	int		radiusDamage = 180;
	float	damageRadius = 180;

//	if (is_quad)
//		{
//		damage *= 4;
//		radius_damage *= 4;
//		}


	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, right, null);
	fPlayer.fKickOrigin.set(forward);
	fPlayer.fKickOrigin.scale(-2);
	fPlayer.fKickAngles.x = -1;

	offset = new Vector3f(8, 8,  fPlayer.fViewHeight-8);
	start = fPlayer.projectSource(offset, forward, right);

	try
		{
		new Rocket(fPlayer, start, forward, damage, 1000, damageRadius, radiusDamage);
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
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fPlayer.setAmmoCount(-1, false);
	}
}