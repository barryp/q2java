
package q2jgame.weapon;

import q2java.*;
import q2jgame.*;

public abstract class PlayerWeapon
	{
	protected Player fOwner;
						
	protected final static int DEFAULT_BULLET_HSPREAD		= 300;
	protected final static int DEFAULT_BULLET_VSPREAD		= 500;
	protected final static int DEFAULT_SHOTGUN_HSPREAD		= 1000;
	protected final static int DEFAULT_SHOTGUN_VSPREAD		= 500;	
	protected final static int DEFAULT_DEATHMATCH_SHOTGUN_COUNT		= 12;
	protected final static int DEFAULT_SHOTGUN_COUNT				= 12;
	protected final static int DEFAULT_SSHOTGUN_COUNT				= 20;	
	
	// keep our own copy of the animation frame, so
	// we don't have to go back and forth to C so much
	private int fGunFrame;

	private String fWeaponName;
	private String fAmmoType;

	// animation settings
	private int fWeaponModel;
	private int fFrameActivateLast;
	private int fFrameFireLast;
	private int fFrameIdleLast;
	private int fFrameDeactivateLast;
	private int[] fPauseFrames;
	private int[] fFireFrames;	

	// private fields to manage the state of the weapon and
	// its animation
	private int fWeaponState;
	private boolean fIsSwitching;
	
	private final static int WEAPON_UNUSED		= 0;
	private final static int WEAPON_READY		= 1;
	private final static int WEAPON_ACTIVATING	= 2;
	private final static int WEAPON_DROPPING		= 3;
	private final static int WEAPON_FIRING		= 4;
	
/**
 * This method was created by a SmartGuide.
 */
public PlayerWeapon(String ammoType, String weaponModelName, int lastActivate, int lastFire, int lastIdle, int lastDeactivate, int[] pauseFrames, int[] fireFrames)
	{
	fAmmoType = ammoType;

	// animation settings
	fWeaponModel = Engine.modelIndex(weaponModelName);
	fFrameActivateLast = lastActivate;
	fFrameFireLast = lastFire;
	fFrameIdleLast = lastIdle;
	fFrameDeactivateLast = lastDeactivate;
	fPauseFrames = pauseFrames;
	fFireFrames = fireFrames;
	}
/**
 * This method was created by a SmartGuide.
 */
public void activate() 
	{
	fIsSwitching = true;
	fWeaponState = WEAPON_ACTIVATING;
	setWeaponFrame(0);
	fOwner.setAmmoType(fAmmoType);
	}
/**
 * This method was created by a SmartGuide.
 */
public void deactivate() 
	{
	fIsSwitching = true;
	}
/**
 * This method was created by a SmartGuide.
 */
public abstract void fire();

/**
 * This method was created by a SmartGuide.
 * @return int
 */
public final int getWeaponFrame() 
	{
	return fGunFrame;
	}
/**
 * This method was created by a SmartGuide.
 */
public final void incWeaponFrame() 
	{
	fOwner.setPlayerGunFrame(++fGunFrame);
	}
/**
 * Check whether this weapon has enough ammo to fire.  One unit of
 * ammo is good enough for most weapons.  Special weapons will 
 * override this.
 * @return boolean
 */
public boolean isEnoughAmmo() 
	{
	if (fAmmoType == null)
		return true;
	else		
		return (fOwner.getAmmoCount(fAmmoType) >= 1);
	}
/**
 * This method was created by a SmartGuide.
 * @param p q2jgame.Player
 */
public void setOwner(Player p) 
	{
	fOwner = p;
	}
/**
 * This method was created by a SmartGuide.
 */
public final void setWeaponFrame(int newFrame) 
	{
	fGunFrame = newFrame;
	fOwner.setPlayerGunFrame(fGunFrame);
	}
/**
 * This method was created by a SmartGuide.
 */
public void weaponThink() 
	{
	if (fWeaponState == WEAPON_UNUSED)
		return;
		
	if (fWeaponState == WEAPON_DROPPING)
		{
		if (fGunFrame == 0)
			{
			fWeaponState = WEAPON_UNUSED;
			fIsSwitching = false;
			fOwner.changeWeapon();
			}
		else		
			{
			if (fGunFrame < fFrameDeactivateLast)
				incWeaponFrame();
			else
				setWeaponFrame(0);
			}				
		return;
		}

	if (fWeaponState == WEAPON_ACTIVATING)
		{
		if (fIsSwitching)
			{
			fOwner.setPlayerGunIndex(fWeaponModel);
			fIsSwitching = false;
			}
			
		if (fGunFrame  < fFrameActivateLast)
			incWeaponFrame();
		else
			{
			fWeaponState = WEAPON_READY;
			setWeaponFrame(fFrameFireLast + 1); // FRAME_IDLE_FIRST = FRAME_FIRE_LAST + 1
			}
		return;
		}

	if (fIsSwitching && (fWeaponState != WEAPON_FIRING))
		{
		fWeaponState = WEAPON_DROPPING;
		setWeaponFrame(fFrameIdleLast + 1); // FRAME_DEACTIVATE_FIRST = FRAME_IDLE_LAST + 1
		return;
		}

	if (fWeaponState == WEAPON_READY)
		{
		if (((fOwner.fButtons | fOwner.fLatchedButtons) & UserCmd.BUTTON_ATTACK) != 0)
			{
			fOwner.fLatchedButtons &= ~UserCmd.BUTTON_ATTACK;
			if (isEnoughAmmo())
				{
				fWeaponState = WEAPON_FIRING;
				setWeaponFrame(fFrameActivateLast + 1); // FRAME_FIRE_FIRST = FRAME_ACTIVATE_LAST + 1
				fOwner.setAnimation(Player.ANIMATE_ATTACK, false);
				}
			else
				{
				fOwner.sound(NativeEntity.CHAN_VOICE, Engine.soundIndex("weapons/noammo.wav"), 1, NativeEntity.ATTN_NORM, 0);
				fWeaponState = WEAPON_DROPPING;
				setWeaponFrame(fFrameIdleLast + 1); // FRAME_DEACTIVATE_FIRST = FRAME_IDLE_LAST + 1
				return;
				}				
			}
		else		
			{
			if (fGunFrame == fFrameIdleLast)
				{
				setWeaponFrame(fFrameFireLast + 1); // FRAME_IDLE_FIRST = FRAME_IDLE_LAST + 1
				return;
				}

			if (fPauseFrames != null)
				{
				int n;
				for (n = 0; fPauseFrames[n] != 0; n++)
					{
					if (fGunFrame == fPauseFrames[n])
						{
						if ((Game.randomInt() & 15) != 0)
							return;
						}
					}
				}
			incWeaponFrame();
			return;
			}
			
		}

	if (fWeaponState == WEAPON_FIRING)
		{
		int n;
		for (n = 0; fFireFrames[n] != 0; n++)
			{
			if (fGunFrame == fFireFrames[n])
				{
/*				
				if (ent->client->quad_framenum > level.framenum)
					gi.sound(ent, CHAN_ITEM, gi.soundindex("items/damage3.wav"), 1, ATTN_NORM, 0);
*/
				fire();
				break;
				}
			}
	
		if (fFireFrames[n] == 0)
			incWeaponFrame();

		if (fGunFrame == fFrameFireLast + 2) // FRAME_IDLE_FIRST = FRAME_FIRE_LAST + 1
			fWeaponState = WEAPON_READY;
		}
		
	}
}