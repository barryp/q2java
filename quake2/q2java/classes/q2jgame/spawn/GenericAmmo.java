
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

abstract class GenericAmmo extends GenericItem
	{
	String fAmmoType;
	int fCount;
	
public GenericAmmo(String[] spawnArgs, String ammoType, int count, String modelName) throws GameException
	{
	super(spawnArgs, "misc/am_pkup.wav");
	setModel(modelName);
	fAmmoType = ammoType;
	fCount = count;
	linkEntity();
	}
/**
 * This method was created by a SmartGuide.
 * @param p q2jgame.Player
 */
public void touch(GenericCharacter mob) 
	{
	super.touch(mob);

	// bring the ammo back in 30 seconds
	setRespawn(30);
	
	if (mob instanceof Player)
		((Player)mob).addAmmo(fAmmoType, fCount);
	}
}