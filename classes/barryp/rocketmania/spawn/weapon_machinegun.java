package barryp.rocketmania.spawn;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * A machine gun that occasionally throws out 
 * a very high-speed, but low-damage rocket
 */

public class weapon_machinegun extends q2java.baseq2.spawn.weapon_machinegun
	{
	
/**
 * Create a machinegun for a player to carry.
 */
public weapon_machinegun() 
	{
	}
/**
 * Create a machinegun to sit on the ground.
 */
public weapon_machinegun(Element spawnArgs) throws GameException
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
			Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, Engine.getSoundIndex("weapons/noammo.wav"), 1, NativeEntity.ATTN_NORM, 0);
//			ent->pain_debounce_time = level.time + 1;
			}
		if (isAutoSwitch())
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

	fPlayer.fKickOrigin.set(GameUtil.cRandom() * 0.35f, GameUtil.cRandom() * 0.35f, GameUtil.cRandom() * 0.35f);
	fPlayer.fKickAngles.set(fShotCount * -1.5f,  GameUtil.cRandom() * 0.7f,  GameUtil.cRandom() * 0.7f);

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
	MiscUtil.fireLead(fPlayer, start, forward, damage, kick, Engine.TE_GUNSHOT, DEFAULT_BULLET_HSPREAD, DEFAULT_BULLET_VSPREAD, "machinegun");

	// throw a rocket occasionally
	if ((GameUtil.randomInt() & 0x07) == 0)
		{
		try
			{
			Class rocketClass = Game.lookupClass(".Rocket");
			// assume we're launching a baseq2.Rocket or subclass
			Rocket r = (Rocket) rocketClass.newInstance();		
			r.launch(fPlayer, start, forward, 50, 1500, 50, 50);
			}
		catch (Exception e)
			{
			Game.dprint("Can't create Rocket " + e);
			}		
		}
		
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Game.getSoundSupport().fireMuzzleEvent(fEntity, Engine.MZ_MACHINEGUN));
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fPlayer.setAmmoCount(-1, false);
	}
}