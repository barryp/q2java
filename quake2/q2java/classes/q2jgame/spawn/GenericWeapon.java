
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
	if (p.addWeapon(fWeaponName, fClassName, fAmmoName, fAmmoCount))
		{
		super.touch(p);
	
		// bring the weapon back in 30 seconds
		setRespawn(30);	
		}
	}

}