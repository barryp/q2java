
package q2jgame;

import q2java.*;

public abstract class GenericWeapon extends GenericItem
	{
	protected Player fOwner;
	
	// keep our own copy of the animation frame, so
	// we don't have to go back and forth to C so much
	private int fGunFrame;

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
	
public GenericWeapon(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setEffects(EF_ROTATE); // all weapons rotate
	}
/**
 * This method was created by a SmartGuide.
 */
public GenericWeapon(int weaponModel, int lastActivate, int lastFire, int lastIdle, int lastDeactivate, int[] pauseFrames, int[] fireFrames) throws GameException
	{
	super(null);
	
	// animation settings
	fWeaponModel = weaponModel;
	fFrameActivateLast = lastActivate;
	fFrameFireLast = lastFire;
	fFrameIdleLast = lastIdle;
	fFrameDeactivateLast = lastDeactivate;
	fPauseFrames = pauseFrames;
	fFireFrames = fireFrames;
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public final int currentWeaponFrame() 
	{
	return fGunFrame;
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
 */
public final void incWeaponFrame() 
	{
	fOwner.setGunFrame(++fGunFrame);
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame() 
	{
	if (fWeaponState == WEAPON_UNUSED)
		return;
		
	if (fWeaponState == WEAPON_DROPPING)
		{
		if (fGunFrame < fFrameDeactivateLast)
			incWeaponFrame();
		else			
			{
			fWeaponState = WEAPON_UNUSED;
			fIsSwitching = false;
			setWeaponFrame(0);
			fOwner.changeWeapon();
			}
		return;
		}

	if (fWeaponState == WEAPON_ACTIVATING)
		{
		if (fIsSwitching)
			{
			fOwner.setGunIndex(fWeaponModel);
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
		if ((fOwner.fButtons & Player.BUTTON_ATTACK) != 0)
			{
			fWeaponState = WEAPON_FIRING;
			setWeaponFrame(fFrameActivateLast + 1); // FRAME_FIRE_FIRST = FRAME_ACTIVATE_LAST + 1
			}
		else
/*		
		if ( ((ent->client->latched_buttons|ent->client->buttons) & BUTTON_ATTACK) )
			{
			ent->client->latched_buttons &= ~BUTTON_ATTACK;
			if ((!ent->client->ammo_index) || 
				( ent->client->pers.inventory[ent->client->ammo_index] >= ent->client->pers.weapon->quantity))
				{
				ent->client->ps.gunframe = FRAME_FIRE_FIRST;
				ent->client->weaponstate = WEAPON_FIRING;

				// start the animation
				ent->client->anim_priority = ANIM_ATTACK;
				if (ent->client->ps.pmove.pm_flags & PMF_DUCKED)
					{
					ent->s.frame = FRAME_crattak1-1;
					ent->client->anim_end = FRAME_crattak9;
					}
				else
					{
					ent->s.frame = FRAME_attack1-1;
					ent->client->anim_end = FRAME_attack8;
					}
				}
			else
				{
				if (level.time >= ent->pain_debounce_time)
					{
					gi.sound(ent, CHAN_VOICE, gi.soundindex("weapons/noammo.wav"), 1, ATTN_NORM, 0);
					ent->pain_debounce_time = level.time + 1;
					}
				NoAmmoWeaponChange (ent);
				}
			}
		else
*/		
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
/**
 * This method was created by a SmartGuide.
 */
public final void setWeaponFrame(int newFrame) 
	{
	fGunFrame = newFrame;
	fOwner.setGunFrame(fGunFrame);
	}
/**
 * This method was created by a SmartGuide.
 */
public void use(Player p) 
	{
	fIsSwitching = true;
	fWeaponState = WEAPON_ACTIVATING;
	fOwner = p;
	setWeaponFrame(0);
	}
}