package baseq2.spawn;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class weapon_railgun extends GenericWeapon
	{
	// all machinegun objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {56, 0};
	private final static int[] FIRE_FRAMES = new int[] {4, 0};				
	
/**
 * Construct a railgun for a player to carry.
 */
public weapon_railgun () 
	{
	}
/**
 * Construct a railgun to sit on the ground
 */
public weapon_railgun(String[] spawnArgs) throws GameException
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
	int		damage;
	int		kick;


//	if (deathmatch->value)
//		{	// normal damage is too extreme in dm
		damage = 100;
		kick = 200;
//		}
//	else
//		{
//		damage = 150;
//		kick = 250;
//		}

	damage *= fPlayer.getDamageMultiplier();
	kick *= fPlayer.getDamageMultiplier();

	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, right, null);
	fPlayer.fKickOrigin.set(forward);
	fPlayer.fKickOrigin.scale(-3);
	fPlayer.fKickAngles.x = -3;

	offset = new Vector3f(0, 7,  fPlayer.fViewHeight - 8);
	start = fPlayer.projectSource(offset, forward, right);
	MiscUtil.fireRail(fPlayer, start, forward, damage, kick);

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_RAILGUN /*| is_silenced */);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);
	
	incWeaponFrame();
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fPlayer.setAmmoCount(-1, false);
	}
/**
 * Get the name of the type of ammo this weapon uses.
 * @return Name of kind of ammo, may be null if the weapon doesn't use ammo.
 */
public String getAmmoName() 
	{
	return "slugs";
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "w_railgun";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Railgun";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/weapons/g_rail/tris.md2";	
	}
/**
 * Get the name of the model used to show the weapon from the player's POV.
 * @return java.lang.String
 */
public String getViewModelName() 
	{
	return "models/weapons/v_rail/tris.md2";
	}
/**
 * Replace the sound
 */
public String getWeaponSound() 
	{
	return "weapons/rg_hum.wav";
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	setAmmoCount(10);	
	
	fFrameActivateLast		= 3;
	fFrameFireLast 		= 18;
	fFrameIdleLast 		= 56;
	fFrameDeactivateLast 	= 61;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}