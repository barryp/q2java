package q2java.baseq2.spawn;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class weapon_chaingun extends GenericWeapon
	{
	// all chaingun objects will share these arrays
	private final static int[] PAUSE_FRAMES = new int[] {38, 43, 51, 61, 0};
	private final static int[] FIRE_FRAMES = new int[] {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 0};				
	
/**
 * Create a chaingun to be carried by a player.
 */
public weapon_chaingun() 
	{
	}	
/**
 * Create a chaingun to sit on the ground
 */
public weapon_chaingun(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	int			i;
	int			shots;
	Point3f		start;
	Vector3f		forward = new Vector3f();
	Vector3f		right = new Vector3f();
	Vector3f		up = new Vector3f();
	float		r, u;
	Vector3f		offset;
	int			damage;
	int			kick = 2;


//	if (deathmatch->value)
		damage = 6;
//	else
//		damage = 8;


	if (getWeaponFrame() == 5)
		fEntity.sound(NativeEntity.CHAN_AUTO, Engine.getSoundIndex("weapons/chngnu1a.wav"), 1, NativeEntity.ATTN_IDLE, 0);

	if ((getWeaponFrame() == 14) && ((fPlayer.fButtons & PlayerCmd.BUTTON_ATTACK) == 0))
		{
		setWeaponFrame(32);
//		ent->client->weapon_sound = 0;
		return;
		}
	else if ((getWeaponFrame() == 21) && ((fPlayer.fButtons & PlayerCmd.BUTTON_ATTACK) != 0)
		&& isEnoughAmmo())
		{
		setWeaponFrame(15);
		}
	else
		{
		incWeaponFrame();
		}

	if (getWeaponFrame() == 22)
		{
//		ent->client->weapon_sound = 0;
		fEntity.sound(NativeEntity.CHAN_AUTO, Engine.getSoundIndex("weapons/chngnd1a.wav"), 1, NativeEntity.ATTN_IDLE, 0);
		}
	else
		{
//		ent->client->weapon_sound = gi.soundindex("weapons/chngnl1a.wav");
		}

	if (getWeaponFrame() <= 9)
		shots = 1;
	else 
		{
		if (getWeaponFrame() <= 14)
			{
			if ((fPlayer.fButtons & PlayerCmd.BUTTON_ATTACK) != 0)
				shots = 2;
			else
				shots = 1;
			}
		else
			shots = 3;
		}
				
	shots = Math.min(shots, fPlayer.getAmmoCount("bullets"));

	if (shots == 0)
		{
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
	fPlayer.fKickAngles.set(GameUtil.cRandom() * 0.7f,  GameUtil.cRandom() * 0.7f,  GameUtil.cRandom() * 0.7f);

	for (i=0 ; i<shots ; i++)
		{
		// get start / end positions
		Angle3f ang = fEntity.getPlayerViewAngles();
		ang.getVectors(forward, right, up);
		r = (float)(7 + GameUtil.cRandom() * 4);
		u = (float)(GameUtil.cRandom() * 4);
		offset = new Vector3f(0, r, u + fPlayer.fViewHeight - 8);
		start = fPlayer.projectSource(offset, forward, right);
		MiscUtil.fireLead(fPlayer, start, forward, damage, kick, Engine.TE_GUNSHOT, DEFAULT_BULLET_HSPREAD, DEFAULT_BULLET_VSPREAD, "chaingun");
		}

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_CHAINGUN1 + shots - 1 /*| is_silenced */);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fPlayer.setAnimation(Player.ANIMATE_ATTACK, false, getWeaponFrame() % 3);  // VWep
	fPlayer.setAmmoCount(-shots, false);
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
	return "w_chaingun";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Chaingun";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/weapons/g_chain/tris.md2";
	}
/**
 * Get the name of the model used to show the weapon from the player's POV.
 * @return java.lang.String
 */
public String getViewModelName() 
	{
	return "models/weapons/v_chain/tris.md2";
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	fFrameActivateLast	= 4;
	fFrameFireLast 		= 31;
	fFrameIdleLast 		= 61;
	fFrameDeactivateLast 	= 64;

	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;			
	}
}