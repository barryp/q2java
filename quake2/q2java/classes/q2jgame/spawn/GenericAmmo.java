
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
public void touch(Player p) 
	{	
	// don't do anything if the player is already maxed out on this ammo
	if (p.getAmmoCount(fAmmoType) >= p.getMaxAmmoCount(fAmmoType))
		return;
		
	super.touch(p);

	p.addAmmo(fAmmoType, fCount);

	// bring the ammo back in 30 seconds
	setRespawn(30);	
	}
}