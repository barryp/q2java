
package q2jgame;

import q2java.*;

public class GenericWeapon extends GenericItem
	{
	public int fWeaponState;
	
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
	
	public final static int WEAPON_READY			= 0;
	public final static int WEAPON_ACTIVATING	= 1;
	public final static int WEAPON_DROPPING		= 2;
	public final static int WEAPON_FIRING		= 3;
	
public GenericWeapon(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setEffects(EF_ROTATE); // all weapons rotate
	}
/**
 * This method was created by a SmartGuide.
 */
public void activate() 
	{
	fWeaponState = WEAPON_ACTIVATING;
	((Player)getOwner()).setGunFrame(fGunFrame = 0);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame() 
	{
	GenericCharacter mob = (GenericCharacter) getOwner();

	// if this isn't a weapon that's being wielded then bail
	if ((mob == null) || (mob.fWeapon != this))
		return;

	if (fWeaponState == WEAPON_DROPPING)
		{
		if (fGunFrame < fFrameDeactivateLast)
			mob.setGunFrame(++fGunFrame);
		else			
			{
			mob.fWeapon = mob.fNextWeapon;
			mob.fNextWeapon = null;
			mob.fWeapon.activate();
			}		
		return;
		}

	if (fWeaponState == WEAPON_ACTIVATING)
		{
		if (fGunFrame  < fFrameActivateLast)
			mob.setGunFrame(++fGunFrame);
		else
			{
			fWeaponState = WEAPON_READY;
			mob.setGunFrame(fGunFrame = fFrameFireLast + 1); // FRAME_IDLE_FIRST = FRAME_FIRE_LAST + 1
			}
		return;
		}


	if ((mob.fNextWeapon != null) && (fWeaponState != WEAPON_FIRING))
		{
		fWeaponState = WEAPON_DROPPING;
		mob.setGunFrame(fGunFrame = fFrameIdleLast + 1); // FRAME_DEACTIVATE_FIRST = FRAME_IDLE_LAST + 1
		return;
		}

	if (fWeaponState == WEAPON_READY)
		{
		if ((Game.randomInt() & 63) == 0)
			{
			fWeaponState = WEAPON_FIRING;
			mob.setGunFrame(fGunFrame = fFrameActivateLast + 1); // FRAME_FIRE_FIRST = FRAME_ACTIVATE_LAST + 1
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
				mob.setGunFrame(fGunFrame = fFrameFireLast + 1); // FRAME_IDLE_FIRST = FRAME_IDLE_LAST + 1
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
			mob.setGunFrame(++fGunFrame);
			return;
			}
			
		}

	if (fWeaponState == WEAPON_FIRING)
		{
		Game.debugLog("GenericWeapon.runFrame() fWeaponState == WEAPON_FIRING, fGunFrame == " + fGunFrame);
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
			mob.setGunFrame(++fGunFrame);

		if (fGunFrame == fFrameFireLast + 2) // FRAME_IDLE_FIRST = FRAME_FIRE_LAST + 1
			fWeaponState = WEAPON_READY;
		}
		
	}
}