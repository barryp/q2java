
package q2jgame.weapon;

import q2java.*;

public class Hyperblaster extends GenericBlaster
	{
	// all hyperblaster objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {0};
	private static int[] FIRE_FRAMES = new int[] {6, 7, 8, 9, 10, 11, 0};		
	
public Hyperblaster() throws GameException
	{
	super("cells", "models/weapons/v_hyperb/tris.md2",
		5, 20, 49, 53, PAUSE_FRAMES, FIRE_FRAMES, 
		NativeEntity.EF_HYPERBLASTER, 20, Engine.MZ_HYPERBLASTER);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	float rotation;
	int weaponSound = Engine.soundIndex("weapons/hyprbl1a.wav");

	if (!fOwner.isAttacking())
		incWeaponFrame();
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
			rotation = (float)((getWeaponFrame() - 5) * (Math.PI / 3));
			fBlasterOffset.x = (float)(-4 * Math.sin(rotation));
			fBlasterOffset.y = 0.0F;
			fBlasterOffset.z = (float)(4 * Math.cos(rotation));

			if ((getWeaponFrame() == 6) || (getWeaponFrame() == 9))			
				fEffect = NativeEntity.EF_HYPERBLASTER;
			else
				fEffect = 0;

			super.fire();				
//			ent->client->pers.inventory[ent->client->ammo_index] -= ent->client->pers.weapon->quantity;
			}

		incWeaponFrame();
		if (getWeaponFrame() == 12)
			setWeaponFrame(6);
		}

	if (getWeaponFrame() == 12)
		{
		fOwner.sound(NativeEntity.CHAN_AUTO, Engine.soundIndex("weapons/hyprbd1a.wav"), 1, NativeEntity.ATTN_NORM, 0);
//		ent->client->weapon_sound = 0;
		}		
	}
}