
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;
import q2jgame.weapon.*;

abstract class GenericWeapon extends GenericItem
	{
	private String fClassName;
	private String fWeaponName;
	private String fAmmoName;
	private int fAmmoCount;
	
public GenericWeapon(String[] spawnArgs, String className, String weaponName, String ammoName, int ammoCount, String modelName) throws GameException
	{
	super(spawnArgs, "misc/w_pkup.wav");
	fClassName = className;
	fWeaponName = weaponName;
	fAmmoName = ammoName;
	fAmmoCount = ammoCount;
	setModel(modelName);
	setEffects(EF_ROTATE); // all weapons rotate
	linkEntity();
	}
/**
 * This method was created by a SmartGuide.
 * @param mob q2jgame.GenericCharacter
 */
public void touch(Player p) 
	{
	// don't do anything if the player is already maxed out on this weapon and ammo
	if (p.isCarrying(fWeaponName) && (p.getAmmoCount(fAmmoName) >= p.getMaxAmmoCount(fAmmoName)))
		return;
			
	super.touch(p);
	
	// bring the weapon back in 30 seconds
	setRespawn(30);	
	
	if (!p.isCarrying(fWeaponName))
		{
		try
			{
			PlayerWeapon w = (PlayerWeapon) Class.forName(fClassName).newInstance();
			p.putInventory(fWeaponName, w);
			w.setOwner(p);
			p.cprint(Engine.PRINT_HIGH, "You picked up a " + fWeaponName + "\n");
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}				
		}

	p.addAmmo(fAmmoName, fAmmoCount);			
	}

}