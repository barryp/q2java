package q2java.baseq2;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.event.*;


/**
 * Superclass for all weapons lying 
 * around in the world and carried by players.
 *
 * @author Barry Pederson
 */
 
public abstract class GenericWeapon extends AmmoHolder 
  implements PlayerStateListener
	{
	protected int    fVWepIndex;
	
	// Player Weapon fields	
	protected Player fPlayer;
	
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
	private boolean fIsAutoSwitch = true;  //does this weapon automatically switch away when ammo is too low?
	
	// ------- Static Constants ----------
	
	protected final static int DEFAULT_BULLET_HSPREAD		= 300;
	protected final static int DEFAULT_BULLET_VSPREAD		= 500;
	protected final static int DEFAULT_SHOTGUN_HSPREAD		= 1000;
	protected final static int DEFAULT_SHOTGUN_VSPREAD		= 500;	
	protected final static int DEFAULT_DEATHMATCH_SHOTGUN_COUNT		= 12;
	protected final static int DEFAULT_SHOTGUN_COUNT				= 12;
	protected final static int DEFAULT_SSHOTGUN_COUNT				= 20;	

	protected final static int WEAPON_UNUSED		= 0;
	protected final static int WEAPON_READY			= 1;
	protected final static int WEAPON_ACTIVATING	= 2;
	protected final static int WEAPON_DROPPING		= 3;
	protected final static int WEAPON_FIRING		= 4;	
	
/**
 * Construct a weapon without any map spawn arguments.
 */
public GenericWeapon() 
	{
	super();
	setFields();

	// fill the weapon with ammo
	setAmmoCount(getDefaultAmmoCount());
	
	// precache VWep
	fVWepIndex = Engine.getVWepIndex(getIconName());
	}
/**
 * Construct a weapon with map spawn arguments
 */
public GenericWeapon(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	setFields();

	// fill the weapon with ammo
	setAmmoCount(getDefaultAmmoCount());
	
	// precache VWep
	fVWepIndex = Engine.getVWepIndex(getIconName());
	
	// precache weapon sound
	String ws = getWeaponSound();
	if (ws != null)
		Engine.getSoundIndex(ws);

	setupEntity();
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
	fPlayer.setAmmoType(getAmmoName());

	// ask to be called back if the player dies
	fPlayer.addPlayerStateListener(this);
	}
/**
 * This method was created by a SmartGuide.
 */
public void deactivate() 
	{
	fIsSwitching = true;

	// not interested anymore if the player dies
	fPlayer.removePlayerStateListener(this);	
	}
/**
 * This method was created by a SmartGuide.
 */
public abstract void fire();
/**
 * Get the name of the type of ammo this weapon uses.
 * @return Name of kind of ammo, may be null if the 
 *   weapon doesn't use ammo.
 */
public String getAmmoName() 
	{
	return null;
	}
/**
 * Get how much ammo this weapon starts off with.
 * @return int
 */
public int getDefaultAmmoCount()
	{
	return 0;
	}
/**
 * Maybe this method should be in GenericWeapon.java...
 * (yep, Menno was right)
 */
public q2java.baseq2.Player getOwner()
	{
	return fPlayer;
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
 * Get the name of the model used for displaying from the player's POV.
 * @return java.lang.String
 */
public abstract String getViewModelName();
/**
 * Get the VWep index of this weapon.
 * @return int
 */
public int getVWepIndex() 
	{
	return fVWepIndex;
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
 * Get the player sound that will be used
 * when carrying weapon, most weapons don't have one.
 * @return name of sound, null if no sound
 */
public String getWeaponSound() 
	{
	return null;
	}
/**
 * This method was created by a SmartGuide.
 */
public final void incWeaponFrame() 
	{
	fEntity.setPlayerGunFrame(++fGunFrame);
	}
/**
 * Find out whether this weapon auto-switches away when ammo runs out.
 * @return boolean
 */
public boolean isAutoSwitch() 
	{
	return fIsAutoSwitch;
	}
/**
 * Check whether this weapon has enough ammo to fire.  One unit of
 * ammo is good enough for most weapons.  Special weapons will 
 * override this.
 * @return boolean
 */
public boolean isEnoughAmmo() 
	{
	String ammo = getAmmoName();
	if (ammo == null)
		return true;
	else		
		return (fPlayer.getAmmoCount(ammo) >= 1);
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
 * Called when a player dies or disconnects.
 * @param wasDisconnected true on disconnects, false on normal deaths.
 */
public void playerStateChanged(PlayerStateEvent pse)
	{
	switch (pse.getStateChanged())	
		{		
		case PlayerStateEvent.STATE_SUSPENDEDSTART:	
			fPlayer.removePlayerStateListener(this);
			break;
			
		case PlayerStateEvent.STATE_DEAD:
		case PlayerStateEvent.STATE_INVALID:
			fPlayer.removePlayerStateListener(this);
			if (isDroppable())
				{
				Player p = pse.getPlayer();
				setAmmoCount(getDefaultAmmoCount());
				p.removeInventory(getItemName()); // not really necessary, but you never know
				drop(p, GenericItem.DROP_TIMEOUT);
				}		
			break;
		}
	
	}
/**
 * Make sure a weapon's VWep skin is precached.  Most useful
 * for weapons that aren't spawned in maps, like the blaster
 * and grapple hook.
 *
 * @param weaponClassName java.lang.String
 */
public static void precacheVWep(String weaponClassSuffix) 
	{
	try
		{
		GenericWeapon gw = (GenericWeapon) Game.lookupClass(weaponClassSuffix).newInstance();
		}
	catch (Exception e)
		{
		// no weapon by this name? oh well, don't raise a stink about it.
		}
	}
/**
 * Set whether this weapon automatically switches away when ammo is too low.
 * @param b boolean
 */
public void setAutoSwitch(boolean b) 
	{
	fIsAutoSwitch = b;
	}
/**
 * Fill in the info specific to this type of weapon.
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
 * Setup this item's NativeEntity.
 */
public void setupEntity() 
	{
	super.setupEntity();
	fEntity.setEffects(NativeEntity.EF_ROTATE); // all weapons rotate
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
			fEntity.setPlayerGunIndex(Engine.getModelIndex(getViewModelName()));
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
				Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, Engine.getSoundIndex("weapons/noammo.wav"), 1, NativeEntity.ATTN_NORM, 0);
				if (fIsAutoSwitch)
  					deactivate();
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
						if ((GameUtil.randomInt() & 15) != 0)
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