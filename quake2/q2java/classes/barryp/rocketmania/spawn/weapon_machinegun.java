
package barryp.rocketmania.spawn;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * A machine gun that occasionally throws out 
 * a very high-speed, but low-damage rocket
 */

public class weapon_machinegun extends GenericWeapon
	{
	// all machinegun objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {23, 45, 0};
	private final static int[] FIRE_FRAMES = new int[] {4, 5, 0};			
	
	private int fShotCount;	
	
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
										
/*
	if (is_quad)
		{
		damage *= 4;
		kick *= 4;
		}
*/

	fPlayer.fKickOrigin.set(MiscUtil.cRandom() * 0.35f, MiscUtil.cRandom() * 0.35f, MiscUtil.cRandom() * 0.35f);
	fPlayer.fKickAngles.set(fShotCount * -1.5f,  MiscUtil.cRandom() * 0.7f,  MiscUtil.cRandom() * 0.7f);

	// raise the gun as it is firing
	if (true /*!deathmatch->value */)
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
	MiscUtil.fireLead(fPlayer, start, forward, damage, kick, Engine.TE_GUNSHOT, DEFAULT_BULLET_HSPREAD, DEFAULT_BULLET_VSPREAD);

	// throw a rocket occasionally
	if ((MiscUtil.randomInt() & 0x07) == 0)
		{
		try
			{
			new Rocket(fPlayer, start, forward, 50, 1500, 50, 50);
			}
		catch (GameException e)
			{
			Game.dprint("Can't create Rocket " + e);
			}		
		}
		
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_MACHINEGUN /*| is_silenced */);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fPlayer.alterAmmoCount(-1);
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fWeaponName = "machinegun";
	fAmmoName = "bullets";
	fAmmoCount = 50;
	fEntityModel = "models/weapons/g_machn/tris.md2";	
	fViewModel = Engine.getModelIndex("models/weapons/v_machn/tris.md2");
	
	fFrameActivateLast		= 3;
	fFrameFireLast 		= 5;
	fFrameIdleLast 		= 45;
	fFrameDeactivateLast 	= 49;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;		
	}
}