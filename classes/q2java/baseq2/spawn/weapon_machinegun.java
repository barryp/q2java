package q2java.baseq2.spawn;

import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class weapon_machinegun extends GenericWeapon
	{
	// all machinegun objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {23, 45, 0};
	private final static int[] FIRE_FRAMES = new int[] {4, 5, 0};			
	
	protected int fShotCount;	
	
/**
 * Create a machinegun for a player to carry.
 */
public weapon_machinegun() 
	{
	}
/**
 * Create a machinegun to sit on the ground.
 */
public weapon_machinegun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	Vector3f offset	= new Vector3f(0, 8, fPlayer.fViewHeight - 8);
	Vector3f forward	= new Vector3f();
	Vector3f right	= new Vector3f();
	Angle3f angles;
	Point3f start;
	int kick = 2;
	int damage = 8;
	
	if ((fPlayer.fButtons & PlayerCmd.BUTTON_ATTACK) == 0)
		{
		fShotCount = 0;
		incWeaponFrame();
		return;
		}
		
	if (getWeaponFrame() == 5)
		setWeaponFrame(4);
	else
		setWeaponFrame(5);			


	if (!isEnoughAmmo())
		{
		setWeaponFrame(6);
//		if (level.time >= ent->pain_debounce_time)
			{
			fEntity.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("weapons/noammo.wav"), 1, NativeEntity.ATTN_NORM, 0);
//			ent->pain_debounce_time = level.time + 1;
			}
		fPlayer.changeWeapon();
		return;
		}
										
	damage *= fPlayer.getDamageMultiplier();
	kick *= fPlayer.getDamageMultiplier();

	fPlayer.fKickOrigin.set(GameUtil.cRandom() * 0.35f, GameUtil.cRandom() * 0.35f, GameUtil.cRandom() * 0.35f);
	fPlayer.fKickAngles.set(fShotCount * -1.5f,  GameUtil.cRandom() * 0.7f,  GameUtil.cRandom() * 0.7f);

	// raise the gun as it is firing
	if (!BaseQ2.gIsDeathmatch)
		{
		fShotCount++;
		if (fShotCount > 9)
			fShotCount = 9;
		}

	// get start / end positions
	angles = fEntity.getPlayerViewAngles();
	angles.add(fPlayer.fKickAngles);
	angles.getVectors(forward, right, null);
	start = fPlayer.projectSource(offset, forward, right);
	MiscUtil.fireLead(fPlayer, start, forward, damage, kick, Engine.TE_GUNSHOT, DEFAULT_BULLET_HSPREAD, DEFAULT_BULLET_VSPREAD, "machinegun");
	
	fPlayer.setAnimation(Player.ANIMATE_ATTACK, false,(int)(GameUtil.randomFloat() + 0.25f));  //VWep
	
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_MACHINEGUN /*| is_silenced */);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

	fPlayer.setAmmoCount(-1, false);
	}
/**
 * Get the name of the type of ammo this weapon uses.
 * @return Name of kind of ammo, may be null if the weapon doesn't use ammo.
 */
public String getAmmoName() 
	{
	return "bullets";
	}
/**
 * Get how much ammo this weapon starts off with.
 * @return int
 */
public int getDefaultAmmoCount() 
	{
	return 50;
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "w_machinegun";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Machinegun";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/weapons/g_machn/tris.md2";	
	}
/**
 * Get the name of the model used to show the weapon from the player's POV.
 * @return java.lang.String
 */
public String getViewModelName() 
	{
	return "models/weapons/v_machn/tris.md2";
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fFrameActivateLast	= 3;
	fFrameFireLast 		= 5;
	fFrameIdleLast 		= 45;
	fFrameDeactivateLast 	= 49;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}