
package q2jgame;

import q2java.*;

public class weapon_hyperblaster extends GenericBlaster
	{
	// all hyperblaster objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {0};
	private static int[] FIRE_FRAMES = new int[] {6, 7, 8, 9, 10, 11, 0};		
	
public weapon_hyperblaster(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/weapons/g_hyperb/tris.md2");
	linkEntity();
	}
public weapon_hyperblaster(GenericCharacter mob) throws GameException
	{
	super(null);
	fFrameActivateLast = 5;
	fFrameFireLast = 20;
	fFrameIdleLast = 49;
	fFrameDeactivateLast = 53;
	fPauseFrames = PAUSE_FRAMES;
	fFireFrames = FIRE_FRAMES;

	fEffect = EF_HYPERBLASTER;
	fDamage = 20;
	fBlasterOffset = new Vec3(0, 0, 0);
	fMuzzleFlash = Engine.MZ_HYPERBLASTER;
	
	setOwner(mob);
	}
/**
 * This method was created by a SmartGuide.
 */
public void activate() 
	{
	super.activate();
	((Player)getOwner()).setGunIndex(Engine.modelIndex("models/weapons/v_hyperb/tris.md2"));
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	float rotation;
	Player mob = (Player) getOwner();
		
	int weaponSound = Engine.soundIndex("weapons/hyprbl1a.wav");

	if ((mob.fButtons & Player.BUTTON_ATTACK) == 0)
		((Player)getOwner()).setGunFrame(++fGunFrame);
	else
	
		{
/*
		if (! ent->client->pers.inventory[ent->client->ammo_index] )
			{
			if (level.time >= ent->pain_debounce_time)
				{
				gi.sound(ent, CHAN_VOICE, gi.soundindex("weapons/noammo.wav"), 1, ATTN_NORM, 0);
				ent->pain_debounce_time = level.time + 1;
				}
			NoAmmoWeaponChange (ent);
			}
		else
*/		
			{
			rotation = (float)((fGunFrame - 5) * (Math.PI / 3));
			fBlasterOffset.x = (float)(-4 * Math.sin(rotation));
			fBlasterOffset.y = 0.0F;
			fBlasterOffset.z = (float)(4 * Math.cos(rotation));

			if ((fGunFrame == 6) || (fGunFrame == 9))			
				fEffect = EF_HYPERBLASTER;
			else
				fEffect = 0;

			super.fire();				
//			ent->client->pers.inventory[ent->client->ammo_index] -= ent->client->pers.weapon->quantity;
			}

		((Player)getOwner()).setGunFrame(++fGunFrame);
		if (fGunFrame == 12)
			((Player)getOwner()).setGunFrame(fGunFrame = 6);
		}

	if (fGunFrame == 12)
		{
		getOwner().sound(CHAN_AUTO, Engine.soundIndex("weapons/hyprbd1a.wav"), 1, ATTN_NORM, 0);
//		ent->client->weapon_sound = 0;
		}		
	}
}