package baseq2.spawn;


import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class weapon_grenadelauncher extends GenericWeapon
	{
	// all rocketlauncher objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {34, 51, 59, 0};
	private final static int[] FIRE_FRAMES = new int[] {6, 0};		
	
/**
 * Construct a grenade launcher for a player to carry.
 */
public weapon_grenadelauncher() 
	{
	}
public weapon_grenadelauncher(String[] spawnArgs) throws GameException
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
	int		damage = 120;
	float	radius = damage + 40;

	damage *= fPlayer.getDamageMultiplier();

	offset = new Vector3f(8, 8,  fPlayer.fViewHeight-8);
	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, right, null);
	fPlayer.fKickOrigin.set(forward);
	fPlayer.fKickOrigin.scale(-2);
	fPlayer.fKickAngles.x = -1;

	start = fPlayer.projectSource(offset, forward, right);

	try
		{
		Class grenadeClass = Game.lookupClass(".Grenade");
		// assume we're tossing a baseq2.Grenade or subclass
		Grenade g = (Grenade) grenadeClass.newInstance();		
		g.toss(fPlayer, start, forward, damage, 600, 2.5F, radius);
		}
	catch (Exception e)
		{
		Game.dprint("Can't create Grenade " + e);
		}		
	
	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_GRENADE /*| is_silenced */);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

	incWeaponFrame();
	
	fPlayer.setAmmoCount(-1, false);
	}
/**
 * Get the name of the type of ammo this weapon uses.
 * @return Name of kind of ammo, may be null if the weapon doesn't use ammo.
 */
public String getAmmoName() 
	{
	return "grenades";
	}
/**
 * Get how much ammo this weapon starts off with.
 * @return int
 */
public int getDefaultAmmoCount() 
	{
	return 5;
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "w_glauncher";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Grenade Launcher";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/weapons/g_launch/tris.md2";	
	}
/**
 * Get the name of the model used to show the weapon from the player's POV.
 * @return java.lang.String
 */
public String getViewModelName() 
	{
	return "models/weapons/v_launch/tris.md2";
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fFrameActivateLast	 =  5;
	fFrameFireLast 		 = 16;
	fFrameIdleLast 		 = 59;
	fFrameDeactivateLast = 64;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;					
	}
}