
package q2jgame.weapon;

import q2java.*;
import q2jgame.*;

public class Machinegun extends PlayerWeapon
	{
	// all machinegun objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {23, 45, 0};
	private final static int[] FIRE_FRAMES = new int[] {4, 5, 0};			
		
	private int fShotCount;
	
public Machinegun()
	{
	super("bullets", "models/weapons/v_machn/tris.md2",
		3, 5, 45, 49, PAUSE_FRAMES, FIRE_FRAMES);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	Vec3 offset	= new Vec3(0, 8, fOwner.fViewHeight - 8);
	Vec3 forward	= new Vec3();
	Vec3 right	= new Vec3();
	Vec3 angles;
	Vec3	 start;
	int kick = 2;
	int damage = 8;
	
	if ((fOwner.fButtons & PlayerCmd.BUTTON_ATTACK) == 0)
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
			fOwner.sound(NativeEntity.CHAN_VOICE, Engine.soundIndex("weapons/noammo.wav"), 1, NativeEntity.ATTN_NORM, 0);
//			ent->pain_debounce_time = level.time + 1;
			}
		fOwner.changeWeapon();
		return;
		}
										
/*
	if (is_quad)
		{
		damage *= 4;
		kick *= 4;
		}
*/

	fOwner.fKickOrigin.set(Game.cRandom() * 0.35, Game.cRandom() * 0.35, Game.cRandom() * 0.35);
	fOwner.fKickAngles.set(fShotCount * -1.5,  Game.cRandom() * 0.7,  Game.cRandom() * 0.7);

	// raise the gun as it is firing
	if (true /*!deathmatch->value */)
		{
		fShotCount++;
		if (fShotCount > 9)
			fShotCount = 9;
		}

	// get start / end positions
	angles = new Vec3(fOwner.getPlayerViewAngles()).add(fOwner.fKickAngles);
	angles.angleVectors(forward, right, null);
	start = fOwner.projectSource(offset, forward, right);
	Game.fireLead(fOwner, start, forward, damage, kick, Engine.TE_GUNSHOT, DEFAULT_BULLET_HSPREAD, DEFAULT_BULLET_VSPREAD);
	
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fOwner.getEntityIndex());
	Engine.writeByte(Engine.MZ_MACHINEGUN /*| is_silenced */);
	Engine.multicast(fOwner.getOrigin(), Engine.MULTICAST_PVS);

//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fOwner.alterAmmoCount(-1);
	}
}