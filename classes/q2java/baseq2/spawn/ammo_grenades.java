package q2java.baseq2.spawn;

import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Grenades - act both as weapon and ammo
 *
 * @author Menno van Gangelen &ltmenno@element.nl&gt
 */

public class ammo_grenades extends GenericWeapon
	{
	protected final static float GRENADE_TIMER    = 3F;
	protected final static int   GRENADE_MINSPEED = 400;
	protected final static int GRENADE_MAXSPEED   = 800;

	private float   fTimer = 0;
	private boolean fHeld;
	protected String fGrenadeSound;
	
/**
 * Construct a grenade launcher for a player to carry.
 */
public ammo_grenades() 
	{
	}
public ammo_grenades(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	//We have to prechache the weapon sound ourselves
	Engine.getSoundIndex("weapons/hgrenc1b.wav");
	fGrenadeSound = null;
	fEntity.setEffects( fEntity.getEffects() & ~NativeEntity.EF_ROTATE); // all weapons rotate, except grenades...
	}
public void activate() 
	{
	super.activate();
	setWeaponFrame(16);
	fWeaponState = WEAPON_READY;
	fIsSwitching = false;
	fEntity.setPlayerGunIndex(Engine.getModelIndex(getViewModelName()));
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	float timer;
	Point3f	start;
	Vector3f	forward = new Vector3f();
	Vector3f	right = new Vector3f();
	Vector3f	offset;
	int		damage = 125;
	float	radius = damage + 40;
	int     speed;

	damage *= fPlayer.getDamageMultiplier();

	offset = new Vector3f(8, 8,  fPlayer.fViewHeight-8);
	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, right, null);

	start = fPlayer.projectSource(offset, forward, right);
	timer = fTimer - Game.getGameTime();
	speed = (int)(GRENADE_MINSPEED + (GRENADE_TIMER - timer) * ((GRENADE_MAXSPEED - GRENADE_MINSPEED) / GRENADE_TIMER));

	try
		{
		Class hgClass = Game.lookupClass(".HandGrenade");
		// assume we're tossing baseq2.HandGrenade or a subclass
		HandGrenade hg = (HandGrenade) hgClass.newInstance();
		hg.toss(fPlayer, start, forward, damage, speed, timer, radius, fHeld);
		}
	catch (Exception e)
		{
		Game.dprint("Can't create HandGrenade " + e);
		}		
	
	incWeaponFrame();
	
//	PlayerNoise(ent, start, PNOISE_WEAPON);
	fPlayer.setAmmoCount(-1, false);
	fTimer = Game.getGameTime() + 1;
	
	fPlayer.setAnimation(Player.ANIMATE_VWEP_THROW);
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
	return "a_grenades";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Grenades";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/ammo/grenades/medium/tris.md2";	
	}
/**
 * Get the name of the model used to show the weapon from the player's POV.
 * @return java.lang.String
 */
public String getViewModelName() 
	{
	return "models/weapons/v_handgr/tris.md2";
	}
/**
 * Replace the sound
 */
public String getWeaponSound() 
	{
	return fGrenadeSound;
	}
/**
 * Fill in the info specific to this type of weapon.
 */
protected void setFields() 
	{
	setAmmoCount(5);
	
	// The frames of weapon_grenade are different of GenericWeapon..
	// 0 - 15 = throw
	// 16- 48 = idle
	// So all the frame_constants of GenericWeapon are useless
	}
/**
 * This method was created by a SmartGuide.
 */
public void weaponThink() 
	{
	if (fWeaponState == WEAPON_UNUSED)
		return;
		
	if (fIsSwitching && (fWeaponState != WEAPON_FIRING))
		{
		fWeaponState = WEAPON_UNUSED;
		fIsSwitching = false;
		fPlayer.changeWeapon();
		//setWeaponFrame(fFrameIdleLast + 1); // FRAME_DEACTIVATE_FIRST = FRAME_IDLE_LAST + 1
		return;
		}


	if (fWeaponState == WEAPON_READY)
		{
		if (((fPlayer.fButtons | fPlayer.fLatchedButtons) & PlayerCmd.BUTTON_ATTACK) != 0)
			{
			fPlayer.fLatchedButtons &= ~PlayerCmd.BUTTON_ATTACK;
			if (isEnoughAmmo())
				{
				fWeaponState = WEAPON_FIRING;
				setWeaponFrame(1);
				fTimer = 0L;
				}
			else
				{
				//if (level.time >= ent->pain_debounce_time)
					//{
					fEntity.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("weapons/noammo.wav"), 1, NativeEntity.ATTN_NORM, 0);
					//ent->pain_debounce_time = level.time + 1;
					//}
				fPlayer.changeWeapon();
				}
			return;
			}

		if ((fGunFrame == 29) || (fGunFrame == 34) || (fGunFrame == 39) || (fGunFrame == 48))
			{
			if ((GameUtil.randomInt() & 15) != 0)
				return;
			}

		if (fGunFrame > 47)
			setWeaponFrame(16);
		else
			incWeaponFrame();
		return;
		}


	if (fWeaponState == WEAPON_FIRING)
		{
		if (fGunFrame == 5)		// 5 seems to be the frame where the grenade is armed...
			fEntity.sound(NativeEntity.CHAN_WEAPON, Engine.getSoundIndex("weapons/hgrena1b.wav"), 1, NativeEntity.ATTN_NORM, 0);

		if (fGunFrame == 11)	// 11 seems to be the frame where the grenade is held behind the player (resting...)
			{
			if (fTimer == 0L)
				{
				fTimer = Game.getGameTime() + GRENADE_TIMER + 0.2F;
				fGrenadeSound = "weapons/hgrenc1b.wav";
				}

			// they waited too long, detonate it in their hand
			if ( Game.getGameTime() >= fTimer)
				{
				//ent->client->weapon_sound = 0;
				//weapon_grenade_fire (ent, true);
				//ent->client->grenade_blew_up = true;
				fGrenadeSound = null;
				fHeld = true;
				fire();
				setWeaponFrame(15);
				return;
				// new
				}

			if ((fPlayer.fButtons & PlayerCmd.BUTTON_ATTACK) != 0)
				return;
				// returned without increasing weaponframe, so it is held behind the player...

			// There is a hack in the c-version of fire(),
			// which increaases the grenade_time with one second.
			// This is to keep the player from firing another too soon.....
			//if (ent->client->grenade_blew_up)
			//{
			//	if (level.time >= ent->client->grenade_time)
			//	{
					//setWeaponFrame(15);
					//ent->client->ps.gunframe = 15;
			//		ent->client->grenade_blew_up = false;
			//	}
			//	else
			//	{
			//		return;
			//	}
			//??????}
		}

		if (fGunFrame == 12)
			{
			//ent->client->weapon_sound = 0;
			//weapon_grenade_fire (ent, false);
			fGrenadeSound = null;
			fHeld = false;
			fire();
			return;
			}

		if ((fGunFrame == 15) && (Game.getGameTime() < fTimer))
			return;

		incWeaponFrame();

		if (fGunFrame == 16)
			{
			fTimer = 0;
			fWeaponState = WEAPON_READY;
			}
		}

	}
}