
package baseq2;

import q2java.*;
/**
 * Superclass for all weapons lying 
 * around in the world and carried by players.
 *
 * @author Barry Pederson
 */
 
public abstract class GenericWeapon extends GenericItem
	{
	protected String fAmmoName;
	protected int    fAmmoCount;
	protected String fViewModel;
	
	// Player Weapon fields	
	protected Player fPlayer;
						
	protected final static int DEFAULT_BULLET_HSPREAD		= 300;
	protected final static int DEFAULT_BULLET_VSPREAD		= 500;
	protected final static int DEFAULT_SHOTGUN_HSPREAD		= 1000;
	protected final static int DEFAULT_SHOTGUN_VSPREAD		= 500;	
	protected final static int DEFAULT_DEATHMATCH_SHOTGUN_COUNT		= 12;
	protected final static int DEFAULT_SHOTGUN_COUNT				= 12;
	protected final static int DEFAULT_SSHOTGUN_COUNT				= 20;	
	
	// keep our own copy of the animation frame, so
	// we don't have to go back and forth to C so much
	protected int fGunFrame;

	// animation settings
	protected int fFrameActivateLast;
	protected int fFrameFireLast;
	protected int fFrameIdleLast;
	protected int fFrameDeactivateLast;
	protected int[] fPauseFrames;
	protected int[] fFireFrames;	

	// private fields to manage the state of the weapon and
	// its animation
	protected int fWeaponState;
	protected boolean fIsSwitching;
	
	protected final static int WEAPON_UNUSED		= 0;
	protected final static int WEAPON_READY		= 1;
	protected final static int WEAPON_ACTIVATING	= 2;
	protected final static int WEAPON_DROPPING	= 3;
	protected final static int WEAPON_FIRING		= 4;	
	
/**
 * This method was created by a SmartGuide.
 */
public GenericWeapon() 
	{
	setFields();
	}
public GenericWeapon(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setFields();
	
	fEntity.setModel(getModelName());
	fEntity.setEffects(NativeEntity.EF_ROTATE); // all weapons rotate
	fEntity.linkEntity();
	}
/**
 * This method was created by a SmartGuide.
 */
public void activate() 
	{
	fEntity = fPlayer.fEntity;
	fIsSwitching = true;
	fWeaponState = WEAPON_ACTIVATING;
	setWeaponFrame(0);
	fPlayer.setAmmoType(fAmmoName);
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
 * Get how much ammo this weapon is carrying with it.
 * @return int.
 */
public int getAmmoCount() 
	{
	return fAmmoCount;
	}
/**
 * Get the name of the type of ammo this weapon uses.
 * @return Name of kind of ammo, may be null if the weapon doesn't use ammo.
 */
public String getAmmoName() 
	{
	return fAmmoName;
	}
/**
 * All weapons share the same pickup sound.
 * @return java.lang.String
 */
public String getPickupSound() 
	{
	return "misc/w_pkup.wav";
	}
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
	fEntity.setPlayerGunFrame(++fGunFrame);
	}
/**
 * Check whether this weapon has enough ammo to fire.  One unit of
 * ammo is good enough for most weapons.  Special weapons will 
 * override this.
 * @return boolean
 */
public boolean isEnoughAmmo() 
	{
	if (fAmmoName == null)
		return true;
	else		
		return (fPlayer.getAmmoCount(fAmmoName) >= 1);
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public boolean isFiring() 
	{
	return fWeaponState == WEAPON_FIRING;
	}
/**
 * This method was created by a SmartGuide.
 */
protected abstract void setFields();
/**
 * This method was created by a SmartGuide.
 * @param p q2jgame.Player
 */
public void setOwner(Player p) 
	{
	fPlayer = p;
	}
/**
 * This method was created by a SmartGuide.
 */
public final void setWeaponFrame(int newFrame) 
	{
	fGunFrame = newFrame;
	fEntity.setPlayerGunFrame(fGunFrame);
	}
/**
 * This method was created by a SmartGuide.
 * @param mob q2jgame.GenericCharacter
 */
public void touch(Player p) 
	{
	if (p.addWeapon(getClass(), true))
		{
		super.touch(p);
	
		// bring the weapon back in 30 seconds
		setRespawn(30);	
		}
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
			fPlayer.changeWeapon();
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
			fEntity.setPlayerGunIndex(Engine.getModelIndex(fViewModel));
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
		if (((fPlayer.fButtons | fPlayer.fLatchedButtons) & PlayerCmd.BUTTON_ATTACK) != 0)
			{
			fPlayer.fLatchedButtons &= ~PlayerCmd.BUTTON_ATTACK;
			if (isEnoughAmmo())
				{
				fWeaponState = WEAPON_FIRING;
				setWeaponFrame(fFrameActivateLast + 1); // FRAME_FIRE_FIRST = FRAME_ACTIVATE_LAST + 1
				fPlayer.setAnimation(Player.ANIMATE_ATTACK);
				}
			else
				{
				fEntity.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("weapons/noammo.wav"), 1, NativeEntity.ATTN_NORM, 0);
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
						if ((MiscUtil.randomInt() & 15) != 0)
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