package q2java.baseq2.spawn;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

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
public weapon_supershotgun(Element spawnArgs) throws GameException
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

	damage *= fPlayer.getDamageMultiplier();
	kick *= fPlayer.getDamageMultiplier();

	ang.y -= 5;
	ang.getVectors(forward, null, null);
	MiscUtil.fireShotgun(fPlayer, start, forward, damage, kick, DEFAULT_SHOTGUN_HSPREAD, DEFAULT_SHOTGUN_VSPREAD, DEFAULT_SSHOTGUN_COUNT/2, "sshotgun");
	ang.y += 10;
	ang.getVectors(forward, null, null);
	MiscUtil.fireShotgun(fPlayer, start, forward, damage, kick, DEFAULT_SHOTGUN_HSPREAD, DEFAULT_SHOTGUN_VSPREAD, DEFAULT_SSHOTGUN_COUNT/2, "sshotgun");

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_SSHOTGUN /*| is_silenced */);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);	

	incWeaponFrame();
	
	fPlayer.setAmmoCount(-2, false);
	}
/**
 * Get the name of the type of ammo this weapon uses.
 * @return Name of kind of ammo, may be null if the weapon doesn't use ammo.
 */
public String getAmmoName() 
	{
	return "shells";
	}
/**
 * Get how much ammo this weapon starts off with.
 * @return int
 */
public int getDefaultAmmoCount() 
	{
	return 10;
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "w_sshotgun";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Super Shotgun";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/weapons/g_shotg2/tris.md2";	
	}
/**
 * Get the name of the model used to show the weapon from the player's POV.
 * @return java.lang.String
 */
public String getViewModelName() 
	{
	return "models/weapons/v_shotg2/tris.md2";
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
	fFrameActivateLast	= 6;
	fFrameFireLast 		= 17;
	fFrameIdleLast 		= 57;
	fFrameDeactivateLast 	= 61;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}